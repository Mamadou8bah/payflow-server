package com.mamadou.payflow.reconciliation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReconciliationMismatchResponse(
    Long id,
    Long reportId,
    String mismatchType,
    Long entityId,
    String entityType,
    String expectedValue,
    String actualValue,
    BigDecimal variance,
    String description,
    boolean resolved,
    String resolutionAction,
    LocalDateTime resolvedAt,
    String resolvedBy,
    LocalDateTime createdAt
) {}
