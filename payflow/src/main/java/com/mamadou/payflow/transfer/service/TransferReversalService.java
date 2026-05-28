package com.mamadou.payflow.transfer.service;

import com.mamadou.payflow.common.Exception.AccountNotFoundException;
import com.mamadou.payflow.ledger.dto.LedgerPostingRequest;
import com.mamadou.payflow.ledger.dto.LedgerTraceResponse;
import com.mamadou.payflow.ledger.enums.LedgerPostingSide;
import com.mamadou.payflow.ledger.service.LedgerService;
import com.mamadou.payflow.transaction.dto.ReversalRequest;
import com.mamadou.payflow.transaction.dto.TransactionResponse;
import com.mamadou.payflow.transaction.entity.Transaction;
import com.mamadou.payflow.transaction.enums.TransactionStatus;
import com.mamadou.payflow.transaction.enums.TransactionType;
import com.mamadou.payflow.transaction.exception.TransactionException;
import com.mamadou.payflow.transaction.service.IdempotencyService;
import com.mamadou.payflow.transaction.service.TransactionService;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.user.repository.UserRepository;
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.exception.WalletNotFoundException;
import com.mamadou.payflow.wallet.exception.WalletOperationException;
import com.mamadou.payflow.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferReversalService {

    private final TransactionService transactionService;
    private final IdempotencyService idempotencyService;
    private final WalletRepository walletRepository;
    private final TransferValidationService transferValidationService;
    private final LedgerService ledgerService;
    private final UserRepository userRepository;

    @Transactional
    public TransactionResponse reverse(Long transactionId, ReversalRequest request) {
        ReversalRequest reversalRequest = request == null ? new ReversalRequest() : request;
        Transaction original = transactionService.getEntity(transactionId);
        String fingerprint = "reverse|" + transactionId + "|" + nullToBlank(reversalRequest.getReason());

        return idempotencyService.findExistingTransaction(reversalRequest.getIdempotencyKey(), fingerprint)
                .map(transactionService::toResponse)
                .orElseGet(() -> {
                    User currentUser = currentUser();
                    validateOriginal(original, currentUser);

                    LockedWallets lockedWallets = lockWallets(
                            original.getDestinationWallet().getId(),
                            original.getSourceWallet().getId()
                    );
                    Wallet refundSource = lockedWallets.refundSource();
                    Wallet refundDestination = lockedWallets.refundDestination();

                    transferValidationService.ensureSufficientBalance(refundSource, original.getAmount());

                    Transaction reversal = transactionService.createPendingReversal(
                            original,
                            currentUser,
                            resolveReason(reversalRequest),
                            null
                    );

                    try {
                        LedgerTraceResponse trace = ledgerService.recordFinancialTransaction(ledgerPostingRequest(
                                reversal.getReference(),
                                original.getReference(),
                                reversal.getCurrency(),
                                reversal.getDescription(),
                                entry(refundSource.getLedgerAccount().getCode(), LedgerPostingSide.DEBIT, reversal.getAmount(), "Transfer reversal debit"),
                                entry(refundDestination.getLedgerAccount().getCode(), LedgerPostingSide.CREDIT, reversal.getAmount(), "Transfer reversal credit")
                        ));
                        Transaction completed = transactionService.completeReversal(original, reversal, trace.getTraceId());
                        idempotencyService.saveKey(reversalRequest.getIdempotencyKey(), fingerprint, completed);
                        return transactionService.toResponse(completed);
                    } catch (RuntimeException exception) {
                        transactionService.markFailed(reversal, exception.getMessage());
                        throw exception;
                    }
                });
    }

    private void validateOriginal(Transaction original, User currentUser) {
        if (original.getType() != TransactionType.TRANSFER) {
            throw new TransactionException("Only wallet transfers can be reversed");
        }
        if (original.getStatus() != TransactionStatus.COMPLETED) {
            throw new TransactionException("Only completed transfers can be reversed");
        }
        if (original.getReversalTransaction() != null || original.getReversedAt() != null) {
            throw new TransactionException("Transfer has already been reversed");
        }
        if (original.getSourceWallet() == null
                || original.getSourceWallet().getUser().getId() != currentUser.getId()) {
            throw new WalletOperationException("Only the source wallet owner can reverse this transfer");
        }
    }

    private String resolveReason(ReversalRequest request) {
        if (request.getReason() == null || request.getReason().isBlank()) {
            return "Transfer reversal";
        }
        return request.getReason().trim();
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

    private String nullToBlank(String value) {
        return value == null ? "" : value.trim();
    }

    private LockedWallets lockWallets(Long refundSourceId, Long refundDestinationId) {
        Wallet firstWallet;
        Wallet secondWallet;
        if (refundSourceId < refundDestinationId) {
            firstWallet = getLockedWallet(refundSourceId, "Destination wallet not found");
            secondWallet = getLockedWallet(refundDestinationId, "Source wallet not found");
        } else {
            firstWallet = getLockedWallet(refundDestinationId, "Source wallet not found");
            secondWallet = getLockedWallet(refundSourceId, "Destination wallet not found");
        }

        Wallet refundSource = firstWallet.getId().equals(refundSourceId) ? firstWallet : secondWallet;
        Wallet refundDestination = firstWallet.getId().equals(refundDestinationId) ? firstWallet : secondWallet;
        return new LockedWallets(refundSource, refundDestination);
    }

    private Wallet getLockedWallet(Long walletId, String errorMessage) {
        return walletRepository.findWithLockById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(errorMessage));
    }

    private record LockedWallets(Wallet refundSource, Wallet refundDestination) {
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
