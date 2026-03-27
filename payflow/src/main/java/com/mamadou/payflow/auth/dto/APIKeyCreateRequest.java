package com.mamadou.payflow.auth.dto;

public record APIKeyCreateRequest(
        String name,
        int expiresInDays
) {
}
