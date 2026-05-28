package com.mamadou.payflow.wallet.service;

import com.mamadou.payflow.common.Exception.AccountNotFoundException;
import com.mamadou.payflow.ledger.dto.LedgerPostingRequest;
import com.mamadou.payflow.ledger.dto.LedgerTraceResponse;
import com.mamadou.payflow.ledger.entity.LedgerAccount;
import com.mamadou.payflow.ledger.enums.LedgerAccountType;
import com.mamadou.payflow.ledger.enums.LedgerPostingSide;
import com.mamadou.payflow.ledger.exception.LedgerException;
import com.mamadou.payflow.ledger.service.LedgerAccountService;
import com.mamadou.payflow.ledger.service.LedgerBalanceComputationService;
import com.mamadou.payflow.ledger.service.LedgerService;
import com.mamadou.payflow.transaction.enums.TransactionType;
import com.mamadou.payflow.transaction.service.TransactionService;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.user.repository.UserRepository;
import com.mamadou.payflow.wallet.dto.WalletBalanceResponse;
import com.mamadou.payflow.wallet.dto.WalletTransactionRequest;
import com.mamadou.payflow.wallet.dto.WalletTransactionResponse;
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.entity.WalletTransaction;
import com.mamadou.payflow.wallet.enums.WalletStatus;
import com.mamadou.payflow.wallet.exception.WalletNotFoundException;
import com.mamadou.payflow.wallet.exception.WalletOperationException;
import com.mamadou.payflow.wallet.repository.WalletRepository;
import com.mamadou.payflow.wallet.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletBalanceService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletLimitService walletLimitService;
    private final LedgerService ledgerService;
    private final LedgerAccountService ledgerAccountService;
    private final LedgerBalanceComputationService ledgerBalanceComputationService;
    private final TransactionService transactionService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public WalletBalanceResponse getWalletBalance(Long walletId) {
        Wallet wallet = getOwnedWallet(walletId);
        return WalletBalanceResponse.builder()
                .id(wallet.getId())
                .walletStatus(wallet.getStatus())
                .ledgerAccountCode(requireLedgerAccount(wallet).getCode())
                .balance(ledgerBalanceComputationService.computeBalance(requireLedgerAccount(wallet)))
                .build();
    }

    @Transactional
    public WalletTransactionResponse creditWallet(Long walletId, WalletTransactionRequest request) {
        Wallet wallet = getOwnedWallet(walletId);
        ensureActiveWallet(wallet);
        validateAmount(request.getAmount());

        String reference = resolveReference(request.getReference());
        LedgerAccount walletLedgerAccount = requireLedgerAccount(wallet);
        LedgerAccount platformCashAccount = getPlatformCashAccount(wallet.getCurrency());

        LedgerTraceResponse trace = ledgerService.recordFinancialTransaction(ledgerPostingRequest(
                reference,
                reference,
                wallet.getCurrency(),
                request.getDescription(),
                entry(platformCashAccount.getCode(), LedgerPostingSide.DEBIT, request.getAmount(), "Wallet funding source"),
                entry(walletLedgerAccount.getCode(), LedgerPostingSide.CREDIT, request.getAmount(), "Wallet liability increase")
        ));

        WalletTransaction transaction = saveTransaction(wallet, request, "CREDIT", reference);
        transactionService.createCompletedWalletMovement(
                wallet,
                currentUser(),
                request.getAmount(),
                wallet.getCurrency(),
                request.getDescription(),
                reference,
                trace.getTraceId(),
                TransactionType.WALLET_CREDIT
        );

        return mapToTransactionResponse(transaction);
    }

    @Transactional
    public WalletTransactionResponse debitWallet(Long walletId, WalletTransactionRequest request) {
        Wallet wallet = getOwnedWallet(walletId);
        ensureActiveWallet(wallet);
        validateAmount(request.getAmount());
        walletLimitService.validateDebit(wallet, request.getAmount());

        LedgerAccount walletLedgerAccount = requireLedgerAccount(wallet);
        BigDecimal walletBalance = ledgerBalanceComputationService.computeBalance(walletLedgerAccount);
        if (walletBalance.compareTo(request.getAmount()) < 0) {
            throw new WalletOperationException("Insufficient wallet balance");
        }

        String reference = resolveReference(request.getReference());
        LedgerAccount platformCashAccount = getPlatformCashAccount(wallet.getCurrency());

        LedgerTraceResponse trace = ledgerService.recordFinancialTransaction(ledgerPostingRequest(
                reference,
                reference,
                wallet.getCurrency(),
                request.getDescription(),
                entry(walletLedgerAccount.getCode(), LedgerPostingSide.DEBIT, request.getAmount(), "Wallet liability decrease"),
                entry(platformCashAccount.getCode(), LedgerPostingSide.CREDIT, request.getAmount(), "Wallet funds released")
        ));

        walletLimitService.trackDebitUsage(wallet.getWalletLimit(), request.getAmount());
        WalletTransaction transaction = saveTransaction(wallet, request, "DEBIT", reference);
        transactionService.createCompletedWalletMovement(
                wallet,
                currentUser(),
                request.getAmount(),
                wallet.getCurrency(),
                request.getDescription(),
                reference,
                trace.getTraceId(),
                TransactionType.WALLET_DEBIT
        );

        return mapToTransactionResponse(transaction);
    }

    @Transactional(readOnly = true)
    public List<WalletTransactionResponse> getWalletTransactions(Long walletId) {
        Wallet wallet = getOwnedWallet(walletId);
        return walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId())
                .stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
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

    private WalletTransaction saveTransaction(
            Wallet wallet,
            WalletTransactionRequest request,
            String transactionType,
            String reference
    ) {
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .amount(request.getAmount())
                .transactionType(transactionType)
                .description(request.getDescription())
                .reference(reference)
                .build();
        return walletTransactionRepository.save(transaction);
    }

    private WalletTransactionResponse mapToTransactionResponse(WalletTransaction transaction) {
        return WalletTransactionResponse.builder()
                .id(transaction.getId())
                .walletId(transaction.getWallet().getId())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .description(transaction.getDescription())
                .reference(transaction.getReference())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    private void ensureActiveWallet(Wallet wallet) {
        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new WalletOperationException("Wallet is not active");
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new WalletOperationException("Amount must be greater than zero");
        }
    }

    private String resolveReference(String reference) {
        if (reference == null || reference.isBlank()) {
            return "wallet_" + UUID.randomUUID();
        }
        return reference.trim();
    }

    private LedgerAccount requireLedgerAccount(Wallet wallet) {
        if (wallet.getLedgerAccount() == null) {
            throw new LedgerException("Wallet is not linked to a ledger account");
        }
        return wallet.getLedgerAccount();
    }

    private LedgerAccount getPlatformCashAccount(String currency) {
        return ledgerAccountService.getOrCreateAccountEntity(
                platformCashAccountCode(currency),
                "Platform cash clearing - " + currency,
                LedgerAccountType.ASSET,
                currency
        );
    }

    private String platformCashAccountCode(String currency) {
        return "PLATFORM:CASH:" + currency.toUpperCase();
    }

    private LedgerPostingRequest ledgerPostingRequest(
            String traceId,
            String externalReference,
            String currency,
            String description,
            LedgerPostingRequest.Entry... entries
    ) {
        LedgerPostingRequest request = new LedgerPostingRequest();
        request.setTraceId(traceId);
        request.setExternalReference(externalReference);
        request.setCurrency(currency);
        request.setDescription(description);
        request.setPostings(List.of(entries));
        return request;
    }

    private LedgerPostingRequest.Entry entry(
            String accountCode,
            LedgerPostingSide side,
            BigDecimal amount,
            String description
    ) {
        LedgerPostingRequest.Entry entry = new LedgerPostingRequest.Entry();
        entry.setAccountCode(accountCode);
        entry.setSide(side);
        entry.setAmount(amount);
        entry.setDescription(description);
        return entry;
    }
}
