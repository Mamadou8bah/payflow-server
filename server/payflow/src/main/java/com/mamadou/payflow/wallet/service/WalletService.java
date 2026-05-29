package com.mamadou.payflow.wallet.service;

import com.mamadou.payflow.common.Exception.AccountNotFoundException;
import com.mamadou.payflow.ledger.entity.LedgerAccount;
import com.mamadou.payflow.ledger.enums.LedgerAccountType;
import com.mamadou.payflow.ledger.service.LedgerAccountService;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.user.repository.UserRepository;
import com.mamadou.payflow.wallet.dto.WalletResponse;
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.entity.WalletLimit;
import com.mamadou.payflow.wallet.enums.WalletStatus;
import com.mamadou.payflow.wallet.exception.WalletAlreadyExistsException;
import com.mamadou.payflow.wallet.exception.WalletNotFoundException;
import com.mamadou.payflow.wallet.exception.WalletOperationException;
import com.mamadou.payflow.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletLimitService walletLimitService;
    private final LedgerAccountService ledgerAccountService;
    private final UserRepository userRepository;

    @Transactional
    public WalletResponse createWallet(String name, String currency) {
        User currentUser = currentUser();
        String normalizedCurrency = normalizeCurrency(currency);

        if (walletRepository.existsByUserIdAndCurrencyIgnoreCase(currentUser.getId(), normalizedCurrency)) {
            throw new WalletAlreadyExistsException("You already have a wallet for " + normalizedCurrency);
        }

        Wallet wallet = Wallet.builder()
                .name(resolveWalletName(name, normalizedCurrency))
                .user(currentUser)
                .currency(normalizedCurrency)
                .status(WalletStatus.ACTIVE)
                .build();

        wallet = walletRepository.save(wallet);

        LedgerAccount ledgerAccount = ledgerAccountService.createAccountEntity(
                walletLedgerAccountCode(wallet),
                "Wallet liability - " + wallet.getId(),
                LedgerAccountType.LIABILITY,
                normalizedCurrency
        );
        wallet.setLedgerAccount(ledgerAccount);

        WalletLimit limit = walletLimitService.createDefaultLimit(wallet);
        wallet.setWalletLimit(limit);
        walletRepository.save(wallet);

        return mapToResponse(wallet);
    }

    @Transactional(readOnly = true)
    public List<WalletResponse> getUserWallets() {
        User currentUser = currentUser();

        return walletRepository.findByUserId(currentUser.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WalletResponse getWallet(Long walletId) {
        return mapToResponse(getOwnedWallet(walletId));
    }

    @Transactional
    public WalletResponse closeWallet(Long walletId) {
        Wallet wallet = getOwnedWallet(walletId);
        wallet.setStatus(WalletStatus.CLOSED);
        return mapToResponse(walletRepository.save(wallet));
    }

    @Transactional
    public WalletResponse freezeWallet(Long walletId) {
        Wallet wallet = getOwnedWallet(walletId);
        if (wallet.getStatus() == WalletStatus.CLOSED) {
            throw new WalletOperationException("Closed wallets cannot be frozen");
        }
        wallet.setStatus(WalletStatus.SUSPENDED);
        return mapToResponse(walletRepository.save(wallet));
    }

    @Transactional
    public WalletResponse unfreezeWallet(Long walletId) {
        Wallet wallet = getOwnedWallet(walletId);
        if (wallet.getStatus() == WalletStatus.CLOSED) {
            throw new WalletOperationException("Closed wallets cannot be unfrozen");
        }
        wallet.setStatus(WalletStatus.ACTIVE);
        return mapToResponse(walletRepository.save(wallet));
    }

    private Wallet getOwnedWallet(Long walletId) {
        return walletRepository.findByIdAndUserId(walletId, currentUser().getId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
    }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccountNotFoundException("Authenticated user not found");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user;
        }

        String username = authentication.getName();
        return userRepository.findByEmail(username)
                .or(() -> userRepository.findByPhoneNumber(username))
                .orElseThrow(() -> new AccountNotFoundException("Authenticated user not found"));
    }

    private WalletResponse mapToResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .name(wallet.getName())
                .currency(wallet.getCurrency())
                .status(wallet.getStatus())
                .ledgerAccountCode(wallet.getLedgerAccount() == null ? null : wallet.getLedgerAccount().getCode())
                .ownerId(wallet.getUser().getId())
                .build();
    }

    private String normalizeCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            return "GMD";
        }
        return currency.trim().toUpperCase();
    }

    private String resolveWalletName(String name, String currency) {
        if (name == null || name.isBlank()) {
            return currency + " Wallet";
        }
        return name.trim();
    }

    private String walletLedgerAccountCode(Wallet wallet) {
        return "WALLET:" + wallet.getId() + ":LIABILITY";
    }
}
