package com.mamadou.payflow.withdrawal.controller;

import com.mamadou.payflow.withdrawal.dto.CreateWithdrawalRequest;
import com.mamadou.payflow.withdrawal.entity.Withdrawal;
import com.mamadou.payflow.withdrawal.service.WithdrawalService;
import com.mamadou.payflow.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        boolean isAgent = currentUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_AGENT"));
        Withdrawal withdrawal = withdrawalService.createWithdrawal(request, currentUser.getId(), isAgent);
        return ResponseEntity.ok(WithdrawalResponse.from(withdrawal));
    }

}

record WithdrawalResponse(Long id, Long walletId, Long userId, Long agentId, String withdrawalType, String status, java.math.BigDecimal amount, String currency, String withdrawalMethod, String reference) {
    static WithdrawalResponse from(Withdrawal d) {
        return new WithdrawalResponse(d.getId(), d.getWallet().getId(), d.getUser().getId(), d.getAgent() != null ? d.getAgent().getId() : null, d.getWithdrawalType().name(), d.getStatus().name(), d.getAmount(), d.getCurrency(), d.getWithdrawalMethod(), d.getReference());
    }
}
