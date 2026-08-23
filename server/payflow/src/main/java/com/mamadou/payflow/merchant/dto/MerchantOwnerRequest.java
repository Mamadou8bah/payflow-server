package com.mamadou.payflow.merchant.dto;

import com.mamadou.payflow.merchant.enums.MerchantIdType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MerchantOwnerRequest(
        @NotBlank String registrationToken,
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @NotNull MerchantIdType ownerIdType,
        @NotBlank @Size(max = 40) String ownerIdNumber
) {}
