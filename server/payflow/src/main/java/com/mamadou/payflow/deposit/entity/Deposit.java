package com.mamadou.payflow.deposit.entity;

import com.mamadou.payflow.common.auditing.AuditableEntity;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.wallet.entity.Wallet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a deposit transaction - money being added to a wallet
 * Supports both agent-initiated deposits (on behalf of user) and self-service deposits
 */
@Entity
@Table(name = "deposits", indexes = {
        @Index(name = "idx_deposits_wallet", columnList = "wallet_id"),
        @Index(name = "idx_deposits_user", columnList = "user_id"),
        @Index(name = "idx_deposits_agent", columnList = "agent_id"),
        @Index(name = "idx_deposits_status", columnList = "status"),
        @Index(name = "idx_deposits_type", columnList = "deposit_type"),
        @Index(name = "idx_deposits_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deposit extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Agent who made the deposit (null for self-service deposits)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private User agent;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DepositType depositType;  // AGENT or SELF

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DepositStatus status;  // PENDING, COMPLETED, FAILED, CANCELLED

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    /**
     * Payment method used
     */
    @Column(length = 50)
    private String paymentMethod;  // mobile_money, card, bank_transfer, etc

    /**
     * Reference to the payment provider (e.g., Modem Pay charge ID)
     */
    @Column(length = 100)
    private String externalPaymentId;

    /**
     * URL where the user can complete payment (hosted checkout/payment link)
     */
    @Column(length = 512)
    private String paymentUrl;

    /**
     * Optional idempotency key used when creating upstream charges
     */
    @Column(length = 128)
    private String idempotencyKey;

    /**
     * Your internal transaction reference
     */
    @Column(length = 100)
    private String reference;

    /**
     * Description of the deposit
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Reason if deposit failed
     */
    @Column(columnDefinition = "TEXT")
    private String failureReason;

    /**
     * Phone number used for mobile money deposits
     */
    @Column(length = 20)
    private String phoneNumber;

    /**
     * When the payment was completed
     */
    private LocalDateTime completedAt;

    @PrePersist
    public void onCreate() {
        if (status == null) {
            status = DepositStatus.PENDING;
        }
    }

    public enum DepositType {
        /**
         * Agent deposits money on behalf of the user
         */
        AGENT,
        /**
         * User deposits money for themselves (self-service)
         */
        SELF
    }

    public enum DepositStatus {
        AWAITING_AGENT, // Merchant intent created; agent must scan QR and complete
        PENDING,        // Awaiting external payment
        COMPLETED,      // Payment received and processed
        FAILED,         // Payment failed
        CANCELLED       // Deposit cancelled
    }
}
