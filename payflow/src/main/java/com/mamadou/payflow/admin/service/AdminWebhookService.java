package com.mamadou.payflow.admin.service;

import com.mamadou.payflow.audit.entity.AuditLog;
import com.mamadou.payflow.audit.service.AuditLogService;
import com.mamadou.payflow.audit.service.AuditTrailBuilder;
import com.mamadou.payflow.webhook.entity.WebhookEvent;
import com.mamadou.payflow.webhook.repository.WebhookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminWebhookService {

    private final WebhookRepository webhookRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public void reprocessWebhook(Long webhookId, String reason, Long adminId, String adminEmail) {
        WebhookEvent webhook = webhookRepository.findById(webhookId)
            .orElseThrow(() -> new RuntimeException("Webhook not found: " + webhookId));

        String previousStatus = webhook.getStatus();
        webhook.setStatus("PENDING_REPROCESS");

        webhookRepository.save(webhook);
        log.warn("Webhook {} marked for reprocessing by admin {}: {}", webhookId, adminId, reason);

        AuditLog auditLog = AuditTrailBuilder.create()
            .actor(adminId, adminEmail)
            .action("REPROCESS_WEBHOOK")
            .entity("WEBHOOK_EVENT", webhookId)
            .previousState(previousStatus)
            .newState("PENDING_REPROCESS")
            .changeDescription(reason)
            .build();
        
        auditLogService.createLog(auditLog);
    }
}
