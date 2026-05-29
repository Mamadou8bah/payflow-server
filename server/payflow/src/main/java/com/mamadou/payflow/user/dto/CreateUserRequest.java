package com.mamadou.payflow.user.dto;

public record CreateUserRequest(
        String firstName,
        String lastname,
        String phoneNumber,
        String password
) {
}
