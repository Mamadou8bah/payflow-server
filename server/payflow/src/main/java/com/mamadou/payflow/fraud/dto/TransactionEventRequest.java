package com.mamadou.payflow.fraud.dto;

import java.time.Instant;
import java.util.Map;

public record TransactionEventRequest(
    String transaction_id,
    String user_id,
    double amount,
    String currency,
    String merchant_id,
    String merchant_category,
    String ip_address,
    String device_fingerprint,
    String card_bin,
    String country,
    Instant timestamp,
    String channel,
    Map<String, Object> metadata
) {}
