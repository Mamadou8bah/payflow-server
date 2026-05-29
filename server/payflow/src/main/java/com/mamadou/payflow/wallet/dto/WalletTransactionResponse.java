package com.mamadou.payflow.wallet.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WalletTransactionResponse {
    private Long id;
    private Long walletId;
    private BigDecimal amount;
    private String transactionType;
    private String description;
    private String reference;
    private LocalDateTime createdAt;
}
