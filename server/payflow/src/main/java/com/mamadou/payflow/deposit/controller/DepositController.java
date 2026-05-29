package com.mamadou.payflow.deposit.controller;

import com.mamadou.payflow.deposit.dto.CreateDepositRequest;
import com.mamadou.payflow.deposit.entity.Deposit;
import com.mamadou.payflow.deposit.service.DepositService;
import com.mamadou.payflow.security.CurrentUser;
import com.mamadou.payflow.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/deposits")
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

    /**
     * Create a deposit. If the authenticated user is an agent and provides userId, it will be treated as agent deposit
     */
    @PostMapping
    public ResponseEntity<DepositResponse> createDeposit(
            @Valid @RequestBody CreateDepositRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        boolean isAgent = currentUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_AGENT"));
        Deposit deposit = depositService.createDeposit(request, currentUser.getId(), isAgent);
        return ResponseEntity.ok(DepositResponse.from(deposit));
    }

    // Additional endpoints (list, get by id) can be added here
}

// Local DTO used for response
record DepositResponse(Long id, Long walletId, Long userId, Long agentId, String depositType, String status, java.math.BigDecimal amount, String currency, String paymentMethod, String reference, String paymentUrl) {
    static DepositResponse from(Deposit d) {
        return new DepositResponse(d.getId(), d.getWallet().getId(), d.getUser().getId(), d.getAgent() != null ? d.getAgent().getId() : null, d.getDepositType().name(), d.getStatus().name(), d.getAmount(), d.getCurrency(), d.getPaymentMethod(), d.getReference(), d.getPaymentUrl());
    }
}
