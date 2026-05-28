package com.mamadou.payflow.webhook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mamadou.payflow.ledger.dto.LedgerPostingRequest;
import com.mamadou.payflow.ledger.dto.LedgerTraceResponse;
import com.mamadou.payflow.ledger.entity.LedgerAccount;
import com.mamadou.payflow.ledger.enums.LedgerAccountType;
import com.mamadou.payflow.ledger.enums.LedgerPostingSide;
import com.mamadou.payflow.ledger.exception.LedgerException;
import com.mamadou.payflow.ledger.service.LedgerAccountService;
import com.mamadou.payflow.ledger.service.LedgerService;
import com.mamadou.payflow.transaction.enums.TransactionType;
import com.mamadou.payflow.transaction.service.TransactionService;
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.entity.WalletTransaction;
import com.mamadou.payflow.wallet.enums.WalletStatus;
import com.mamadou.payflow.wallet.exception.WalletNotFoundException;
import com.mamadou.payflow.wallet.exception.WalletOperationException;
import com.mamadou.payflow.wallet.repository.WalletRepository;
import com.mamadou.payflow.wallet.repository.WalletTransactionRepository;
import com.mamadou.payflow.webhook.dto.DepositWebhookRequest;
import com.mamadou.payflow.webhook.entity.WebhookEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepositProcessingService {

    private final ObjectMapper objectMapper;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final LedgerAccountService ledgerAccountService;
    private final LedgerService ledgerService;
    private final TransactionService transactionService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process(WebhookEvent event) {
        DepositWebhookRequest request = parse(event.getRawPayload());
        validate(request);

        if (!isSuccessfulDeposit(request)) {
            event.setStatus("IGNORED");
            event.setFailureReason("Deposit status is not successful: " + request.status());
            return;
        }

        Wallet wallet = walletRepository.findWithLockById(request.walletId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new WalletOperationException("Wallet is not active");
        }
        if (!wallet.getCurrency().equalsIgnoreCase(request.currency())) {
            throw new WalletOperationException("Webhook currency does not match wallet currency");
        }

        LedgerAccount walletLedgerAccount = requireLedgerAccount(wallet);
        LedgerAccount clearingAccount = ledgerAccountService.getOrCreateAccountEntity(
                "MODEMPAY:CLEARING:" + wallet.getCurrency().toUpperCase(),
                "ModemPay clearing - " + wallet.getCurrency().toUpperCase(),
                LedgerAccountType.ASSET,
                wallet.getCurrency()
        );

        String reference = resolveReference(request);
        LedgerTraceResponse trace = ledgerService.recordFinancialTransaction(ledgerPostingRequest(
                reference,
                request.providerTransactionId(),
                wallet.getCurrency(),
                "ModemPay deposit",
                entry(clearingAccount.getCode(), LedgerPostingSide.DEBIT, request.amount(), "ModemPay deposit received"),
                entry(walletLedgerAccount.getCode(), LedgerPostingSide.CREDIT, request.amount(), "Wallet funded by ModemPay")
        ));

        walletTransactionRepository.save(WalletTransaction.builder()
                .wallet(wallet)
                .amount(request.amount())
                .transactionType("CREDIT")
                .description("ModemPay deposit")
                .reference(reference)
                .build());

        transactionService.createCompletedWalletMovement(
                wallet,
                wallet.getUser(),
                request.amount(),
                wallet.getCurrency(),
                "ModemPay deposit",
                reference,
                trace.getTraceId(),
                TransactionType.WALLET_CREDIT
        );
    }

    public DepositWebhookRequest parse(String rawPayload) {
        try {
            return objectMapper.readValue(rawPayload, DepositWebhookRequest.class);
        } catch (Exception ex) {
            throw new WalletOperationException("Invalid deposit webhook payload");
        }
    }

    private void validate(DepositWebhookRequest request) {
        if (request.eventId() == null || request.eventId().isBlank()) {
            throw new WalletOperationException("Webhook event id is required");
        }
        if (request.walletId() == null) {
            throw new WalletOperationException("Wallet id is required");
        }
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new WalletOperationException("Deposit amount must be greater than zero");
        }
        if (request.currency() == null || request.currency().isBlank()) {
            throw new WalletOperationException("Deposit currency is required");
        }
    }

    private boolean isSuccessfulDeposit(DepositWebhookRequest request) {
        if (request.status() == null) {
            return false;
        }
        String status = request.status().trim().toUpperCase();
        return status.equals("SUCCESS") || status.equals("SUCCEEDED") || status.equals("COMPLETED") || status.equals("PAID");
    }

    private String resolveReference(DepositWebhookRequest request) {
        if (request.reference() != null && !request.reference().isBlank()) {
            return request.reference().trim();
        }
        if (request.providerTransactionId() != null && !request.providerTransactionId().isBlank()) {
            return request.providerTransactionId().trim();
        }
        return "modempay_" + request.eventId().trim();
    }

    private LedgerAccount requireLedgerAccount(Wallet wallet) {
        if (wallet.getLedgerAccount() == null) {
            throw new LedgerException("Wallet is not linked to a ledger account");
        }
        return wallet.getLedgerAccount();
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
