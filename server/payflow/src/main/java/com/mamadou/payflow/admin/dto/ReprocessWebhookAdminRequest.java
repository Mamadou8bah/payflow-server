package com.mamadou.payflow.admin.dto;

public record ReprocessWebhookAdminRequest(
    Long webhookId,
    String reason
) {}
