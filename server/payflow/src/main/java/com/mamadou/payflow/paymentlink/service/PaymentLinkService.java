package com.mamadou.payflow.paymentlink.service;

import com.mamadou.payflow.paymentlink.dto.CreatePaymentLinkRequest;
import com.mamadou.payflow.paymentlink.dto.PaymentLinkResponse;
import com.mamadou.payflow.paymentlink.entity.PaymentLink;
import com.mamadou.payflow.paymentlink.repository.PaymentLinkRepository;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.user.repository.UserRepository;
import com.mamadou.payflow.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentLinkService {

    private final PaymentLinkRepository paymentLinkRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @Transactional
    public PaymentLinkResponse create(Long merchantId, CreatePaymentLinkRequest req) {
        User merchant = userRepository.findById(merchantId).orElseThrow(() -> new IllegalArgumentException("Merchant not found"));
        var wallet = walletRepository.findById(req.getWalletId()).orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        PaymentLink link = PaymentLink.builder()
                .merchant(merchant)
                .wallet(wallet)
                .amount(req.getAmount())
                .currency(req.getCurrency())
                .description(req.getDescription())
                .reference("plink-" + java.util.UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
                .paymentUrl(req.getPaymentUrl())
                .expiresAt(LocalDateTime.now().plusDays(req.getExpiresDays() == null ? 7 : req.getExpiresDays()))
                .status("ACTIVE")
                .build();

        link = paymentLinkRepository.save(link);
        return PaymentLinkResponse.from(link);
    }

    @Transactional(readOnly = true)
    public List<PaymentLinkResponse> listForMerchant(Long merchantId) {
        return paymentLinkRepository.findByMerchantIdOrderByIdDesc(merchantId).stream()
                .map(PaymentLinkResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaymentLinkResponse getById(Long merchantId, Long linkId) {
        PaymentLink link = paymentLinkRepository.findByIdAndMerchantId(linkId, merchantId)
                .orElseThrow(() -> new IllegalArgumentException("Payment link not found"));
        return PaymentLinkResponse.from(link);
    }
}
