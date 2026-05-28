package com.mamadou.payflow.transaction.service;

import com.mamadou.payflow.transaction.entity.Transaction;
import com.mamadou.payflow.transaction.enums.TransactionStatus;
import com.mamadou.payflow.transaction.exception.TransactionException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionStateService {

    public void markCompleted(Transaction transaction, String ledgerTraceId) {
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setLedgerTraceId(ledgerTraceId);
        transaction.setFailureReason(null);
    }

    public void markFailed(Transaction transaction, String failureReason) {
        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setFailureReason(failureReason);
    }

    public void markReversed(Transaction original, Transaction reversal) {
        if (original.getStatus() != TransactionStatus.COMPLETED) {
            throw new TransactionException("Only completed transactions can be reversed");
        }
        if (original.getReversalTransaction() != null || original.getReversedAt() != null) {
            throw new TransactionException("Transaction has already been reversed");
        }
        original.setStatus(TransactionStatus.REVERSED);
        original.setReversalTransaction(reversal);
        original.setReversedAt(LocalDateTime.now());
    }
}
