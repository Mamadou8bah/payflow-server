package com.mamadou.payflow.fraud.entity;

import com.mamadou.payflow.common.auditing.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fraud_evaluation_logs", indexes = {
    @Index(name = "idx_fraud_eval_txn_ref", columnList = "transaction_reference"),
    @Index(name = "idx_fraud_eval_wallet", columnList = "wallet_id"),
    @Index(name = "idx_fraud_eval_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudEvaluationLog extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String transactionReference;

    private Long walletId;

    private Long userId;

    private Long persistedTransactionId;

    @Column(nullable = false, length = 32)
    private String operationType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 8)
    private String currency;

    @Column(nullable = false, length = 20)
    private String decision;

    @Column(nullable = false, precision = 8, scale = 4)
    private BigDecimal score;

    @Column(columnDefinition = "TEXT")
    private String featuresJson;

    @Column(columnDefinition = "TEXT")
    private String reasonsJson;

    @Column(length = 100)
    private String ruleTriggered;

    private Double latencyMs;

    /** Null until an admin confirms fraud/not-fraud for model retraining */
    private Boolean confirmedFraud;

    @Column(length = 64)
    private String labelSource;

    private LocalDateTime labeledAt;
}
