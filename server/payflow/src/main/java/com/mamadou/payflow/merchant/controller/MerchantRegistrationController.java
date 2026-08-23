package com.mamadou.payflow.merchant.controller;

import com.mamadou.payflow.common.response.ApiResponse;
import com.mamadou.payflow.merchant.dto.*;
import com.mamadou.payflow.merchant.service.MerchantRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/merchant/register")
@RequiredArgsConstructor
public class MerchantRegistrationController {

    private final MerchantRegistrationService merchantRegistrationService;

    @PostMapping("/phone")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MerchantRegistrationStepResponse> startPhone(@Valid @RequestBody MerchantPhoneRequest request) {
        return new ApiResponse<>(true, "Verification code sent", merchantRegistrationService.startPhoneVerification(request));
    }

    @PostMapping("/verify-phone")
    public ApiResponse<MerchantRegistrationStepResponse> verifyPhone(@Valid @RequestBody MerchantVerifyPhoneRequest request) {
        return new ApiResponse<>(true, "Phone verified", merchantRegistrationService.verifyPhone(request));
    }

    @PostMapping("/business")
    public ApiResponse<MerchantRegistrationStepResponse> business(@Valid @RequestBody MerchantBusinessRequest request) {
        return new ApiResponse<>(true, "Business details saved", merchantRegistrationService.saveBusiness(request));
    }

    @PostMapping("/owner")
    public ApiResponse<MerchantRegistrationStepResponse> owner(@Valid @RequestBody MerchantOwnerRequest request) {
        return new ApiResponse<>(true, "Owner details saved", merchantRegistrationService.saveOwner(request));
    }

    @PostMapping("/complete")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MerchantRegistrationCompleteResponse> complete(@Valid @RequestBody MerchantCompleteRequest request) {
        return new ApiResponse<>(true, "Merchant application submitted", merchantRegistrationService.complete(request));
    }
}
