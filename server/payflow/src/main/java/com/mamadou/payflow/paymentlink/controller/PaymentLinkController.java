package com.mamadou.payflow.paymentlink.controller;

import com.mamadou.payflow.paymentlink.dto.CreatePaymentLinkRequest;
import com.mamadou.payflow.paymentlink.dto.PaymentLinkResponse;
import com.mamadou.payflow.paymentlink.service.PaymentLinkService;
import com.mamadou.payflow.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment-links")
@RequiredArgsConstructor
public class PaymentLinkController {

    private final PaymentLinkService paymentLinkService;

    @PostMapping
    public ResponseEntity<PaymentLinkResponse> create(@Valid @RequestBody CreatePaymentLinkRequest request,
                                                      @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(paymentLinkService.create(currentUser.getId(), request));
    }

    @GetMapping
    public ResponseEntity<java.util.List<PaymentLinkResponse>> list(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(paymentLinkService.listForMerchant(currentUser.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentLinkResponse> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(paymentLinkService.getById(currentUser.getId(), id));
    }
}
