package com.mamadou.payflow.withdrawal.controller;

import com.mamadou.payflow.common.security.SecurityRoleUtils;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.withdrawal.dto.CreateWithdrawalRequest;
import com.mamadou.payflow.withdrawal.dto.WithdrawalResponse;
import com.mamadou.payflow.withdrawal.entity.Withdrawal;
import com.mamadou.payflow.withdrawal.service.WithdrawalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/withdrawals")
@RequiredArgsConstructor
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    @PostMapping
    public ResponseEntity<WithdrawalResponse> createWithdrawal(
            @Valid @RequestBody CreateWithdrawalRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        boolean isAgent = SecurityRoleUtils.isAgent(currentUser);
        Withdrawal withdrawal = withdrawalService.createWithdrawal(request, currentUser.getId(), isAgent);
        return ResponseEntity.ok(WithdrawalResponse.from(withdrawal));
    }

    @GetMapping
    public ResponseEntity<List<WithdrawalResponse>> listWithdrawals(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(withdrawalService.listForUser(currentUser.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WithdrawalResponse> getWithdrawal(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(withdrawalService.getById(id, currentUser.getId()));
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<WithdrawalResponse> getWithdrawalByReference(@PathVariable String reference) {
        return ResponseEntity.ok(withdrawalService.getByReference(reference));
    }
}
