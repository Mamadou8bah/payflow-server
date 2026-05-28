package com.mamadou.payflow.audit.entity;

import com.mamadou.payflow.common.auditing.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_actor_id", columnList = "actor_id"),
    @Index(name = "idx_action_type", columnList = "action_type"),
    @Index(name = "idx_entity_id", columnList = "entity_id"),
    @Index(name = "idx_timestamp", columnList = "timestamp"),
    @Index(name = "idx_entity_type", columnList = "entity_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long actorId;

    @Column(nullable = false)
    private String actorEmail;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String actionType;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private Long entityId;

    @Column(columnDefinition = "TEXT")
    private String previousState;

    @Column(columnDefinition = "TEXT")
    private String newState;

    @Column(columnDefinition = "TEXT")
    private String changeDescription;

    private String ipAddress;

    private String userAgent;

    private boolean success;

    @Column(columnDefinition = "TEXT")
    private String errorDetails;

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
        if (this.success == false) {
            this.success = true;
        }
    }
}
