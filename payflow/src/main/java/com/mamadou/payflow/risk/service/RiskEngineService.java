package com.mamadou.payflow.risk.service;

import com.mamadou.payflow.common.metrics.PayFlowMetricsService;
import com.mamadou.payflow.risk.dto.RiskEvaluationResult;
import com.mamadou.payflow.risk.dto.RiskRuleResult;
import com.mamadou.payflow.risk.entity.RiskFlag;
import com.mamadou.payflow.risk.enums.RiskLevel;
import com.mamadou.payflow.risk.enums.RiskRuleType;
import com.mamadou.payflow.risk.rules.RiskRule;
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.repository.WalletRepository;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.core.task.TaskExecutor;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Main risk engine service that orchestrates risk evaluation for transactions.
 * Evaluates all rules, aggregates risk scores, and determines if transaction should be blocked.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RiskEngineService {

    private final RiskRuleService riskRuleService;
    private final RiskFlagService riskFlagService;
    private final WalletRepository walletRepository;
    private final PayFlowMetricsService metricsService;
    
    @Qualifier("riskEvaluationExecutor")
    private final TaskExecutor riskEvaluationExecutor;

    @Value("${payflow.risk.critical-threshold:9.0}")
    private double criticalThreshold;

    @Value("${payflow.risk.high-threshold:7.0}")
    private double highThreshold;

    @Value("${payflow.risk.medium-threshold:5.0}")
    private double mediumThreshold;

    @Value("${payflow.risk.block-critical:true}")
    private boolean blockCritical;

    @Value("${payflow.risk.block-high:false}")
    private boolean blockHigh;

    /**
     * Evaluate risk for a transaction
     *
     * @param walletId the wallet ID
     * @param transactionAmount the transaction amount
     * @param transactionId the transaction ID
     * @return RiskEvaluationResult with risk assessment
     */
    @Transactional
    public RiskEvaluationResult evaluateTransactionRisk(Long walletId, BigDecimal transactionAmount, Long transactionId) {
        metricsService.recordRiskEvaluationRun();
        Timer.Sample sample = metricsService.startRiskEvaluationTimer();
        
        log.info("Evaluating risk for transaction: walletId={}, amount={}, transactionId={}",
            walletId, transactionAmount, transactionId);

        try {
            Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + walletId));

            List<RiskRuleResult> ruleResults = evaluateRulesInParallel(walletId, wallet, transactionAmount, transactionId);

            double aggregatedScore = 0.0;
            for (RiskRuleResult result : ruleResults) {
                if (result.triggered()) {
                    aggregatedScore += result.riskScore().doubleValue();
                }
        }

        List<RiskRuleResult> triggeredRules = ruleResults.stream()
            .filter(RiskRuleResult::triggered)
            .toList();

        double averageScore = triggeredRules.isEmpty() ? 0.0 : aggregatedScore / triggeredRules.size();
        RiskLevel riskLevel = RiskLevel.fromScore(averageScore);
        boolean shouldBlock = determineShouldBlock(riskLevel, averageScore);

        if (!triggeredRules.isEmpty()) {
            RiskRuleType primaryRule = triggeredRules.get(0).ruleType();
            riskFlagService.createRiskFlag(
                walletId,
                transactionId,
                riskLevel,
                primaryRule,
                BigDecimal.valueOf(averageScore),
                generateSummary(triggeredRules, averageScore)
            );
            metricsService.recordRiskFlagRaised();
            log.warn("Risk flag raised: walletId={}, transactionId={}, riskLevel={}, score={}", 
                walletId, transactionId, riskLevel, averageScore);
        }

        String summary = generateRiskSummary(riskLevel, triggeredRules, shouldBlock);

        RiskEvaluationResult result = new RiskEvaluationResult(
            transactionId,
            walletId,
            riskLevel,
            BigDecimal.valueOf(averageScore),
            triggeredRules,
            shouldBlock,
            summary
        );

        log.info("Risk evaluation complete: transactionId={}, riskLevel={}, score={}, shouldBlock={}",
            transactionId, riskLevel, averageScore, shouldBlock);

        return result;
        } finally {
            metricsService.stopRiskEvaluationTimer(sample);
        }
    }

    /**
     * Determine if transaction should be blocked based on risk level
     */
    private boolean determineShouldBlock(RiskLevel riskLevel, double score) {
        return (blockCritical && riskLevel == RiskLevel.CRITICAL) ||
               (blockHigh && riskLevel == RiskLevel.HIGH && score >= highThreshold);
    }

    /**
     * Evaluate all risk rules in parallel using thread pool executor
     * Each rule is evaluated independently on a separate thread
     */
    private List<RiskRuleResult> evaluateRulesInParallel(Long walletId, Wallet wallet, BigDecimal transactionAmount, Long transactionId) {
        List<RiskRule> rules = riskRuleService.getAllRules();
        List<CompletableFuture<RiskRuleResult>> futures = new ArrayList<>();

        // Submit all rule evaluations as async tasks
        for (RiskRule rule : rules) {
            CompletableFuture<RiskRuleResult> future = CompletableFuture.supplyAsync(() -> {
                try {
                    RiskRuleResult result = rule.evaluate(walletId, wallet, transactionAmount, transactionId);
                    if (result.triggered()) {
                        log.debug("Rule triggered: {} with score: {}", rule.getName(), result.riskScore());
                    }
                    return result;
                } catch (Exception e) {
                    log.error("Error evaluating rule: {}", rule.getName(), e);
                    // Return a neutral result on error (no risk)
                    return new RiskRuleResult(RiskRuleType.DAILY_THRESHOLD, false, BigDecimal.ZERO, "Error evaluating rule");
                }
            }, riskEvaluationExecutor);
            futures.add(future);
        }

        // Wait for all evaluations to complete
        try {
            return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error waiting for rule evaluation futures", e);
            // Fallback to sequential evaluation on error
            return evaluateRulesSequentially(walletId, wallet, transactionAmount, transactionId);
        }
    }

    /**
     * Fallback sequential rule evaluation if parallel execution fails
     */
    private List<RiskRuleResult> evaluateRulesSequentially(Long walletId, Wallet wallet, BigDecimal transactionAmount, Long transactionId) {
        List<RiskRuleResult> results = new ArrayList<>();
        for (RiskRule rule : riskRuleService.getAllRules()) {
            try {
                RiskRuleResult result = rule.evaluate(walletId, wallet, transactionAmount, transactionId);
                results.add(result);
            } catch (Exception e) {
                log.error("Error evaluating rule sequentially: {}", rule.getName(), e);
            }
        }
        return results;
    }

    /**
     * Generate summary from triggered rules
     */
    private String generateSummary(List<RiskRuleResult> triggeredRules, double averageScore) {
        if (triggeredRules.isEmpty()) {
            return "No risk rules triggered";
        }

        String rulesSummary = triggeredRules.stream()
            .map(r -> r.ruleType().getDescription())
            .collect(Collectors.joining(", "));

        return String.format("Risk Score: %.2f - Triggered rules: %s", averageScore, rulesSummary);
    }

    /**
     * Generate comprehensive risk summary
     */
    private String generateRiskSummary(RiskLevel riskLevel, List<RiskRuleResult> triggeredRules, boolean shouldBlock) {
        StringBuilder summary = new StringBuilder();
        summary.append("Risk Level: ").append(riskLevel);

        if (!triggeredRules.isEmpty()) {
            summary.append(" | Triggered Rules: ");
            summary.append(triggeredRules.stream()
                .map(r -> r.ruleType().getDescription())
                .collect(Collectors.joining(", ")));
        }

        if (shouldBlock) {
            summary.append(" | ACTION: BLOCK");
        } else {
            summary.append(" | ACTION: ALLOW");
        }

        return summary.toString();
    }

    /**
     * Get risk statistics for a wallet
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "riskStats", key = "#walletId")
    public Map<String, Object> getWalletRiskStats(Long walletId) {
        Map<String, Long> flagSummary = riskFlagService.getRiskSummary(walletId);

        return Map.of(
            "walletId", walletId,
            "unresolvedFlags", flagSummary.get("total"),
            "criticalFlags", flagSummary.get("critical"),
            "highFlags", flagSummary.get("high"),
            "mediumFlags", flagSummary.get("medium"),
            "lowFlags", flagSummary.get("low"),
            "hasHighRisk", riskFlagService.hasHighRiskActivity(walletId),
            "hasCritical", riskFlagService.hasCriticalFlags(walletId)
        );
    }

    /**
     * Get engine configuration
     */
    @Cacheable(value = "riskConfig", key = "'engineConfig'")
    public Map<String, Object> getEngineConfig() {
        return Map.of(
            "criticalThreshold", criticalThreshold,
            "highThreshold", highThreshold,
            "mediumThreshold", mediumThreshold,
            "blockCritical", blockCritical,
            "blockHigh", blockHigh,
            "activeRules", riskRuleService.getActiveRuleCount()
        );
    }
}
