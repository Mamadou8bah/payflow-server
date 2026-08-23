package com.mamadou.payflow.admin.dto;

import java.time.LocalDateTime;

public record AuditLogEntryResponse(
        Long id,
        Long actorId,
        String actorEmail,
        LocalDateTime timestamp,
        String actionType,
        String entityType,
        Long entityId,
        String changeDescription,
        boolean success
) {}
