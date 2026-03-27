package com.mamadou.payflow.auth.entity;

import com.mamadou.payflow.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class APIKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String publicId;

    @Column(nullable = false)
    private String secretHash;

    @Column(nullable = false)
    private boolean revoked = false;

    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastUsedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
}
