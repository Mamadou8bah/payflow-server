package com.mamadou.payflow.wallet.entity;

import com.mamadou.payflow.wallet.enums.KycLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "wallet_limits"
)
@Builder
public class WalletLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "wallet_id",nullable = false
    )
    private Wallet wallet;

    @Column(nullable = false)
    private BigDecimal minTransactionAmount;

    @Column(nullable = false)
    private BigDecimal maxTransactionAmount;

    private BigDecimal dailyTransactionLimit;

    private BigDecimal weeklyTransactionLimit;

    private BigDecimal monthlyTransactionLimit;

    @Enumerated(EnumType.STRING)
    private KycLevel kycLevel;

    private LocalDateTime lastResetAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //Usage Tracking
    @Builder.Default
    private BigDecimal amountSpentToday=BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal amountSpentThisWeek=BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal amountSpentThisMonth=BigDecimal.ZERO;

    @Builder.Default
    private boolean isActive=true;

    @PrePersist
    public void onCreate() {
        if (this.amountSpentToday == null) {
            this.amountSpentToday = BigDecimal.ZERO;
        }
        if (this.amountSpentThisWeek == null) {
            this.amountSpentThisWeek = BigDecimal.ZERO;
        }
        if (this.amountSpentThisMonth == null) {
            this.amountSpentThisMonth = BigDecimal.ZERO;
        }
        this.createdAt = LocalDateTime.now();
        this.lastResetAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
