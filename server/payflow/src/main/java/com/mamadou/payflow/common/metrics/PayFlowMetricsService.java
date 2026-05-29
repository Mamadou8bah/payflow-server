package com.mamadou.payflow.common.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service to track key operational metrics using Micrometer.
 * Metrics are automatically exposed via actuator endpoints.
 */
@Service
@RequiredArgsConstructor
public class PayFlowMetricsService {

    private final MeterRegistry meterRegistry;

    // Counter metrics
    private Counter authLoginAttempts;
    private Counter authLoginSuccess;
    private Counter authLoginFailure;
    private Counter authRegisterAttempts;
    private Counter authRegisterSuccess;
    private Counter walletCreationAttempts;
    private Counter walletCreationSuccess;
    private Counter transferInitiationAttempts;
    private Counter transferInitiationSuccess;
    private Counter transferInitiationFailure;
    private Counter riskEvaluationRuns;
    private Counter riskFlagsRaised;
    private Counter reconciliationRuns;
    private Counter reconciliationMismatchesFound;

    // Timer metrics
    private Timer authServiceLoginTimer;
    private Timer transferServiceExecutionTimer;
    private Timer riskEvaluationTimer;
    private Timer ledgerPostingTimer;
    private Timer webhookProcessingTimer;

    public void initialize() {
        authLoginAttempts = Counter.builder("auth.login.attempts")
            .description("Total login attempts")
            .register(meterRegistry);

        authLoginSuccess = Counter.builder("auth.login.success")
            .description("Successful logins")
            .register(meterRegistry);

        authLoginFailure = Counter.builder("auth.login.failure")
            .description("Failed login attempts")
            .register(meterRegistry);

        authRegisterAttempts = Counter.builder("auth.register.attempts")
            .description("Total registration attempts")
            .register(meterRegistry);

        authRegisterSuccess = Counter.builder("auth.register.success")
            .description("Successful registrations")
            .register(meterRegistry);

        walletCreationAttempts = Counter.builder("wallet.creation.attempts")
            .description("Total wallet creation attempts")
            .register(meterRegistry);

        walletCreationSuccess = Counter.builder("wallet.creation.success")
            .description("Successful wallet creations")
            .register(meterRegistry);

        transferInitiationAttempts = Counter.builder("transfer.initiation.attempts")
            .description("Total transfer initiation attempts")
            .register(meterRegistry);

        transferInitiationSuccess = Counter.builder("transfer.initiation.success")
            .description("Successful transfer initiations")
            .register(meterRegistry);

        transferInitiationFailure = Counter.builder("transfer.initiation.failure")
            .description("Failed transfer initiations")
            .register(meterRegistry);

        riskEvaluationRuns = Counter.builder("risk.evaluation.runs")
            .description("Number of risk evaluations executed")
            .register(meterRegistry);

        riskFlagsRaised = Counter.builder("risk.flags.raised")
            .description("Total risk flags raised")
            .register(meterRegistry);

        reconciliationRuns = Counter.builder("reconciliation.runs")
            .description("Number of reconciliation runs executed")
            .register(meterRegistry);

        reconciliationMismatchesFound = Counter.builder("reconciliation.mismatches")
            .description("Number of reconciliation mismatches found")
            .register(meterRegistry);

        authServiceLoginTimer = Timer.builder("auth.login.duration")
            .description("Duration of login operations")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(meterRegistry);

        transferServiceExecutionTimer = Timer.builder("transfer.execution.duration")
            .description("Duration of transfer execution")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(meterRegistry);

        riskEvaluationTimer = Timer.builder("risk.evaluation.duration")
            .description("Duration of risk evaluation")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(meterRegistry);

        ledgerPostingTimer = Timer.builder("ledger.posting.duration")
            .description("Duration of ledger posting")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(meterRegistry);

        webhookProcessingTimer = Timer.builder("webhook.processing.duration")
            .description("Duration of webhook processing")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(meterRegistry);
    }

    public void recordAuthLoginAttempt() {
        authLoginAttempts.increment();
    }

    public void recordAuthLoginSuccess() {
        authLoginSuccess.increment();
    }

    public void recordAuthLoginFailure() {
        authLoginFailure.increment();
    }

    public void recordAuthRegisterAttempt() {
        authRegisterAttempts.increment();
    }

    public void recordAuthRegisterSuccess() {
        authRegisterSuccess.increment();
    }

    public void recordWalletCreationAttempt() {
        walletCreationAttempts.increment();
    }

    public void recordWalletCreationSuccess() {
        walletCreationSuccess.increment();
    }

    public void recordTransferInitiationAttempt() {
        transferInitiationAttempts.increment();
    }

    public void recordTransferInitiationSuccess() {
        transferInitiationSuccess.increment();
    }

    public void recordTransferInitiationFailure() {
        transferInitiationFailure.increment();
    }

    public void recordRiskEvaluationRun() {
        riskEvaluationRuns.increment();
    }

    public void recordRiskFlagRaised() {
        riskFlagsRaised.increment();
    }

    public void recordReconciliationRun() {
        reconciliationRuns.increment();
    }

    public void recordReconciliationMismatchFound(int count) {
        reconciliationMismatchesFound.increment(count);
    }

    public Timer.Sample startAuthLoginTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopAuthLoginTimer(Timer.Sample sample) {
        sample.stop(authServiceLoginTimer);
    }

    public Timer.Sample startTransferExecutionTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopTransferExecutionTimer(Timer.Sample sample) {
        sample.stop(transferServiceExecutionTimer);
    }

    public Timer.Sample startRiskEvaluationTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopRiskEvaluationTimer(Timer.Sample sample) {
        sample.stop(riskEvaluationTimer);
    }

    public Timer.Sample startLedgerPostingTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopLedgerPostingTimer(Timer.Sample sample) {
        sample.stop(ledgerPostingTimer);
    }

    public Timer.Sample startWebhookProcessingTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopWebhookProcessingTimer(Timer.Sample sample) {
        sample.stop(webhookProcessingTimer);
    }
}
