package com.mamadou.payflow.webhook.repository;

import com.mamadou.payflow.webhook.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WebhookRepository extends JpaRepository<WebhookEvent, Long> {

    Optional<WebhookEvent> findByExternalEventId(String externalEventId);

    List<WebhookEvent> findByStatusAndAttemptsLessThan(String status, int attempts);

    List<WebhookEvent> findByReceivedAtAfter(LocalDateTime since);

    List<WebhookEvent> findByProvider(String provider);
}
