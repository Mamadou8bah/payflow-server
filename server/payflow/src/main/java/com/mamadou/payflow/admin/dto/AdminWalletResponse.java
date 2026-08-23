package com.mamadou.payflow.admin.dto;

import com.mamadou.payflow.wallet.enums.WalletStatus;

import java.math.BigDecimal;

public record AdminWalletResponse(
        Long id,
        String name,
        String currency,
        WalletStatus status,
        Long ownerId,
        String ownerEmail,
        BigDecimal balance
) {}
