package com.mamadou.payflow.auth.mapper;

import com.mamadou.payflow.auth.dto.AuthResponse;
import com.mamadou.payflow.auth.dto.RegisterRequest;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.user.enums.Role;
import com.mamadou.payflow.user.enums.UserStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuthMapper {

    public User toUser(RegisterRequest request, String passwordHash) {
        Role role = request.role() == null ? Role.USER : request.role();

        return User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .passwordHash(passwordHash)
                .enabled(true)
                .registrationStage(4)
                .userStatus(UserStatus.ACTIVE)
                .phoneVerified(false)
                .lastLoginAt(LocalDateTime.now())
                .failedLoginAttempts(0)
                .twoFactorEnabled(request.twoFactorEnabled())
                .role(role)
                .build();
    }

    public AuthResponse authenticated(User user, String accessToken, String refreshToken, long expiresInSeconds) {
        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                expiresInSeconds,
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                false,
                null
        );
    }

    public AuthResponse twoFactorRequired(User user, String challengeId) {
        return new AuthResponse(
                null,
                null,
                "Bearer",
                0,
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                true,
                challengeId
        );
    }
}
