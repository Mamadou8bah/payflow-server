package com.mamadou.payflow.webhook.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DepositWebhookRequest(
        @JsonAlias({"id", "event_id", "eventId"})
        String eventId,

        @JsonAlias({"type", "event_type", "eventType"})
        String eventType,

        @JsonAlias({"transaction_id", "transactionId", "payment_id", "paymentId"})
        String providerTransactionId,

        @JsonAlias({"reference", "external_reference", "externalReference"})
        String reference,

        @JsonAlias({"wallet_id", "walletId"})
        Long walletId,

        BigDecimal amount,

        String currency,

        String status
) {
}
