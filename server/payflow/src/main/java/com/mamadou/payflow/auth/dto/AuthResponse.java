package com.mamadou.payflow.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds,
        Long userId,
        String username,
        String role,
        String userStatus,
        boolean twoFactorRequired,
        String twoFactorChallengeId
) {
}
