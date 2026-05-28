package com.mamadou.payflow.transaction.service;

import com.mamadou.payflow.transaction.dto.ReversalRequest;
import com.mamadou.payflow.transaction.dto.TransactionDetailResponse;
import com.mamadou.payflow.transaction.dto.TransactionResponse;
import com.mamadou.payflow.transaction.entity.Transaction;
import com.mamadou.payflow.transaction.enums.TransactionStatus;
import com.mamadou.payflow.transaction.enums.TransactionType;
import com.mamadou.payflow.transaction.exception.TransactionException;
import com.mamadou.payflow.transaction.mapper.TransactionMapper;
import com.mamadou.payflow.transaction.repository.TransactionRepository;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.wallet.entity.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionStateService transactionStateService;
    private final TransactionQueryService transactionQueryService;
    private final TransactionMapper transactionMapper;

    @Transactional
    public Transaction createPendingTransfer(
            Wallet sourceWallet,
            Wallet destinationWallet,
            User initiatedBy,
            BigDecimal amount,
            String currency,
            String description,
            String reference
    ) {
        return transactionRepository.save(Transaction.builder()
                .reference(resolveReference(reference, "txn_"))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .sourceWallet(sourceWallet)
                .destinationWallet(destinationWallet)
                .initiatedBy(initiatedBy)
                .amount(amount)
                .currency(currency)
                .description(description)
                .build());
    }

    @Transactional
    public Transaction createCompletedWalletMovement(
            Wallet wallet,
            User initiatedBy,
            BigDecimal amount,
            String currency,
            String description,
            String reference,
            String ledgerTraceId,
            TransactionType type
    ) {
        Transaction transaction = Transaction.builder()
                .reference(resolveReference(reference, "wallet_"))
                .type(type)
                .status(TransactionStatus.COMPLETED)
                .sourceWallet(type == TransactionType.WALLET_DEBIT ? wallet : null)
                .destinationWallet(type == TransactionType.WALLET_CREDIT ? wallet : null)
                .initiatedBy(initiatedBy)
                .amount(amount)
                .currency(currency)
                .description(description)
                .ledgerTraceId(ledgerTraceId)
                .build();
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction markCompleted(Transaction transaction, String ledgerTraceId) {
        transactionStateService.markCompleted(transaction, ledgerTraceId);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction markFailed(Transaction transaction, String failureReason) {
        transactionStateService.markFailed(transaction, failureReason);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction createPendingReversal(
            Transaction original,
            User initiatedBy,
            String reason,
            String reference
    ) {
        return transactionRepository.save(Transaction.builder()
                .reference(resolveReference(reference, "rev_"))
                .type(TransactionType.TRANSFER_REVERSAL)
                .status(TransactionStatus.PENDING)
                .sourceWallet(original.getDestinationWallet())
                .destinationWallet(original.getSourceWallet())
                .initiatedBy(initiatedBy)
                .amount(original.getAmount())
                .currency(original.getCurrency())
                .description(reason)
                .parentTransaction(original)
                .build());
    }

    @Transactional
    public Transaction completeReversal(Transaction original, Transaction reversal, String ledgerTraceId) {
        transactionStateService.markCompleted(reversal, ledgerTraceId);
        transactionRepository.save(reversal);
        transactionStateService.markReversed(original, reversal);
        transactionRepository.save(original);
        return reversal;
    }

    @Transactional(readOnly = true)
    public Transaction getEntity(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionException("Transaction not found"));
    }

    public TransactionResponse toResponse(Transaction transaction) {
        return transactionMapper.toResponse(transaction);
    }

    public TransactionDetailResponse getDetail(Long id) {
        return transactionQueryService.getById(id);
    }

    private String resolveReference(String reference, String prefix) {
        if (reference == null || reference.isBlank()) {
            return prefix + UUID.randomUUID();
        }
        String normalizedReference = reference.trim();
        if (transactionRepository.existsByReference(normalizedReference)) {
            throw new TransactionException("Transaction reference already exists");
        }
        return normalizedReference;
    }
}
