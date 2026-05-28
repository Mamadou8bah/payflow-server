package com.mamadou.payflow.webhook.repository;

import com.mamadou.payflow.webhook.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, Long> {
    Optional<WebhookEvent> findByProviderAndExternalEventId(String provider, String externalEventId);
}
