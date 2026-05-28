package com.mamadou.payflow.reconciliation.dto;

import com.mamadou.payflow.reconciliation.enums.ReconciliationType;

public record ManualReconciliationRequest(
    ReconciliationType reconciliationType,
    String reason,
    Long walletId
) {}
