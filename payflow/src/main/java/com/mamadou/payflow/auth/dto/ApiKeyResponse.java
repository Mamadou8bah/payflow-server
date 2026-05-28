package com.mamadou.payflow.auth.dto;

import java.time.LocalDateTime;

public record ApiKeyResponse(
        String name,
        String publicId,
        LocalDateTime expiresAt,
        boolean revoked,
        String apiKey
) {

}
