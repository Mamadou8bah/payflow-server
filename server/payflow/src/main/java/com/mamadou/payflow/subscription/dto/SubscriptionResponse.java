package com.mamadou.payflow.subscription.dto;

import com.mamadou.payflow.subscription.entity.Subscription;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SubscriptionResponse {
    private Long id;
    private Long userId;
    private Long walletId;
    private BigDecimal amount;
    private String currency;
    private String interval;
    private String status;
    private String reference;
    private LocalDateTime nextChargeAt;

    public static SubscriptionResponse from(Subscription s) {
        SubscriptionResponse r = new SubscriptionResponse();
        r.setId(s.getId());
        r.setUserId(s.getUser().getId());
        r.setWalletId(s.getWallet().getId());
        r.setAmount(s.getAmount());
        r.setCurrency(s.getCurrency());
        r.setInterval(s.getInterval());
        r.setStatus(s.getStatus());
        r.setReference(s.getReference());
        r.setNextChargeAt(s.getNextChargeAt());
        return r;
    }
}
