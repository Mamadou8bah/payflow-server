package com.mamadou.payflow.merchant.dto;

import com.mamadou.payflow.merchant.enums.GambiaRegion;
import com.mamadou.payflow.merchant.enums.MerchantBusinessCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MerchantBusinessRequest(
        @NotBlank String registrationToken,
        @NotBlank @Size(max = 120) String businessName,
        @Size(max = 120) String tradingName,
        @NotNull MerchantBusinessCategory category,
        @NotNull GambiaRegion region,
        @NotBlank @Size(max = 80) String cityOrArea,
        @NotBlank @Size(max = 255) String streetAddress,
        @Size(max = 60) String businessRegistrationNumber
) {}
