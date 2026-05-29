package com.mamadou.payflow.webhook.client;

import com.mamadou.payflow.webhook.config.ModemPayConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModemPayClient {

    private final ModemPayConfig config;
    private final RestTemplate restTemplate = new RestTemplate();

    public CreateChargeResponse createCharge(CreateChargeRequest req) {
        String url = config.getApiUrl().replaceAll("/+$", "") + "/v1/charges";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (config.getApiKey() != null) {
            headers.set("Authorization", "Bearer " + config.getApiKey());
        }
        if (req.getIdempotencyKey() != null) {
            headers.set("Idempotency-Key", req.getIdempotencyKey());
        }

        HttpEntity<CreateChargeRequest> entity = new HttpEntity<>(req, headers);

        try {
            ResponseEntity<CreateChargeResponse> resp = restTemplate.exchange(url, HttpMethod.POST, entity, CreateChargeResponse.class);
            return resp.getBody();
        } catch (Exception ex) {
            log.error("Failed to create ModemPay charge: {}", ex.getMessage(), ex);
            throw new RuntimeException("ModemPay createCharge failed", ex);
        }
    }

    public CreatePayoutResponse createPayout(CreatePayoutRequest req) {
        String url = config.getApiUrl().replaceAll("/+$", "") + "/v1/payouts";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (config.getApiKey() != null) {
            headers.set("Authorization", "Bearer " + config.getApiKey());
        }
        if (req.getIdempotencyKey() != null) {
            headers.set("Idempotency-Key", req.getIdempotencyKey());
        }

        HttpEntity<CreatePayoutRequest> entity = new HttpEntity<>(req, headers);

        try {
            ResponseEntity<CreatePayoutResponse> resp = restTemplate.exchange(url, HttpMethod.POST, entity, CreatePayoutResponse.class);
            return resp.getBody();
        } catch (Exception ex) {
            log.error("Failed to create ModemPay payout: {}", ex.getMessage(), ex);
            throw new RuntimeException("ModemPay createPayout failed", ex);
        }
    }
}
