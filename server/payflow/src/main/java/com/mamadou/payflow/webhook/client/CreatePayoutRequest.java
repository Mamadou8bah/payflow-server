package com.mamadou.payflow.webhook.client;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class CreatePayoutRequest {
    private BigDecimal amount;
    private String currency;
    private String phoneNumber;
    private String bankAccount;
    private String withdrawalMethod;
    private String description;
    private String reference;
    private String idempotencyKey;
    private Map<String, Object> metadata;
}
