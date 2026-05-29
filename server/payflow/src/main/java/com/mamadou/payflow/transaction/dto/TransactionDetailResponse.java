package com.mamadou.payflow.transaction.dto;

import com.mamadou.payflow.ledger.dto.LedgerPostingResponse;
import com.mamadou.payflow.transaction.enums.TransactionStatus;
import com.mamadou.payflow.transaction.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TransactionDetailResponse {
    private Long id;
    private String reference;
    private TransactionType type;
    private TransactionStatus status;
    private Long sourceWalletId;
    private Long destinationWalletId;
    private Long initiatedById;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String ledgerTraceId;
    private String failureReason;
    private Long parentTransactionId;
    private Long reversalTransactionId;
    private LocalDateTime reversedAt;
    private boolean reversible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<LedgerPostingResponse> ledgerPostings;
}
