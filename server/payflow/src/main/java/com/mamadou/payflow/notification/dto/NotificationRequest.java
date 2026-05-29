package com.mamadou.payflow.notification.dto;

import java.util.Map;

public record NotificationRequest(
    String recipient,
    String notificationType,
    String subject,
    String body,
    Map<String, Object> metadata
) {}
