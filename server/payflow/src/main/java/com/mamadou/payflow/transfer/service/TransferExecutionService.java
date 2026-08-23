package com.mamadou.payflow.transfer.service;

import com.mamadou.payflow.ledger.dto.LedgerPostingRequest;
import com.mamadou.payflow.ledger.dto.LedgerTraceResponse;
import com.mamadou.payflow.ledger.enums.LedgerPostingSide;
import com.mamadou.payflow.ledger.service.LedgerService;
import com.mamadou.payflow.risk.service.RiskEngineService;
import com.mamadou.payflow.transaction.entity.Transaction;
import com.mamadou.payflow.transaction.service.TransactionService;
import com.mamadou.payflow.transfer.dto.TransferRequest;
import com.mamadou.payflow.transfer.dto.TransferValidationResult;
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.service.WalletLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferExecutionService {

    private final LedgerService ledgerService;
    private final TransactionService transactionService;
    private final WalletLimitService walletLimitService;
    
    @Qualifier("transferExecutor")
    private final TaskExecutor transferExecutor;
    private final RiskEngineService riskEngineService;

    public Transaction execute(TransferRequest request, TransferValidationResult validationResult) {
        Wallet sourceWallet = validationResult.getSourceWallet();
        Wallet destinationWallet = validationResult.getDestinationWallet();

        Transaction transaction = transactionService.createPendingTransfer(
                sourceWallet,
                destinationWallet,
                validationResult.getInitiatedBy(),
                request.getAmount(),
                sourceWallet.getCurrency(),
                request.getDescription(),
                request.getReference()
        );

        try {
            // Execute ledger recording and wallet limit tracking in parallel
            LedgerTraceResponse trace = recordLedgerTransactionAsync(transaction, sourceWallet, destinationWallet, request).join();
            
            // Track wallet limit usage asynchronously (can be done in parallel)
            trackWalletLimitAsync(sourceWallet, request.getAmount());
            evaluateRiskAsync(sourceWallet, transaction, request.getAmount());

            return transactionService.markCompleted(transaction, trace.getTraceId());
        } catch (RuntimeException exception) {
            transactionService.markFailed(transaction, exception.getMessage());
            throw exception;
        }
    }

    /**
     * Async ledger recording for non-blocking transfer execution
     */
    public CompletableFuture<LedgerTraceResponse> recordLedgerTransactionAsync(
            Transaction transaction,
            Wallet sourceWallet,
            Wallet destinationWallet,
            TransferRequest request) {
        
        return CompletableFuture.supplyAsync(() -> 
            ledgerService.recordFinancialTransaction(ledgerPostingRequest(
                    transaction.getReference(),
                    transaction.getReference(),
                    transaction.getCurrency(),
                    request.getDescription(),
                    entry(sourceWallet.getLedgerAccount().getCode(), LedgerPostingSide.DEBIT, request.getAmount(), "Transfer debit"),
                    entry(destinationWallet.getLedgerAccount().getCode(), LedgerPostingSide.CREDIT, request.getAmount(), "Transfer credit")
            )), transferExecutor);
    }

    /**
     * Async wallet limit tracking (fire-and-forget)
     */
    public void trackWalletLimitAsync(Wallet sourceWallet, BigDecimal amount) {
        CompletableFuture.runAsync(() -> {
            try {
                walletLimitService.trackDebitUsage(sourceWallet.getWalletLimit(), amount);
                log.debug("Wallet limit tracking completed for wallet {}", sourceWallet.getId());
            } catch (Exception e) {
                log.error("Error tracking wallet limit for wallet {}", sourceWallet.getId(), e);
            }
        }, transferExecutor);
    }

    private void evaluateRiskAsync(Wallet sourceWallet, Transaction transaction, BigDecimal amount) {
        CompletableFuture.runAsync(() -> {
            try {
                riskEngineService.evaluateTransactionRisk(
                    sourceWallet.getId(),
                    amount,
                    transaction.getId()
                );
            } catch (Exception e) {
                log.error("Post-transfer risk evaluation failed for transaction {}", transaction.getId(), e);
            }
        }, transferExecutor);
    }

    private LedgerPostingRequest ledgerPostingRequest(
            String traceId,
            String externalReference,
            String currency,
            String description,
            LedgerPostingRequest.Entry... entries
    ) {
        LedgerPostingRequest request = new LedgerPostingRequest();
        request.setTraceId(traceId);
        request.setExternalReference(externalReference);
        request.setCurrency(currency);
        request.setDescription(description);
        request.setPostings(List.of(entries));
        return request;
    }

    private LedgerPostingRequest.Entry entry(
            String accountCode,
            LedgerPostingSide side,
            BigDecimal amount,
            String description
    ) {
        LedgerPostingRequest.Entry entry = new LedgerPostingRequest.Entry();
        entry.setAccountCode(accountCode);
        entry.setSide(side);
        entry.setAmount(amount);
        entry.setDescription(description);
        return entry;
    }
}
