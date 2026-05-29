package com.mamadou.payflow.subscription.entity;

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

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(length = 50)
    private String interval; // e.g. MONTHLY, WEEKLY

    @Column(length = 50)
    private String status; // ACTIVE, PAUSED, CANCELLED

    @Column(length = 100)
    private String reference;

    private LocalDateTime nextChargeAt;
}
