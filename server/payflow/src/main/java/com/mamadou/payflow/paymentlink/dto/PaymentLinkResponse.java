package com.mamadou.payflow.paymentlink.dto;

import com.mamadou.payflow.paymentlink.entity.PaymentLink;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentLinkResponse {
    private Long id;
    private Long merchantId;
    private Long walletId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String paymentUrl;
    private String reference;
    private LocalDateTime expiresAt;

    public static PaymentLinkResponse from(PaymentLink p) {
        PaymentLinkResponse r = new PaymentLinkResponse();
        r.setId(p.getId());
        r.setMerchantId(p.getMerchant().getId());
        r.setWalletId(p.getWallet().getId());
        r.setAmount(p.getAmount());
        r.setCurrency(p.getCurrency());
        r.setDescription(p.getDescription());
        r.setPaymentUrl(p.getPaymentUrl());
        r.setReference(p.getReference());
        r.setExpiresAt(p.getExpiresAt());
        return r;
    }
}
