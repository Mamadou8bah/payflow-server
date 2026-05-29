package com.mamadou.payflow.auth.dto;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds
) {
}
