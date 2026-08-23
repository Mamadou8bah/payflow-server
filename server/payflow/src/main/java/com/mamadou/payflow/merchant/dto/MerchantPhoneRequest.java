package com.mamadou.payflow.merchant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MerchantPhoneRequest(
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+220[235679]\\d{6}$", message = "Enter a valid Gambian mobile number (+220 followed by 7 digits)")
        String phoneNumber
) {}
