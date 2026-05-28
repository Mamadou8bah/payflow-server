package com.mamadou.payflow.wallet.dto;

import com.mamadou.payflow.wallet.enums.KycLevel;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletLimitUpdateRequest {
    @DecimalMin(value = "0.01", message = "Minimum transaction amount must be greater than zero")
    private BigDecimal minTransactionAmount;

    @DecimalMin(value = "0.01", message = "Maximum transaction amount must be greater than zero")
    private BigDecimal maxTransactionAmount;

    @DecimalMin(value = "0.01", message = "Daily transaction limit must be greater than zero")
    private BigDecimal dailyTransactionLimit;

    @DecimalMin(value = "0.01", message = "Weekly transaction limit must be greater than zero")
    private BigDecimal weeklyTransactionLimit;

    @DecimalMin(value = "0.01", message = "Monthly transaction limit must be greater than zero")
    private BigDecimal monthlyTransactionLimit;

    private KycLevel kycLevel;
    private Boolean active;
}
