package com.mamadou.payflow.webhook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mamadou.payflow.ledger.dto.LedgerPostingRequest;
import com.mamadou.payflow.ledger.dto.LedgerTraceResponse;
import com.mamadou.payflow.ledger.entity.LedgerAccount;
import com.mamadou.payflow.ledger.enums.LedgerAccountType;
import com.mamadou.payflow.ledger.enums.LedgerPostingSide;
import com.mamadou.payflow.ledger.service.LedgerAccountService;
import com.mamadou.payflow.ledger.service.LedgerService;
import com.mamadou.payflow.transaction.enums.TransactionType;
import com.mamadou.payflow.transaction.service.TransactionService;
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.entity.WalletTransaction;
import com.mamadou.payflow.wallet.repository.WalletRepository;
import com.mamadou.payflow.wallet.repository.WalletTransactionRepository;
import com.mamadou.payflow.withdrawal.entity.Withdrawal;
import com.mamadou.payflow.withdrawal.repository.WithdrawalRepository;
import com.mamadou.payflow.webhook.dto.ModemPayWebhookPayload;
import com.mamadou.payflow.webhook.entity.WebhookEvent;
import com.mamadou.payflow.wallet.enums.WalletStatus;
import com.mamadou.payflow.wallet.exception.WalletNotFoundException;
import com.mamadou.payflow.wallet.exception.WalletOperationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WithdrawalProcessingService {

    private final ObjectMapper objectMapper;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final LedgerAccountService ledgerAccountService;
    private final LedgerService ledgerService;
    private final TransactionService transactionService;
    private final WithdrawalRepository withdrawalRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process(ModemPayWebhookPayload payload) {
        ModemPayWebhookPayload.ModemPayChargeData data = payload.getData();
        String reference = data.getReference() != null && !data.getReference().isBlank()
                ? data.getReference().trim() : (data.getChargeId() != null ? data.getChargeId() : "payout_" + payload.getTimestamp());

        Optional<Withdrawal> withdrawalOpt = withdrawalRepository.findByReferenceIgnoreCase(reference);
        if (withdrawalOpt.isEmpty()) {
            return; // nothing to do
        }
        Withdrawal withdrawal = withdrawalOpt.get();

        if (withdrawal.getStatus() == Withdrawal.WithdrawalStatus.COMPLETED) {
            return;
        }

        Wallet wallet = walletRepository.findWithLockById(withdrawal.getWallet().getId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new WalletOperationException("Wallet is not active");
        }

        LedgerAccount walletLedgerAccount = wallet.getLedgerAccount();
        if (walletLedgerAccount == null) {
            throw new RuntimeException("Wallet is not linked to a ledger account");
        }

        LedgerAccount clearingAccount = ledgerAccountService.getOrCreateAccountEntity(
                "MODEMPAY:CLEARING:" + withdrawal.getCurrency().toUpperCase(),
                "ModemPay clearing - " + withdrawal.getCurrency().toUpperCase(),
                LedgerAccountType.ASSET,
                withdrawal.getCurrency()
        );

        BigDecimal amount = data.getAmount();

        // Record ledger postings: debit wallet (asset decrease), credit clearing/settlement
        LedgerTraceResponse trace = ledgerService.recordFinancialTransaction(ledgerPostingRequest(
                reference,
                data.getChargeId(),
                withdrawal.getCurrency(),
                "ModemPay payout",
                entry(walletLedgerAccount.getCode(), LedgerPostingSide.DEBIT, amount, "Wallet debited for payout"),
                entry(clearingAccount.getCode(), LedgerPostingSide.CREDIT, amount, "ModemPay payout settled")
        ));

        // Save wallet transaction (debit)
        walletTransactionRepository.save(WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount)
                .transactionType("DEBIT")
                .description("ModemPay payout")
                .reference(reference)
                .build());

        // Mark withdrawal completed and attach ledger trace
        withdrawal.setStatus(Withdrawal.WithdrawalStatus.COMPLETED);
        withdrawal.setCompletedAt(LocalDateTime.now());
        withdrawal.setExternalPaymentId(data.getChargeId());
        withdrawalRepository.save(withdrawal);

        // Create completed transaction record
        transactionService.createCompletedWalletMovement(
                wallet,
                wallet.getUser(),
                amount,
                withdrawal.getCurrency(),
                "ModemPay payout",
                reference,
                trace.getTraceId(),
                TransactionType.WALLET_DEBIT
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
        request.setPostings(java.util.List.of(entries));
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
