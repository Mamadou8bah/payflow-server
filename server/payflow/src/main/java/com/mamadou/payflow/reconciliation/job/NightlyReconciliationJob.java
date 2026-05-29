package com.mamadou.payflow.reconciliation.job;

import com.mamadou.payflow.reconciliation.service.ReconciliationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NightlyReconciliationJob {

    private final ReconciliationService reconciliationService;

    /**
     * Runs nightly wallet-ledger reconciliation at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void runNightlyWalletReconciliation() {
        log.info("Starting scheduled nightly wallet-ledger reconciliation");
        try {
            reconciliationService.runWalletLedgerReconciliation();
            log.info("Nightly wallet-ledger reconciliation completed successfully");
        } catch (Exception e) {
            log.error("Nightly wallet-ledger reconciliation failed", e);
        }
    }

    /**
     * Runs webhook-deposit reconciliation every 6 hours
     */
    @Scheduled(cron = "0 0 */6 * * *")
    public void runScheduledWebhookReconciliation() {
        log.info("Starting scheduled webhook-deposit reconciliation");
        try {
            reconciliationService.runWebhookDepositReconciliation();
            log.info("Webhook-deposit reconciliation completed successfully");
        } catch (Exception e) {
            log.error("Webhook-deposit reconciliation failed", e);
        }
    }

    /**
     * Quick balance check every hour (can be used for alerts)
     */
    @Scheduled(cron = "0 0 * * * *")
    public void runHourlyBalanceCheck() {
        log.debug("Running hourly balance check");
        try {
            // This is a lightweight check for alerts
            // In production: check for critical mismatches and trigger alerts
        } catch (Exception e) {
            log.error("Hourly balance check failed", e);
        }
    }
}
