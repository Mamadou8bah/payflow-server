package com.mamadou.payflow.auth.dto;

public record RegisterRequest(
        String email,
        String password
) {
}
