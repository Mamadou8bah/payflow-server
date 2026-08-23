package com.mamadou.payflow.wallet.service;

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
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.entity.WalletTransaction;
import com.mamadou.payflow.wallet.enums.WalletStatus;
import com.mamadou.payflow.wallet.exception.WalletOperationException;
import com.mamadou.payflow.wallet.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LedgerWalletMovementService {

    private final LedgerService ledgerService;
    private final LedgerAccountService ledgerAccountService;
    private final LedgerBalanceComputationService ledgerBalanceComputationService;
    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletLimitService walletLimitService;
    private final TransactionService transactionService;

    @Transactional
    public LedgerTraceResponse creditWallet(Wallet wallet, User owner, BigDecimal amount, String reference, String description) {
        ensureActiveWallet(wallet);
        validateAmount(amount);

        LedgerAccount walletLedgerAccount = requireLedgerAccount(wallet);
        LedgerAccount platformCashAccount = getPlatformCashAccount(wallet.getCurrency());

        LedgerTraceResponse trace = ledgerService.recordFinancialTransaction(ledgerPostingRequest(
                reference,
                reference,
                wallet.getCurrency(),
                description,
                entry(platformCashAccount.getCode(), LedgerPostingSide.DEBIT, amount, "Internal wallet funding"),
                entry(walletLedgerAccount.getCode(), LedgerPostingSide.CREDIT, amount, "Wallet liability increase")
        ));

        walletTransactionRepository.save(WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount)
                .transactionType("CREDIT")
                .description(description)
                .reference(reference)
                .build());

        transactionService.createCompletedWalletMovement(
                wallet,
                owner,
                amount,
                wallet.getCurrency(),
                description,
                reference,
                trace.getTraceId(),
                TransactionType.WALLET_CREDIT
        );

        return trace;
    }

    @Transactional
    public LedgerTraceResponse debitWallet(Wallet wallet, User owner, BigDecimal amount, String reference, String description) {
        ensureActiveWallet(wallet);
        validateAmount(amount);
        walletLimitService.validateDebit(wallet, amount);

        LedgerAccount walletLedgerAccount = requireLedgerAccount(wallet);
        BigDecimal walletBalance = ledgerBalanceComputationService.computeBalance(walletLedgerAccount);
        if (walletBalance.compareTo(amount) < 0) {
            throw new WalletOperationException("Insufficient wallet balance");
        }

        LedgerAccount platformCashAccount = getPlatformCashAccount(wallet.getCurrency());
        LedgerTraceResponse trace = ledgerService.recordFinancialTransaction(ledgerPostingRequest(
                reference,
                reference,
                wallet.getCurrency(),
                description,
                entry(walletLedgerAccount.getCode(), LedgerPostingSide.DEBIT, amount, "Wallet liability decrease"),
                entry(platformCashAccount.getCode(), LedgerPostingSide.CREDIT, amount, "Internal wallet payout")
        ));

        walletLimitService.trackDebitUsage(wallet.getWalletLimit(), amount);
        walletTransactionRepository.save(WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount)
                .transactionType("DEBIT")
                .description(description)
                .reference(reference)
                .build());

        transactionService.createCompletedWalletMovement(
                wallet,
                owner,
                amount,
                wallet.getCurrency(),
                description,
                reference,
                trace.getTraceId(),
                TransactionType.WALLET_DEBIT
        );

        return trace;
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

    private LedgerAccount requireLedgerAccount(Wallet wallet) {
        if (wallet.getLedgerAccount() == null) {
            throw new LedgerException("Wallet is not linked to a ledger account");
        }
        return wallet.getLedgerAccount();
    }

    private LedgerAccount getPlatformCashAccount(String currency) {
        return ledgerAccountService.getOrCreateAccountEntity(
                "PLATFORM:CASH:" + currency.toUpperCase(),
                "Platform cash clearing - " + currency,
                LedgerAccountType.ASSET,
                currency
        );
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
