package com.mamadou.payflow.auth.dto;

public record ApiKeyResponse(
        String name,
        int expiresInDays,
        String apiKey
) {

}
