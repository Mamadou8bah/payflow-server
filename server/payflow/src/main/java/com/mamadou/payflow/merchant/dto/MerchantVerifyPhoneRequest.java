package com.mamadou.payflow.merchant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MerchantVerifyPhoneRequest(
        @NotBlank String registrationToken,
        @NotBlank
        @Pattern(regexp = "^\\d{6}$", message = "Verification code must be 6 digits")
        String code
) {}
