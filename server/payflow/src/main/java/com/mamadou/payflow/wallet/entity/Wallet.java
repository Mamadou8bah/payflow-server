package com.mamadou.payflow.wallet.entity;

import com.mamadou.payflow.ledger.entity.LedgerAccount;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.wallet.enums.WalletStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private WalletStatus status = WalletStatus.ACTIVE;

    @Column(nullable = false,length =3)
    private String currency;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ledger_account_id")
    private LedgerAccount ledgerAccount;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "wallet_limit_id"
    )
    private WalletLimit walletLimit;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        if (this.status == null) {
            this.status = WalletStatus.ACTIVE;
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
