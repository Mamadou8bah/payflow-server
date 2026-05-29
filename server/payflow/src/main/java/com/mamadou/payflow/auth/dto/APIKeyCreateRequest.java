package com.mamadou.payflow.auth.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record APIKeyCreateRequest(
        @NotBlank String name,
        @Min(1) @Max(3650) int expiresInDays
) {
}
