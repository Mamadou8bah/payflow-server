package com.mamadou.payflow.audit.service;

import com.mamadou.payflow.audit.entity.AuditLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class AuditTrailBuilder {

    private final AuditLog auditLog;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private AuditTrailBuilder() {
        this.auditLog = new AuditLog();
        this.auditLog.setTimestamp(LocalDateTime.now());
        this.auditLog.setSuccess(true);
    }

    public static AuditTrailBuilder create() {
        return new AuditTrailBuilder();
    }

    public AuditTrailBuilder actor(Long actorId, String actorEmail) {
        this.auditLog.setActorId(actorId);
        this.auditLog.setActorEmail(actorEmail);
        return this;
    }

    public AuditTrailBuilder action(String actionType) {
        this.auditLog.setActionType(actionType);
        return this;
    }

    public AuditTrailBuilder entity(String entityType, Long entityId) {
        this.auditLog.setEntityType(entityType);
        this.auditLog.setEntityId(entityId);
        return this;
    }

    public AuditTrailBuilder previousState(Object previousState) {
        try {
            this.auditLog.setPreviousState(objectMapper.writeValueAsString(previousState));
        } catch (Exception e) {
            log.error("Error serializing previous state", e);
            this.auditLog.setPreviousState(previousState != null ? previousState.toString() : null);
        }
        return this;
    }

    public AuditTrailBuilder newState(Object newState) {
        try {
            this.auditLog.setNewState(objectMapper.writeValueAsString(newState));
        } catch (Exception e) {
            log.error("Error serializing new state", e);
            this.auditLog.setNewState(newState != null ? newState.toString() : null);
        }
        return this;
    }

    public AuditTrailBuilder changeDescription(String description) {
        this.auditLog.setChangeDescription(description);
        return this;
    }

    public AuditTrailBuilder requestMetadata(String ipAddress, String userAgent) {
        this.auditLog.setIpAddress(ipAddress);
        this.auditLog.setUserAgent(userAgent);
        return this;
    }

    public AuditTrailBuilder success(boolean success) {
        this.auditLog.setSuccess(success);
        return this;
    }

    public AuditTrailBuilder error(String errorDetails) {
        this.auditLog.setSuccess(false);
        this.auditLog.setErrorDetails(errorDetails);
        return this;
    }

    public AuditTrailBuilder timestamp(LocalDateTime timestamp) {
        this.auditLog.setTimestamp(timestamp);
        return this;
    }

    public AuditLog build() {
        if (this.auditLog.getActorId() == null) {
            throw new IllegalArgumentException("actorId is required");
        }
        if (this.auditLog.getActionType() == null) {
            throw new IllegalArgumentException("actionType is required");
        }
        if (this.auditLog.getEntityType() == null) {
            throw new IllegalArgumentException("entityType is required");
        }
        if (this.auditLog.getEntityId() == null) {
            throw new IllegalArgumentException("entityId is required");
        }
        return this.auditLog;
    }

    public String toString() {
        return "AuditTrailBuilder{" +
            "actor=" + auditLog.getActorId() +
            ", action='" + auditLog.getActionType() + '\'' +
            ", entity=" + auditLog.getEntityType() + ':' + auditLog.getEntityId() +
            ", timestamp=" + auditLog.getTimestamp() +
            ", success=" + auditLog.isSuccess() +
            '}';
    }
}
