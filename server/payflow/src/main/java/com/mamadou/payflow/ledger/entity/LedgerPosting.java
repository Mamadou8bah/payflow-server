package com.mamadou.payflow.ledger.entity;

import com.mamadou.payflow.ledger.enums.LedgerPostingSide;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "ledger_postings",
        indexes = {
                @Index(name = "idx_ledger_postings_trace_id", columnList = "trace_id"),
                @Index(name = "idx_ledger_postings_account_id", columnList = "account_id"),
                @Index(name = "idx_ledger_postings_external_reference", columnList = "external_reference")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trace_id", nullable = false, length = 80)
    private String traceId;

    @Column(name = "external_reference", length = 120)
    private String externalReference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private LedgerAccount account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private LedgerPostingSide side;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private LocalDateTime postedAt;

    @PrePersist
    public void onCreate() {
        if (currency != null) {
            currency = currency.toUpperCase();
        }
        postedAt = LocalDateTime.now();
    }
}
