package com.mamadou.payflow.wallet.dto;

import com.mamadou.payflow.wallet.enums.KycLevel;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WalletLimitResponse {
    private Long id;
    private Long walletId;
    private BigDecimal minTransactionAmount;
    private BigDecimal maxTransactionAmount;
    private BigDecimal dailyTransactionLimit;
    private BigDecimal weeklyTransactionLimit;
    private BigDecimal monthlyTransactionLimit;
    private BigDecimal amountSpentToday;
    private BigDecimal amountSpentThisWeek;
    private BigDecimal amountSpentThisMonth;
    private KycLevel kycLevel;
    private boolean active;
    private LocalDateTime lastResetAt;
}
