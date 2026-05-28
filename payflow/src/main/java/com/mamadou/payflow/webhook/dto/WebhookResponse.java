package com.mamadou.payflow.webhook.dto;

public record WebhookResponse(
        boolean accepted,
        Long eventId,
        String status,
        String message
) {
}
