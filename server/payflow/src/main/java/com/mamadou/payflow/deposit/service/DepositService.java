package com.mamadou.payflow.deposit.service;

import com.mamadou.payflow.deposit.dto.CreateDepositRequest;
import com.mamadou.payflow.deposit.entity.Deposit;
import com.mamadou.payflow.deposit.repository.DepositRepository;
import com.mamadou.payflow.transaction.entity.Transaction;
import com.mamadou.payflow.transaction.enums.TransactionType;
import com.mamadou.payflow.transaction.enums.TransactionStatus;
import com.mamadou.payflow.transaction.repository.TransactionRepository;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.user.repository.UserRepository;
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.repository.WalletRepository;
import com.mamadou.payflow.webhook.service.ModemPayWebhookService;
import com.mamadou.payflow.webhook.client.ModemPayClient;
import com.mamadou.payflow.webhook.client.CreateChargeRequest;
import com.mamadou.payflow.webhook.client.CreateChargeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepositService {

    private final DepositRepository depositRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final ModemPayWebhookService modemPayWebhookService;
    private final ModemPayClient modemPayClient;
    private final com.mamadou.payflow.idempotency.service.IdempotencyService idempotencyService;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    /**
     * Create a deposit record and (optionally) initialize a Modem Pay charge
     * For agent deposits, the agentId parameter is set and userId points to the target user.
     */
    @Transactional
    public Deposit createDeposit(CreateDepositRequest request, Long actorId, boolean isAgent) {
        User actor = userRepository.findById(actorId).orElseThrow(() -> new IllegalArgumentException("Actor not found"));
        User targetUser;
        if (isAgent && request.getUserId() != null) {
            targetUser = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Target user not found"));
        } else {
            targetUser = actor;
        }

        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        // If client provided an idempotency key and a deposit already exists, return it
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            var existing = depositRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        Deposit deposit = Deposit.builder()
                .wallet(wallet)
                .user(targetUser)
                .agent(isAgent ? actor : null)
                .depositType(isAgent ? Deposit.DepositType.AGENT : Deposit.DepositType.SELF)
                .status(Deposit.DepositStatus.PENDING)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .paymentMethod(request.getPaymentMethod())
                .phoneNumber(request.getPhoneNumber())
                .description(request.getDescription())
                .reference(generateReference())
                .idempotencyKey(request.getIdempotencyKey())
                .build();

        deposit = depositRepository.save(deposit);

        // Create a pending Transaction record linked to this deposit.
        // Ledger double-entry will be performed when the provider confirms the payment via webhook.
        Transaction transaction = Transaction.builder()
            .reference(deposit.getReference())
            .type(TransactionType.WALLET_CREDIT)
            .status(TransactionStatus.PENDING)
            .destinationWallet(wallet)
            .initiatedBy(actor)
            .amount(deposit.getAmount())
            .currency(deposit.getCurrency())
            .description("Deposit created: " + deposit.getReference())
            .build();
        transaction = transactionRepository.save(transaction);

        // Create ModemPay charge so the client can complete payment, with idempotency persistence
        try {
            CreateChargeRequest chargeReq = new CreateChargeRequest();
            chargeReq.setAmount(deposit.getAmount());
            chargeReq.setCurrency(deposit.getCurrency());
            chargeReq.setPhoneNumber(deposit.getPhoneNumber());
            chargeReq.setPaymentMethod(deposit.getPaymentMethod());
            chargeReq.setDescription(deposit.getDescription());
            chargeReq.setReference(deposit.getReference());

            String idempotency = request.getIdempotencyKey();
            if (idempotency == null || idempotency.isBlank()) {
                idempotency = "dep-" + deposit.getReference();
            }
            chargeReq.setIdempotencyKey(idempotency);

            // Check persisted idempotency
            var recordOpt = idempotencyService.findByKey(idempotency);
            if (recordOpt.isPresent() && "COMPLETED".equals(recordOpt.get().getStatus()) && recordOpt.get().getResponsePayload() != null) {
                CreateChargeResponse cached = objectMapper.readValue(recordOpt.get().getResponsePayload(), CreateChargeResponse.class);
                deposit.setExternalPaymentId(cached.getId());
                deposit.setPaymentUrl(cached.getPaymentUrl());
                deposit.setIdempotencyKey(idempotency);
                deposit = depositRepository.save(deposit);
            } else {
                idempotencyService.createProcessing(idempotency, buildRequestHash(chargeReq));
                CreateChargeResponse chargeResp = modemPayClient.createCharge(chargeReq);
                if (chargeResp != null) {
                    idempotencyService.complete(idempotency, chargeResp);
                    deposit.setExternalPaymentId(chargeResp.getId());
                    deposit.setPaymentUrl(chargeResp.getPaymentUrl());
                    deposit.setIdempotencyKey(idempotency);
                    deposit = depositRepository.save(deposit);
                }
            }
        } catch (Exception ex) {
            log.warn("ModemPay charge creation failed for deposit {}: {}", deposit.getReference(), ex.getMessage());
        }

        log.info("Deposit created: {} for user {} (agent={})", deposit.getReference(), targetUser.getId(), isAgent);
        return deposit;
    }

    private String generateReference() {
        return "dep-" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
    }

    private String buildRequestHash(CreateChargeRequest req) {
        String s = req.getReference() + "|" + req.getAmount() + "|" + req.getCurrency() + "|" + req.getPhoneNumber();
        return Integer.toHexString(s.hashCode());
    }
}
