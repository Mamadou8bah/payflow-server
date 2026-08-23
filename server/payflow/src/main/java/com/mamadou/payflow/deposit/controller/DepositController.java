package com.mamadou.payflow.deposit.controller;

import com.mamadou.payflow.common.security.SecurityRoleUtils;
import com.mamadou.payflow.deposit.dto.CreateDepositRequest;
import com.mamadou.payflow.deposit.dto.DepositResponse;
import com.mamadou.payflow.deposit.entity.Deposit;
import com.mamadou.payflow.deposit.service.DepositService;
import com.mamadou.payflow.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/deposits")
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

    @PostMapping
    public ResponseEntity<DepositResponse> createDeposit(
            @Valid @RequestBody CreateDepositRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        boolean isAgent = SecurityRoleUtils.isAgent(currentUser);
        Deposit deposit = depositService.createDeposit(request, currentUser.getId(), isAgent);
        return ResponseEntity.ok(DepositResponse.from(deposit));
    }

    @GetMapping
    public ResponseEntity<List<DepositResponse>> listDeposits(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(depositService.listForUser(currentUser.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepositResponse> getDeposit(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(depositService.getById(id, currentUser.getId()));
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<DepositResponse> getDepositByReference(@PathVariable String reference) {
        return ResponseEntity.ok(depositService.getByReference(reference));
    }
}
