package com.mamadou.payflow.fraud.service;

import com.mamadou.payflow.fraud.client.FraudDetectionClient;
import com.mamadou.payflow.fraud.config.FraudDetectionProperties;
import com.mamadou.payflow.fraud.dto.FraudDecisionResponse;
import com.mamadou.payflow.fraud.dto.TransactionEventRequest;
import com.mamadou.payflow.fraud.exception.FraudBlockedException;
import com.mamadou.payflow.risk.enums.RiskLevel;
import com.mamadou.payflow.risk.enums.RiskRuleType;
import com.mamadou.payflow.risk.service.RiskFlagService;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.wallet.entity.Wallet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService {

    private final FraudDetectionClient fraudDetectionClient;
    private final FraudDetectionProperties properties;
    private final RiskFlagService riskFlagService;
    private final FraudEvaluationLogService fraudEvaluationLogService;

    public FraudDecisionResponse evaluateTransaction(
            String transactionId,
            User user,
            Wallet wallet,
            BigDecimal amount,
            String operationType,
            Long persistedTransactionId
    ) {
        if (!properties.isEnabled()) {
            return null;
        }

        TransactionEventRequest event = buildEvent(transactionId, user, wallet, amount, operationType);

        try {
            FraudDecisionResponse decision = fraudDetectionClient.score(event);
            fraudEvaluationLogService.saveEvaluation(
                transactionId,
                wallet.getId(),
                user.getId(),
                persistedTransactionId,
                operationType,
                amount,
                wallet.getCurrency(),
                decision
            );
            handleDecision(decision, wallet.getId(), persistedTransactionId, operationType);
            return decision;
        } catch (RestClientException ex) {
            log.error("Fraud detection API unavailable for transaction {}: {}", transactionId, ex.getMessage());
            if (!properties.isFailOpen()) {
                throw new FraudBlockedException(
                    "Transaction blocked: fraud detection service unavailable",
                    1.0,
                    "block"
                );
            }
            return null;
        }
    }

    public void enforceDecision(FraudDecisionResponse decision) {
        if (decision == null) {
            return;
        }
        if ("block".equalsIgnoreCase(decision.decision())) {
            String reasons = decision.reasons() != null
                ? decision.reasons().stream().collect(Collectors.joining("; "))
                : "Fraud score exceeded threshold";
            throw new FraudBlockedException(
                "Transaction blocked by fraud detection: " + reasons,
                decision.score(),
                decision.decision()
            );
        }
        if (properties.isBlockOnReview() && "review".equalsIgnoreCase(decision.decision())) {
            throw new FraudBlockedException(
                "Transaction held for manual review",
                decision.score(),
                decision.decision()
            );
        }
    }

    public void checkBeforeExecution(
            String transactionId,
            User user,
            Wallet wallet,
            BigDecimal amount,
            String operationType
    ) {
        FraudDecisionResponse decision = evaluateTransaction(
            transactionId, user, wallet, amount, operationType, null
        );
        enforceDecision(decision);
    }

    private void handleDecision(
            FraudDecisionResponse decision,
            Long walletId,
            Long transactionId,
            String operationType
    ) {
        if (decision == null || "allow".equalsIgnoreCase(decision.decision())) {
            return;
        }

        RiskLevel level = mapRiskLevel(decision.decision(), decision.score());
        String reason = buildReason(decision, operationType);
        Long flagTransactionId = transactionId != null ? transactionId : 0L;

        riskFlagService.createRiskFlag(
            walletId,
            flagTransactionId,
            level,
            RiskRuleType.FRAUD_ML_SCORE,
            BigDecimal.valueOf(decision.score()),
            reason
        );
    }

    private RiskLevel mapRiskLevel(String decision, double score) {
        if ("block".equalsIgnoreCase(decision) || score >= 0.85) {
            return RiskLevel.CRITICAL;
        }
        if ("review".equalsIgnoreCase(decision) || score >= 0.50) {
            return RiskLevel.HIGH;
        }
        return RiskLevel.MEDIUM;
    }

    private String buildReason(FraudDecisionResponse decision, String operationType) {
        StringBuilder sb = new StringBuilder();
        sb.append("Fraud ML [").append(operationType).append("]: ");
        sb.append(decision.decision().toUpperCase());
        sb.append(" (score=").append(String.format("%.4f", decision.score())).append(")");
        if (decision.rule_triggered() != null) {
            sb.append(" rule=").append(decision.rule_triggered());
        }
        if (decision.reasons() != null && !decision.reasons().isEmpty()) {
            sb.append(" — ").append(String.join("; ", decision.reasons()));
        }
        return sb.toString();
    }

    private TransactionEventRequest buildEvent(
            String transactionId,
            User user,
            Wallet wallet,
            BigDecimal amount,
            String operationType
    ) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("operation_type", operationType);
        metadata.put("wallet_id", wallet.getId());

        return new TransactionEventRequest(
            transactionId,
            String.valueOf(user.getId()),
            amount.doubleValue(),
            wallet.getCurrency(),
            "payflow",
            mapOperationToMcc(operationType),
            resolveClientIp(),
            "web-" + user.getId(),
            "000000",
            properties.getDefaultCountry(),
            Instant.now(),
            properties.getDefaultChannel(),
            metadata
        );
    }

    private String mapOperationToMcc(String operationType) {
        return switch (operationType) {
            case "deposit" -> "6012";
            case "withdrawal" -> "6011";
            case "transfer" -> "4829";
            default -> "5999";
        };
    }

    private String resolveClientIp() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return "0.0.0.0";
        }
        HttpServletRequest request = attrs.getRequest();
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}
