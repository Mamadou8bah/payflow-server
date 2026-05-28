package com.mamadou.payflow.webhook.service;

import com.mamadou.payflow.webhook.validator.WebhookSignatureValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebhookValidationService {

    private final WebhookSignatureValidator webhookSignatureValidator;

    @Value("${payflow.webhooks.modempay.signature-header:X-ModemPay-Signature}")
    private String signatureHeader;

    public boolean hasValidSignature(String rawPayload, HttpHeaders headers) {
        return webhookSignatureValidator.isValid(rawPayload, headers.getFirst(signatureHeader));
    }
}
