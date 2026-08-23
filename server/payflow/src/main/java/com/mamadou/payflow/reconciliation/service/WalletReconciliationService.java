package com.mamadou.payflow.reconciliation.service;

import com.mamadou.payflow.ledger.exception.LedgerException;
import com.mamadou.payflow.ledger.service.LedgerBalanceComputationService;
import com.mamadou.payflow.reconciliation.entity.ReconciliationMismatch;
import com.mamadou.payflow.reconciliation.entity.ReconciliationReport;
import com.mamadou.payflow.reconciliation.repository.ReconciliationMismatchRepository;
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.entity.WalletTransaction;
import com.mamadou.payflow.wallet.repository.WalletRepository;
import com.mamadou.payflow.wallet.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletReconciliationService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final LedgerBalanceComputationService ledgerBalanceComputationService;
    private final ReconciliationMismatchRepository mismatchRepository;

    @Qualifier("reconciliationExecutor")
    private final TaskExecutor reconciliationExecutor;

    private static final int BATCH_SIZE = 100;

    @Transactional
    public void reconcileWalletVsLedger(ReconciliationReport report) {
        log.info("Starting wallet vs ledger reconciliation for report {}", report.getId());

        List<Wallet> wallets = walletRepository.findAll();
        AtomicLong totalRecords = new AtomicLong(0);
        AtomicLong mismatches = new AtomicLong(0);

        processBatches(wallets, batch -> {
            List<CompletableFuture<ReconciliationResult>> futures = batch.stream()
                .map(wallet -> CompletableFuture.supplyAsync(() -> {
                    try {
                        totalRecords.incrementAndGet();
                        BigDecimal ledgerBalance = computeLedgerBalance(wallet);
                        BigDecimal transactionBalance = computeTransactionBalance(wallet.getId());

                        if (ledgerBalance.compareTo(transactionBalance) != 0) {
                            createMismatch(report, wallet, transactionBalance, ledgerBalance);
                            return new ReconciliationResult(true, wallet);
                        }
                        return new ReconciliationResult(false, wallet);
                    } catch (Exception e) {
                        log.error("Error reconciling wallet {}", wallet.getId(), e);
                        createMismatch(report, wallet, BigDecimal.ZERO, BigDecimal.ZERO);
                        return new ReconciliationResult(true, wallet);
                    }
                }, reconciliationExecutor))
                .collect(Collectors.toList());

            futures.stream()
                .map(CompletableFuture::join)
                .filter(ReconciliationResult::hasMismatch)
                .forEach(result -> mismatches.incrementAndGet());
        });

        report.setTotalRecordsChecked(totalRecords.get());
        report.setMismatchesFound(mismatches.get());
        log.info("Wallet reconciliation completed: total={}, mismatches={}", totalRecords.get(), mismatches.get());
    }

    private void processBatches(List<Wallet> wallets, java.util.function.Consumer<List<Wallet>> batchProcessor) {
        for (int i = 0; i < wallets.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, wallets.size());
            List<Wallet> batch = wallets.subList(i, end);
            batchProcessor.accept(batch);
        }
    }

    private BigDecimal computeLedgerBalance(Wallet wallet) {
        if (wallet.getLedgerAccount() == null) {
            throw new LedgerException("Wallet " + wallet.getId() + " has no ledger account");
        }
        return ledgerBalanceComputationService.computeBalance(wallet.getLedgerAccount());
    }

    private BigDecimal computeTransactionBalance(Long walletId) {
        List<WalletTransaction> transactions = walletTransactionRepository.findByWalletId(walletId);
        BigDecimal balance = BigDecimal.ZERO;
        for (WalletTransaction txn : transactions) {
            if ("CREDIT".equalsIgnoreCase(txn.getTransactionType())) {
                balance = balance.add(txn.getAmount());
            } else if ("DEBIT".equalsIgnoreCase(txn.getTransactionType())) {
                balance = balance.subtract(txn.getAmount());
            }
        }
        return balance;
    }

    private void createMismatch(
            ReconciliationReport report,
            Wallet wallet,
            BigDecimal transactionBalance,
            BigDecimal ledgerBalance
    ) {
        BigDecimal variance = ledgerBalance.subtract(transactionBalance).abs();

        ReconciliationMismatch mismatch = ReconciliationMismatch.builder()
            .report(report)
            .mismatchType("BALANCE_VARIANCE")
            .entityId(wallet.getId())
            .entityType("WALLET")
            .expectedValue(ledgerBalance.toPlainString())
            .actualValue(transactionBalance.toPlainString())
            .variance(variance)
            .description("Ledger balance does not match wallet transaction sum: ledger="
                + ledgerBalance + ", transactions=" + transactionBalance)
            .resolved(false)
            .build();

        mismatchRepository.save(mismatch);
        log.warn("Mismatch found for wallet {}: ledger={}, transactions={}",
            wallet.getId(), ledgerBalance, transactionBalance);
    }

    private record ReconciliationResult(boolean hasMismatch, Wallet wallet) {
    }
}
