package com.mamadou.payflow.subscription.service;

import com.mamadou.payflow.subscription.dto.CreateSubscriptionRequest;
import com.mamadou.payflow.subscription.dto.SubscriptionResponse;
import com.mamadou.payflow.subscription.entity.Subscription;
import com.mamadou.payflow.subscription.repository.SubscriptionRepository;
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
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @Transactional
    public SubscriptionResponse create(Long userId, CreateSubscriptionRequest req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        var wallet = walletRepository.findById(req.getWalletId()).orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        Subscription s = Subscription.builder()
                .user(user)
                .wallet(wallet)
                .amount(req.getAmount())
                .currency(req.getCurrency())
                .interval(req.getInterval())
                .status("ACTIVE")
                .reference("sub-" + java.util.UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
                .nextChargeAt(LocalDateTime.now().plusDays(30))
                .build();

        s = subscriptionRepository.save(s);
        return SubscriptionResponse.from(s);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> listForUser(Long userId) {
        return subscriptionRepository.findByUserId(userId).stream().map(SubscriptionResponse::from).collect(Collectors.toList());
    }
}
