package com.mamadou.payflow.admin.dto;

import java.util.Map;

public record AdminDashboardResponse(
    long totalWallets,
    long activeWallets,
    long frozenWallets,
    long totalTransactions,
    long failedTransactions,
    long pendingTransactions,
    long totalRiskFlags,
    long criticalRiskFlags,
    long unresolvedReconciliationMismatches,
    long failedOperations,
    Map<String, Long> operationCounts,
    Map<String, Object> systemHealth
) {}
