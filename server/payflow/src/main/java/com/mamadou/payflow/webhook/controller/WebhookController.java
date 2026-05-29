package com.mamadou.payflow.webhook.controller;

import com.mamadou.payflow.webhook.dto.WebhookReplayRequest;
import com.mamadou.payflow.webhook.dto.WebhookResponse;
import com.mamadou.payflow.webhook.dto.ModemPayWebhookPayload;
import com.mamadou.payflow.webhook.service.WebhookRetryService;
import com.mamadou.payflow.webhook.service.WebhookService;
import com.mamadou.payflow.webhook.service.ModemPayWebhookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final WebhookService webhookService;
    private final WebhookRetryService webhookRetryService;
    private final ModemPayWebhookService modemPayWebhookService;

    @PostMapping("/modempay/deposits")
    public ResponseEntity<WebhookResponse> modemPayDeposit(
            @RequestBody String rawPayload,
            @RequestHeader HttpHeaders headers
    ) {
        WebhookResponse response = webhookService.handleModemPayDeposit(rawPayload, headers);
        HttpStatus status = response.accepted() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        if ("FAILED".equals(response.status())) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
        }
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/replay")
    public ResponseEntity<WebhookResponse> replay(@Valid @RequestBody WebhookReplayRequest request) {
        WebhookResponse response = webhookRetryService.replay(request.webhookEventId());
        return ResponseEntity.status(response.accepted() ? HttpStatus.OK : HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    /**
     * Receive and process Modem Pay webhook events (charge.succeeded, charge.failed, etc.)
     * 
     * Endpoint: POST /api/v1/webhooks/modem-pay/charges
     * 
     * Configure this URL in Modem Pay dashboard webhooks settings:
     * https://yourdomain.com/api/v1/webhooks/modem-pay/charges
     * 
     * Events: charge.succeeded, charge.failed, charge.cancelled, charge.pending
     */
    @PostMapping("/modem-pay/charges")
    public ResponseEntity<ModemPayWebhookResponse> handleModemPayChargeWebhook(
            @RequestBody ModemPayWebhookPayload payload,
            @RequestHeader(value = "X-ModemPay-Signature", required = false) String signature) {

        try {
            log.info("Received Modem Pay webhook - Event: {}, Charge ID: {}", 
                    payload.getEvent(), payload.getData().getChargeId());

            // Validate signature
            if (signature == null || signature.isEmpty()) {
                log.warn("Missing webhook signature header for Modem Pay webhook");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ModemPayWebhookResponse("INVALID_SIGNATURE", "Missing signature"));
            }

            // Process webhook
            modemPayWebhookService.processWebhook(payload, signature);

            log.info("Modem Pay webhook processed successfully - Event: {}", payload.getEvent());
            return ResponseEntity.ok(
                    new ModemPayWebhookResponse("SUCCESS", "Webhook processed successfully")
            );

        } catch (IllegalArgumentException e) {
            log.warn("Invalid Modem Pay webhook: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ModemPayWebhookResponse("INVALID_SIGNATURE", e.getMessage()));
        } catch (Exception e) {
            log.error("Error processing Modem Pay webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ModemPayWebhookResponse("ERROR", "Failed to process webhook: " + e.getMessage()));
        }
    }

    /**
     * Health check endpoint for Modem Pay to verify webhook URL is accessible
     */
    @GetMapping("/modem-pay/health")
    public ResponseEntity<ModemPayWebhookResponse> webhookHealth() {
        return ResponseEntity.ok(
                new ModemPayWebhookResponse("OK", "Webhook service is healthy and ready")
        );
    }

    /**
     * Modem Pay webhook response DTO
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ModemPayWebhookResponse {
        private String status;
        private String message;
    }
}
