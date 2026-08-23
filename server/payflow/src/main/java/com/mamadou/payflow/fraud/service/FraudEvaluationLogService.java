package com.mamadou.payflow.fraud.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mamadou.payflow.fraud.client.FraudDetectionClient;
import com.mamadou.payflow.fraud.dto.FraudDecisionResponse;
import com.mamadou.payflow.fraud.entity.FraudEvaluationLog;
import com.mamadou.payflow.fraud.repository.FraudEvaluationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudEvaluationLogService {

    private final FraudEvaluationLogRepository repository;
    private final FraudDetectionClient fraudDetectionClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public FraudEvaluationLog saveEvaluation(
            String transactionReference,
            Long walletId,
            Long userId,
            Long persistedTransactionId,
            String operationType,
            BigDecimal amount,
            String currency,
            FraudDecisionResponse decision
    ) {
        if (decision == null) {
            return null;
        }

        FraudEvaluationLog entry = FraudEvaluationLog.builder()
            .transactionReference(transactionReference)
            .walletId(walletId)
            .userId(userId)
            .persistedTransactionId(persistedTransactionId)
            .operationType(operationType)
            .amount(amount)
            .currency(currency)
            .decision(decision.decision())
            .score(BigDecimal.valueOf(decision.score()))
            .featuresJson(toJson(decision.features()))
            .reasonsJson(toJson(decision.reasons()))
            .ruleTriggered(decision.rule_triggered())
            .latencyMs(decision.latency_ms())
            .build();

        FraudEvaluationLog saved = repository.save(entry);
        log.debug("Fraud evaluation logged: ref={}, decision={}, score={}",
            transactionReference, decision.decision(), decision.score());
        return saved;
    }

    @Transactional
    public FraudEvaluationLog labelEvaluation(Long logId, boolean confirmedFraud, String labelSource) {
        FraudEvaluationLog entry = repository.findById(logId)
            .orElseThrow(() -> new IllegalArgumentException("Fraud evaluation log not found: " + logId));
        entry.setConfirmedFraud(confirmedFraud);
        entry.setLabelSource(labelSource);
        entry.setLabeledAt(LocalDateTime.now());
        FraudEvaluationLog saved = repository.save(entry);
        if (saved.getTransactionReference() != null) {
            fraudDetectionClient.labelRecord(saved.getTransactionReference(), confirmedFraud);
        }
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<FraudEvaluationLog> listByWallet(Long walletId, Pageable pageable) {
        return repository.findByWalletIdOrderByCreatedAtDesc(walletId, pageable);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getStats() {
        return Map.of(
            "total", repository.countAll(),
            "labeled", repository.countLabeled(),
            "confirmedFraud", repository.countConfirmedFraud()
        );
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize fraud log JSON", e);
            return null;
        }
    }
}
