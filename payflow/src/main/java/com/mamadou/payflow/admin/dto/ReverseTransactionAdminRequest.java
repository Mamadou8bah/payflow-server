package com.mamadou.payflow.admin.dto;

public record ReverseTransactionAdminRequest(
    Long transactionId,
    String reason,
    boolean refundToWallet
) {}
