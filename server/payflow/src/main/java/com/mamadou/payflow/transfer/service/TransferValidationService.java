package com.mamadou.payflow.transfer.service;

import com.mamadou.payflow.common.Exception.AccountNotFoundException;
import com.mamadou.payflow.ledger.entity.LedgerAccount;
import com.mamadou.payflow.ledger.exception.LedgerException;
import com.mamadou.payflow.ledger.service.LedgerBalanceComputationService;
import com.mamadou.payflow.transfer.dto.TransferRequest;
import com.mamadou.payflow.transfer.dto.TransferValidationResult;
import com.mamadou.payflow.transfer.validator.TransferRequestValidator;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.user.repository.UserRepository;
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.enums.WalletStatus;
import com.mamadou.payflow.wallet.exception.WalletNotFoundException;
import com.mamadou.payflow.wallet.exception.WalletOperationException;
import com.mamadou.payflow.wallet.repository.WalletRepository;
import com.mamadou.payflow.wallet.service.WalletLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferValidationService {

    private final WalletRepository walletRepository;
    private final WalletLimitService walletLimitService;
    private final LedgerBalanceComputationService ledgerBalanceComputationService;
    private final TransferRequestValidator transferRequestValidator;
    private final UserRepository userRepository;

    public TransferValidationResult validateForExecution(TransferRequest request) {
        transferRequestValidator.validate(request);
        User currentUser = currentUser();
        LockedWallets lockedWallets = lockWallets(request.getSourceWalletId(), request.getDestinationWalletId());
        Wallet sourceWallet = lockedWallets.sourceWallet();
        Wallet destinationWallet = lockedWallets.destinationWallet();
        if (sourceWallet.getUser().getId() != currentUser.getId()) {
            throw new WalletNotFoundException("Source wallet not found");
        }

        validateWallets(sourceWallet, destinationWallet);
        walletLimitService.validateDebit(sourceWallet, request.getAmount());
        ensureSufficientBalance(sourceWallet, request.getAmount());

        return TransferValidationResult.builder()
                .sourceWallet(sourceWallet)
                .destinationWallet(destinationWallet)
                .initiatedBy(currentUser)
                .build();
    }

    public void ensureSufficientBalance(Wallet wallet, BigDecimal amount) {
        LedgerAccount ledgerAccount = requireLedgerAccount(wallet);
        BigDecimal balance = ledgerBalanceComputationService.computeBalance(ledgerAccount);
        if (balance.compareTo(amount) < 0) {
            throw new WalletOperationException("Insufficient wallet balance");
        }
    }

    private void validateWallets(Wallet sourceWallet, Wallet destinationWallet) {
        if (sourceWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new WalletOperationException("Source wallet is not active");
        }
        if (destinationWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new WalletOperationException("Destination wallet is not active");
        }
        if (!sourceWallet.getCurrency().equalsIgnoreCase(destinationWallet.getCurrency())) {
            throw new WalletOperationException("Wallet currencies must match");
        }
        requireLedgerAccount(sourceWallet);
        requireLedgerAccount(destinationWallet);
    }

    private LedgerAccount requireLedgerAccount(Wallet wallet) {
        if (wallet.getLedgerAccount() == null) {
            throw new LedgerException("Wallet is not linked to a ledger account");
        }
        return wallet.getLedgerAccount();
    }

    private LockedWallets lockWallets(Long sourceWalletId, Long destinationWalletId) {
        Wallet firstWallet;
        Wallet secondWallet;
        if (sourceWalletId < destinationWalletId) {
            firstWallet = getLockedWallet(sourceWalletId, "Source wallet not found");
            secondWallet = getLockedWallet(destinationWalletId, "Destination wallet not found");
        } else {
            firstWallet = getLockedWallet(destinationWalletId, "Destination wallet not found");
            secondWallet = getLockedWallet(sourceWalletId, "Source wallet not found");
        }

        Wallet sourceWallet = firstWallet.getId().equals(sourceWalletId) ? firstWallet : secondWallet;
        Wallet destinationWallet = firstWallet.getId().equals(destinationWalletId) ? firstWallet : secondWallet;
        return new LockedWallets(sourceWallet, destinationWallet);
    }

    private Wallet getLockedWallet(Long walletId, String errorMessage) {
        return walletRepository.findWithLockById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(errorMessage));
    }

    private record LockedWallets(Wallet sourceWallet, Wallet destinationWallet) {
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
}
