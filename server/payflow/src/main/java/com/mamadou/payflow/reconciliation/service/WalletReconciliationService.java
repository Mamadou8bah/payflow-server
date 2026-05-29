package com.mamadou.payflow.reconciliation.service;

import com.mamadou.payflow.reconciliation.entity.ReconciliationMismatch;
import com.mamadou.payflow.reconciliation.entity.ReconciliationReport;
import com.mamadou.payflow.reconciliation.repository.ReconciliationMismatchRepository;
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.repository.WalletRepository;
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

        // Process wallets in batches for better performance
        processBatches(wallets, batch -> {
            List<CompletableFuture<ReconciliationResult>> futures = batch.stream()
                .map(wallet -> CompletableFuture.supplyAsync(() -> {
                    try {
                        totalRecords.incrementAndGet();
                        String walletId = wallet.getId().toString();
                        String ledgerStatus = calculateLedgerStatus(wallet.getId());

                        if (!walletId.equals(ledgerStatus)) {
                            createMismatch(report, wallet, walletId, ledgerStatus);
                            return new ReconciliationResult(true, wallet);
                        }
                        return new ReconciliationResult(false, wallet);
                    } catch (Exception e) {
                        log.error("Error reconciling wallet {}", wallet.getId(), e);
                        return new ReconciliationResult(false, wallet);
                    }
                }, reconciliationExecutor))
                .collect(Collectors.toList());

            // Wait for batch to complete
            futures.stream()
                .map(CompletableFuture::join)
                .filter(ReconciliationResult::hasMismatch)
                .forEach(result -> mismatches.incrementAndGet());
        });

        report.setTotalRecordsChecked(totalRecords.get());
        report.setMismatchesFound(mismatches.get());
        log.info("Wallet reconciliation completed: total={}, mismatches={}", totalRecords.get(), mismatches.get());
    }

    /**
     * Process wallets in batches for efficient parallel processing
     */
    private void processBatches(List<Wallet> wallets, java.util.function.Consumer<List<Wallet>> batchProcessor) {
        for (int i = 0; i < wallets.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, wallets.size());
            List<Wallet> batch = wallets.subList(i, end);
            batchProcessor.accept(batch);
        }
    }

    private String calculateLedgerStatus(Long walletId) {
        // In a real implementation, this would query the ledger service
        // For now, return a placeholder that would indicate a mismatch
        // In production: LedgerService.getBalance(walletId)
        return "UNKNOWN";
    }

    private void createMismatch(ReconciliationReport report, Wallet wallet, String walletData, String ledgerData) {
        BigDecimal variance = BigDecimal.ZERO;
        
        ReconciliationMismatch mismatch = ReconciliationMismatch.builder()
            .report(report)
            .mismatchType("BALANCE_VARIANCE")
            .entityId(wallet.getId())
            .entityType("WALLET")
            .expectedValue(ledgerData)
            .actualValue(walletData)
            .variance(variance)
            .description("Wallet data does not match ledger: wallet=" + walletData + ", ledger=" + ledgerData)
            .resolved(false)
            .build();

        mismatchRepository.save(mismatch);
        log.warn("Mismatch found for wallet {}: wallet={}, ledger={}", 
            wallet.getId(), walletData, ledgerData);
    }

    /**
     * Helper record to track reconciliation results
     */
    private record ReconciliationResult(boolean hasMismatch, Wallet wallet) {
    }
}
