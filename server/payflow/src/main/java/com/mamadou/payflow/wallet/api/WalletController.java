package com.mamadou.payflow.wallet.api;

import com.mamadou.payflow.wallet.dto.WalletBalanceResponse;
import com.mamadou.payflow.wallet.dto.WalletLimitResponse;
import com.mamadou.payflow.wallet.dto.WalletLimitUpdateRequest;
import com.mamadou.payflow.wallet.dto.WalletRequest;
import com.mamadou.payflow.wallet.dto.WalletResponse;
import com.mamadou.payflow.wallet.dto.WalletTransactionRequest;
import com.mamadou.payflow.wallet.dto.WalletTransactionResponse;
import com.mamadou.payflow.wallet.service.WalletBalanceService;
import com.mamadou.payflow.wallet.service.WalletLimitService;
import com.mamadou.payflow.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final WalletBalanceService walletBalanceService;
    private final WalletLimitService walletLimitService;

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody WalletRequest request) {
        return ResponseEntity.ok(walletService.createWallet(request.getName(), request.getCurrency()));
    }

    @GetMapping
    public ResponseEntity<List<WalletResponse>> getUserWallets() {
        return ResponseEntity.ok(walletService.getUserWallets());
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<WalletBalanceResponse> getWalletBalance(@PathVariable Long id) {
        return ResponseEntity.ok(walletBalanceService.getWalletBalance(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.getWallet(id));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<WalletResponse> closeWallet(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.closeWallet(id));
    }

    @PatchMapping("/{id}/freeze")
    public ResponseEntity<WalletResponse> freezeWallet(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.freezeWallet(id));
    }

    @PatchMapping("/{id}/unfreeze")
    public ResponseEntity<WalletResponse> unfreezeWallet(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.unfreezeWallet(id));
    }

    @PostMapping("/{id}/credit")
    public ResponseEntity<WalletTransactionResponse> creditWallet(
            @PathVariable Long id,
            @Valid @RequestBody WalletTransactionRequest request) {
        return ResponseEntity.ok(walletBalanceService.creditWallet(id, request));
    }

    @PostMapping("/{id}/debit")
    public ResponseEntity<WalletTransactionResponse> debitWallet(
            @PathVariable Long id,
            @Valid @RequestBody WalletTransactionRequest request) {
        return ResponseEntity.ok(walletBalanceService.debitWallet(id, request));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<WalletTransactionResponse>> getWalletTransactions(@PathVariable Long id) {
        return ResponseEntity.ok(walletBalanceService.getWalletTransactions(id));
    }

    @GetMapping("/{id}/limits")
    public ResponseEntity<WalletLimitResponse> getWalletLimit(@PathVariable Long id) {
        return ResponseEntity.ok(walletLimitService.getWalletLimit(id));
    }

    @PatchMapping("/{id}/limits")
    public ResponseEntity<WalletLimitResponse> updateWalletLimit(
            @PathVariable Long id,
            @Valid @RequestBody WalletLimitUpdateRequest request) {
        return ResponseEntity.ok(walletLimitService.updateWalletLimit(id, request));
    }

    @PatchMapping("/{id}/limits/reset-usage")
    public ResponseEntity<WalletLimitResponse> resetWalletLimitUsage(@PathVariable Long id) {
        return ResponseEntity.ok(walletLimitService.resetUsage(id));
    }
}
