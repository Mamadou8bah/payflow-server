package com.mamadou.payflow.transaction.dto;

import com.mamadou.payflow.transaction.enums.TransactionStatus;
import com.mamadou.payflow.transaction.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private Long id;
    private String reference;
    private TransactionType type;
    private TransactionStatus status;
    private Long sourceWalletId;
    private Long destinationWalletId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private boolean reversible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
