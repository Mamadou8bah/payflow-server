package com.mamadou.payflow.subscription.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateSubscriptionRequest {
    @NotNull
    private Long walletId;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private String currency;
    @NotNull
    private String interval; // MONTHLY, WEEKLY, etc.
}
