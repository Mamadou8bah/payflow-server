package com.mamadou.payflow.paymentlink.entity;

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
@Table(name = "payment_links")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private User merchant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 200)
    private String paymentUrl;

    @Column(length = 100)
    private String reference;

    private LocalDateTime expiresAt;

    @Column(length = 20)
    private String status; // ACTIVE, EXPIRED
}
