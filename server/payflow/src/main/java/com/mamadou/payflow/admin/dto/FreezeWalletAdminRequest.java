package com.mamadou.payflow.admin.dto;

import java.math.BigDecimal;

public record FreezeWalletAdminRequest(
    Long walletId,
    String reason,
    boolean permanent
) {}
