package com.mamadou.payflow.wallet.service;

import com.mamadou.payflow.common.Exception.AccountNotFoundException;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.user.repository.UserRepository;
import com.mamadou.payflow.wallet.dto.WalletLimitResponse;
import com.mamadou.payflow.wallet.dto.WalletLimitUpdateRequest;
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.entity.WalletLimit;
import com.mamadou.payflow.wallet.enums.KycLevel;
import com.mamadou.payflow.wallet.exception.WalletNotFoundException;
import com.mamadou.payflow.wallet.exception.WalletOperationException;
import com.mamadou.payflow.wallet.repository.WalletLimitRepository;
import com.mamadou.payflow.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletLimitService {

    private final WalletLimitRepository walletLimitRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletLimit createDefaultLimit(Wallet wallet) {
        return walletLimitRepository.save(WalletLimit.builder()
                .wallet(wallet)
                .kycLevel(KycLevel.LEVEL_1)
                .minTransactionAmount(BigDecimal.valueOf(10))
                .maxTransactionAmount(BigDecimal.valueOf(100000))
                .dailyTransactionLimit(BigDecimal.valueOf(200000))
                .weeklyTransactionLimit(BigDecimal.valueOf(1000000))
                .monthlyTransactionLimit(BigDecimal.valueOf(3000000))
                .build());
    }

    @Transactional(readOnly = true)
    public WalletLimitResponse getWalletLimit(Long walletId) {
        Wallet wallet = getOwnedWallet(walletId);
        return toResponse(getLimit(wallet));
    }

    @Transactional
    public WalletLimitResponse updateWalletLimit(Long walletId, WalletLimitUpdateRequest request) {
        Wallet wallet = getOwnedWallet(walletId);
        WalletLimit limit = getLimit(wallet);

        if (request.getMinTransactionAmount() != null) {
            limit.setMinTransactionAmount(request.getMinTransactionAmount());
        }
        if (request.getMaxTransactionAmount() != null) {
            limit.setMaxTransactionAmount(request.getMaxTransactionAmount());
        }
        if (request.getDailyTransactionLimit() != null) {
            limit.setDailyTransactionLimit(request.getDailyTransactionLimit());
        }
        if (request.getWeeklyTransactionLimit() != null) {
            limit.setWeeklyTransactionLimit(request.getWeeklyTransactionLimit());
        }
        if (request.getMonthlyTransactionLimit() != null) {
            limit.setMonthlyTransactionLimit(request.getMonthlyTransactionLimit());
        }
        if (request.getKycLevel() != null) {
            limit.setKycLevel(request.getKycLevel());
        }
        if (request.getActive() != null) {
            limit.setActive(request.getActive());
        }

        validateLimitConfiguration(limit);
        return toResponse(walletLimitRepository.save(limit));
    }

    @Transactional
    public WalletLimitResponse resetUsage(Long walletId) {
        Wallet wallet = getOwnedWallet(walletId);
        WalletLimit limit = getLimit(wallet);
        limit.setAmountSpentToday(BigDecimal.ZERO);
        limit.setAmountSpentThisWeek(BigDecimal.ZERO);
        limit.setAmountSpentThisMonth(BigDecimal.ZERO);
        return toResponse(walletLimitRepository.save(limit));
    }

    public void validateDebit(Wallet wallet, BigDecimal amount) {
        WalletLimit limit = wallet.getWalletLimit();
        if (limit == null || !limit.isActive()) {
            return;
        }
        if (amount.compareTo(limit.getMinTransactionAmount()) < 0) {
            throw new WalletOperationException("Amount is below the minimum transaction amount");
        }
        if (amount.compareTo(limit.getMaxTransactionAmount()) > 0) {
            throw new WalletOperationException("Amount exceeds the maximum transaction amount");
        }
        if (limit.getDailyTransactionLimit() != null
                && limit.getAmountSpentToday().add(amount).compareTo(limit.getDailyTransactionLimit()) > 0) {
            throw new WalletOperationException("Amount exceeds the daily transaction limit");
        }
        if (limit.getWeeklyTransactionLimit() != null
                && limit.getAmountSpentThisWeek().add(amount).compareTo(limit.getWeeklyTransactionLimit()) > 0) {
            throw new WalletOperationException("Amount exceeds the weekly transaction limit");
        }
        if (limit.getMonthlyTransactionLimit() != null
                && limit.getAmountSpentThisMonth().add(amount).compareTo(limit.getMonthlyTransactionLimit()) > 0) {
            throw new WalletOperationException("Amount exceeds the monthly transaction limit");
        }
    }

    public void trackDebitUsage(WalletLimit limit, BigDecimal amount) {
        if (limit == null || !limit.isActive()) {
            return;
        }
        limit.setAmountSpentToday(limit.getAmountSpentToday().add(amount));
        limit.setAmountSpentThisWeek(limit.getAmountSpentThisWeek().add(amount));
        limit.setAmountSpentThisMonth(limit.getAmountSpentThisMonth().add(amount));
        walletLimitRepository.save(limit);
    }

    private WalletLimit getLimit(Wallet wallet) {
        return walletLimitRepository.findByWalletId(wallet.getId())
                .orElseThrow(() -> new WalletOperationException("Wallet limit not found"));
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

    private void validateLimitConfiguration(WalletLimit limit) {
        if (limit.getMinTransactionAmount().compareTo(limit.getMaxTransactionAmount()) > 0) {
            throw new WalletOperationException("Minimum transaction amount cannot exceed maximum transaction amount");
        }
    }

    private WalletLimitResponse toResponse(WalletLimit limit) {
        return WalletLimitResponse.builder()
                .id(limit.getId())
                .walletId(limit.getWallet().getId())
                .minTransactionAmount(limit.getMinTransactionAmount())
                .maxTransactionAmount(limit.getMaxTransactionAmount())
                .dailyTransactionLimit(limit.getDailyTransactionLimit())
                .weeklyTransactionLimit(limit.getWeeklyTransactionLimit())
                .monthlyTransactionLimit(limit.getMonthlyTransactionLimit())
                .amountSpentToday(limit.getAmountSpentToday())
                .amountSpentThisWeek(limit.getAmountSpentThisWeek())
                .amountSpentThisMonth(limit.getAmountSpentThisMonth())
                .kycLevel(limit.getKycLevel())
                .active(limit.isActive())
                .lastResetAt(limit.getLastResetAt())
                .build();
    }
}
