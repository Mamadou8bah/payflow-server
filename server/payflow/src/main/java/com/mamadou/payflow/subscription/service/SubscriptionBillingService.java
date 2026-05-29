package com.mamadou.payflow.subscription.service;

import com.mamadou.payflow.subscription.repository.SubscriptionRepository;
import com.mamadou.payflow.webhook.client.CreateChargeRequest;
import com.mamadou.payflow.webhook.client.CreateChargeResponse;
import com.mamadou.payflow.webhook.client.ModemPayClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionBillingService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;
    private final ModemPayClient modemPayClient;

    // Runs every hour and charges due subscriptions (simple implementation)
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void runBilling() {
        List<com.mamadou.payflow.subscription.entity.Subscription> due = subscriptionRepository.findByStatusAndNextChargeAtBefore("ACTIVE", LocalDateTime.now());
        for (var s : due) {
            try {
                CreateChargeRequest req = new CreateChargeRequest();
                req.setAmount(s.getAmount());
                req.setCurrency(s.getCurrency());
                req.setDescription("Subscription charge: " + s.getReference());
                req.setReference(s.getReference());
                req.setIdempotencyKey("sub-" + s.getReference() + "-" + s.getNextChargeAt().toLocalDate());
                CreateChargeResponse resp = modemPayClient.createCharge(req);
                if (resp != null) {
                    // advance nextChargeAt by 30 days
                    s.setNextChargeAt(s.getNextChargeAt().plusDays(30));
                    subscriptionRepository.save(s);
                }
            } catch (Exception ex) {
                log.warn("Failed to charge subscription {}: {}", s.getReference(), ex.getMessage());
            }
        }
    }
}
