package com.mamadou.payflow.withdrawal.service;

import com.mamadou.payflow.withdrawal.dto.CreateWithdrawalRequest;
import com.mamadou.payflow.withdrawal.entity.Withdrawal;
import com.mamadou.payflow.withdrawal.repository.WithdrawalRepository;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.transaction.entity.Transaction;
import com.mamadou.payflow.transaction.enums.TransactionType;
import com.mamadou.payflow.transaction.enums.TransactionStatus;
import com.mamadou.payflow.transaction.repository.TransactionRepository;
import com.mamadou.payflow.user.repository.UserRepository;
import com.mamadou.payflow.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mamadou.payflow.webhook.client.ModemPayClient;
import com.mamadou.payflow.webhook.client.CreatePayoutRequest;
import com.mamadou.payflow.webhook.client.CreatePayoutResponse;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final ModemPayClient modemPayClient;
    private final com.mamadou.payflow.idempotency.service.IdempotencyService idempotencyService;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    @Transactional
    public Withdrawal createWithdrawal(CreateWithdrawalRequest request, Long actorId, boolean isAgent) {
        User actor = userRepository.findById(actorId).orElseThrow(() -> new IllegalArgumentException("Actor not found"));
        User targetUser;
        if (isAgent && request.getUserId() != null) {
            targetUser = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Target user not found"));
        } else {
            targetUser = actor;
        }

        var wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        // If client supplied idempotency key and a withdrawal exists, return it
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            var existing = withdrawalRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        Withdrawal withdrawal = Withdrawal.builder()
                .wallet(wallet)
                .user(targetUser)
                .agent(isAgent ? actor : null)
                .withdrawalType(isAgent ? Withdrawal.WithdrawalType.AGENT : Withdrawal.WithdrawalType.SELF)
                .status(Withdrawal.WithdrawalStatus.PENDING)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .withdrawalMethod(request.getWithdrawalMethod())
                .phoneNumber(request.getPhoneNumber())
                .bankAccount(request.getBankAccount())
                .description(request.getDescription())
                .reference(generateReference())
                .idempotencyKey(request.getIdempotencyKey())
                .build();

        withdrawal = withdrawalRepository.save(withdrawal);

        // Create a pending Transaction record for this withdrawal (ledger posting will occur on payout webhook)
        Transaction transaction = Transaction.builder()
            .reference(withdrawal.getReference())
            .type(TransactionType.WALLET_DEBIT)
            .status(TransactionStatus.PENDING)
            .sourceWallet(wallet)
            .initiatedBy(actor)
            .amount(withdrawal.getAmount())
            .currency(withdrawal.getCurrency())
            .description("Withdrawal created: " + withdrawal.getReference())
            .build();
        transactionRepository.save(transaction);

        // Optionally create a payout via ModemPay (if configured)
        try {
            CreatePayoutRequest payoutReq = new CreatePayoutRequest();
            payoutReq.setAmount(withdrawal.getAmount());
            payoutReq.setCurrency(withdrawal.getCurrency());
            payoutReq.setPhoneNumber(withdrawal.getPhoneNumber());
            payoutReq.setBankAccount(withdrawal.getBankAccount());
            payoutReq.setWithdrawalMethod(withdrawal.getWithdrawalMethod());
            payoutReq.setDescription(withdrawal.getDescription());
            String idemp = request.getIdempotencyKey();
            if (idemp == null || idemp.isBlank()) {
                idemp = "wd-" + withdrawal.getReference();
            }
            payoutReq.setIdempotencyKey(idemp);
            payoutReq.setReference(withdrawal.getReference());

            var recordOpt = idempotencyService.findByKey(idemp);
            if (recordOpt.isPresent() && "COMPLETED".equals(recordOpt.get().getStatus()) && recordOpt.get().getResponsePayload() != null) {
                com.mamadou.payflow.webhook.client.CreatePayoutResponse cached = objectMapper.readValue(recordOpt.get().getResponsePayload(), com.mamadou.payflow.webhook.client.CreatePayoutResponse.class);
                withdrawal.setExternalPaymentId(cached.getId());
                withdrawal.setStatus(Withdrawal.WithdrawalStatus.PROCESSING);
                withdrawalRepository.save(withdrawal);
            } else {
                idempotencyService.createProcessing(idemp, buildPayoutHash(payoutReq));
                CreatePayoutResponse payoutResp = modemPayClient.createPayout(payoutReq);
                if (payoutResp != null) {
                    idempotencyService.complete(idemp, payoutResp);
                    withdrawal.setExternalPaymentId(payoutResp.getId());
                    withdrawal.setStatus(Withdrawal.WithdrawalStatus.PROCESSING);
                    withdrawalRepository.save(withdrawal);
                }
            }
        } catch (Exception ex) {
            log.warn("ModemPay payout creation failed for withdrawal {}: {}", withdrawal.getReference(), ex.getMessage());
        }

        log.info("Withdrawal created: {} for user {} (agent={})", withdrawal.getReference(), targetUser.getId(), isAgent);
        return withdrawal;
    }

    private String generateReference() {
        return "wd-" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
    }

    private String buildPayoutHash(CreatePayoutRequest req) {
        String s = req.getReference() + "|" + req.getAmount() + "|" + req.getCurrency() + "|" + req.getBankAccount() + "|" + req.getPhoneNumber();
        return Integer.toHexString(s.hashCode());
    }
}
