package com.mamadou.payflow.auth.entity;

import com.mamadou.payflow.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String tokenId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String tokenHash;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean revoked;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public boolean isExpired() {
        return expiredAt.isBefore(LocalDateTime.now());
    }

    public boolean isActive() {
        return !revoked && !isExpired();
    }
}