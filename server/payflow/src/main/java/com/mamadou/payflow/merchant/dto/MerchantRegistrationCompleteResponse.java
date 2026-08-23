package com.mamadou.payflow.merchant.dto;

import com.mamadou.payflow.auth.dto.AuthResponse;

public record MerchantRegistrationCompleteResponse(
        AuthResponse auth,
        String businessName,
        String verificationStatus,
        String message
) {}
