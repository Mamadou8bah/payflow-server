package com.mamadou.payflow.transfer.dto;

import com.mamadou.payflow.transaction.entity.Transaction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransferListItem {
    private Long id;
    private Long from;
    private Long to;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String reference;
    private LocalDateTime time;

    public static TransferListItem from(Transaction transaction) {
        return TransferListItem.builder()
                .id(transaction.getId())
                .from(transaction.getSourceWallet() != null ? transaction.getSourceWallet().getId() : null)
                .to(transaction.getDestinationWallet() != null ? transaction.getDestinationWallet().getId() : null)
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .status(transaction.getStatus().name())
                .reference(transaction.getReference())
                .time(transaction.getCreatedAt())
                .build();
    }
}
