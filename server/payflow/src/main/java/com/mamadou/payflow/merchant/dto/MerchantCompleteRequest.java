package com.mamadou.payflow.merchant.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MerchantCompleteRequest(
        @NotBlank String registrationToken,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password,
        @NotBlank String confirmPassword,
        @AssertTrue(message = "You must accept the terms and conditions") boolean acceptedTerms
) {
    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
