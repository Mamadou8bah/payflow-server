package com.mamadou.payflow.audit.service;

import com.mamadou.payflow.audit.entity.AuditLog;
import com.mamadou.payflow.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    @CacheEvict(value = "auditLogs", allEntries = true)
    public AuditLog createLog(AuditLog auditLog) {
        if (auditLog.getTimestamp() == null) {
            auditLog.setTimestamp(LocalDateTime.now());
        }
        AuditLog saved = auditLogRepository.save(auditLog);
        log.debug("Audit log created: actor={}, action={}, entity={}", 
            auditLog.getActorId(), auditLog.getActionType(), auditLog.getEntityType());
        return saved;
    }

    /**
     * Async audit logging (fire-and-forget)
     * Useful for non-critical audit logging that shouldn't block request processing
     */
    @Async
    @CacheEvict(value = "auditLogs", allEntries = true)
    public void createLogAsync(AuditLog auditLog) {
        try {
            createLog(auditLog);
        } catch (Exception e) {
            log.error("Error creating async audit log", e);
        }
    }

    /**
     * Batch create audit logs efficiently
     * Reduces number of DB transactions for multiple log entries
     */
    @Transactional
    @CacheEvict(value = "auditLogs", allEntries = true)
    public List<AuditLog> createLogsBatch(List<AuditLog> auditLogs) {
        LocalDateTime now = LocalDateTime.now();
        auditLogs.forEach(log -> {
            if (log.getTimestamp() == null) {
                log.setTimestamp(now);
            }
        });
        List<AuditLog> saved = auditLogRepository.saveAll(auditLogs);
        log.info("Batch audit logs created: {} records", saved.size());
        return saved;
    }

    /**
     * Async batch audit logging
     */
    @Async
    @CacheEvict(value = "auditLogs", allEntries = true)
    public void createLogsBatchAsync(List<AuditLog> auditLogs) {
        try {
            createLogsBatch(auditLogs);
        } catch (Exception e) {
            log.error("Error creating batch async audit logs", e);
        }
    }

    @Cacheable(value = "auditLogs", key = "'actor:' + #actorId")
    public List<AuditLog> getLogsByActor(Long actorId) {
        return auditLogRepository.findByActorId(actorId);
    }

    @Cacheable(value = "auditLogs", key = "'action:' + #actionType")
    public List<AuditLog> getLogsByActionType(String actionType) {
        return auditLogRepository.findByActionType(actionType);
    }

    @Cacheable(value = "auditLogs", key = "'entity:' + #entityType + ':' + #entityId")
    public List<AuditLog> getLogsByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    @Cacheable(value = "auditLogs", key = "'failed'")
    public List<AuditLog> getFailedOperations() {
        return auditLogRepository.findFailedOperations();
    }

    @Cacheable(value = "auditLogs", key = "'trail:' + #entityType + ':' + #entityId")
    public List<AuditLog> getEntityAuditTrail(String entityType, Long entityId) {
        List<AuditLog> logs = getLogsByEntity(entityType, entityId);
        log.info("Retrieved audit trail for {}:{} - {} records", entityType, entityId, logs.size());
        return logs;
    }
}
