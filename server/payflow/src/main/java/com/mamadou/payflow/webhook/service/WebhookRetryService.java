package com.mamadou.payflow.webhook.service;

import com.mamadou.payflow.webhook.dto.WebhookResponse;
import com.mamadou.payflow.webhook.entity.WebhookEvent;
import com.mamadou.payflow.webhook.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WebhookRetryService {

    private final WebhookEventRepository webhookEventRepository;
    private final WebhookService webhookService;

    @Transactional
    public WebhookResponse replay(Long webhookEventId) {
        WebhookEvent event = webhookEventRepository.findById(webhookEventId)
                .orElseThrow(() -> new IllegalArgumentException("Webhook event not found"));

        if ("PROCESSED".equals(event.getStatus()) || "IGNORED".equals(event.getStatus())) {
            return new WebhookResponse(true, event.getId(), event.getStatus(), "Webhook already handled");
        }
        if ("SIGNATURE_FAILED".equals(event.getStatus())) {
            return new WebhookResponse(false, event.getId(), event.getStatus(), "Cannot replay a signature-failed webhook");
        }

        return webhookService.processStoredEvent(event);
    }
}
