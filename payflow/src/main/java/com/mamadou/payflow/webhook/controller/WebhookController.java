package com.mamadou.payflow.webhook.controller;

import com.mamadou.payflow.webhook.dto.WebhookReplayRequest;
import com.mamadou.payflow.webhook.dto.WebhookResponse;
import com.mamadou.payflow.webhook.service.WebhookRetryService;
import com.mamadou.payflow.webhook.service.WebhookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;
    private final WebhookRetryService webhookRetryService;

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
}
