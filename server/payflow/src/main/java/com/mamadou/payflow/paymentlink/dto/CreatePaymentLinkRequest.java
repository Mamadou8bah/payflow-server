package com.mamadou.payflow.paymentlink.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePaymentLinkRequest {
    @NotNull
    private Long walletId;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private String currency;
    private String description;
    private String paymentUrl; // optional prefilled
    private Integer expiresDays;
}
