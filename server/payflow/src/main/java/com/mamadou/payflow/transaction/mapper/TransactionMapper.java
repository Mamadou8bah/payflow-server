package com.mamadou.payflow.transaction.mapper;

import com.mamadou.payflow.ledger.dto.LedgerPostingResponse;
import com.mamadou.payflow.transaction.dto.TransactionDetailResponse;
import com.mamadou.payflow.transaction.dto.TransactionResponse;
import com.mamadou.payflow.transaction.entity.Transaction;
import com.mamadou.payflow.transaction.enums.TransactionStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .reference(transaction.getReference())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .sourceWalletId(walletId(transaction.getSourceWallet()))
                .destinationWalletId(walletId(transaction.getDestinationWallet()))
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .description(transaction.getDescription())
                .reversible(isReversible(transaction))
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }

    public TransactionDetailResponse toDetailResponse(
            Transaction transaction,
            List<LedgerPostingResponse> ledgerPostings
    ) {
        return TransactionDetailResponse.builder()
                .id(transaction.getId())
                .reference(transaction.getReference())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .sourceWalletId(walletId(transaction.getSourceWallet()))
                .destinationWalletId(walletId(transaction.getDestinationWallet()))
                .initiatedById(transaction.getInitiatedBy() == null ? null : transaction.getInitiatedBy().getId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .description(transaction.getDescription())
                .ledgerTraceId(transaction.getLedgerTraceId())
                .failureReason(transaction.getFailureReason())
                .parentTransactionId(transaction.getParentTransaction() == null ? null : transaction.getParentTransaction().getId())
                .reversalTransactionId(transaction.getReversalTransaction() == null ? null : transaction.getReversalTransaction().getId())
                .reversedAt(transaction.getReversedAt())
                .reversible(isReversible(transaction))
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .ledgerPostings(ledgerPostings)
                .build();
    }

    private Long walletId(com.mamadou.payflow.wallet.entity.Wallet wallet) {
        return wallet == null ? null : wallet.getId();
    }

    private boolean isReversible(Transaction transaction) {
        return transaction.getStatus() == TransactionStatus.COMPLETED
                && transaction.getReversalTransaction() == null
                && transaction.getReversedAt() == null;
    }
}
