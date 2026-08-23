package com.mamadou.payflow.deposit.service;

import com.mamadou.payflow.common.security.SecurityRoleUtils;
import com.mamadou.payflow.deposit.dto.CreateDepositRequest;
import com.mamadou.payflow.deposit.dto.DepositResponse;
import com.mamadou.payflow.deposit.entity.Deposit;
import com.mamadou.payflow.deposit.repository.DepositRepository;
import com.mamadou.payflow.fraud.service.FraudDetectionService;
import com.mamadou.payflow.transaction.entity.Transaction;
import com.mamadou.payflow.transaction.enums.TransactionStatus;
import com.mamadou.payflow.transaction.enums.TransactionType;
import com.mamadou.payflow.transaction.repository.TransactionRepository;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.user.repository.UserRepository;
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.exception.WalletOperationException;
import com.mamadou.payflow.wallet.service.WalletService;
import com.mamadou.payflow.webhook.client.CreateChargeRequest;
import com.mamadou.payflow.webhook.client.CreateChargeResponse;
import com.mamadou.payflow.webhook.client.ModemPayClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepositService {

    private final DepositRepository depositRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final ModemPayClient modemPayClient;
    private final com.mamadou.payflow.idempotency.service.IdempotencyService idempotencyService;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
    private final WalletService walletService;
    private final FraudDetectionService fraudDetectionService;

    @Transactional
    public Deposit createDeposit(CreateDepositRequest request, Long actorId, boolean isAgent) {
        User actor = userRepository.findById(actorId).orElseThrow(() -> new IllegalArgumentException("Actor not found"));
        User targetUser = resolveTargetUser(actor, isAgent, request.getUserId());
        Wallet wallet = resolveWallet(targetUser, request.getWalletId(), request.getCurrency());

        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            var existing = depositRepository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        boolean internalMerchantFlow = isInternalMerchantFlow(actor, request);
        String currency = request.getCurrency() != null && !request.getCurrency().isBlank()
                ? request.getCurrency().trim().toUpperCase()
                : wallet.getCurrency();

        String reference = generateReference();
        fraudDetectionService.checkBeforeExecution(
                reference,
                targetUser,
                wallet,
                request.getAmount(),
                "deposit"
        );

        Deposit deposit = Deposit.builder()
                .wallet(wallet)
                .user(targetUser)
                .agent(isAgent ? actor : null)
                .depositType(isAgent ? Deposit.DepositType.AGENT : Deposit.DepositType.SELF)
                .status(internalMerchantFlow ? Deposit.DepositStatus.AWAITING_AGENT : Deposit.DepositStatus.PENDING)
                .amount(request.getAmount())
                .currency(currency)
                .paymentMethod(internalMerchantFlow ? null : request.getPaymentMethod())
                .phoneNumber(request.getPhoneNumber())
                .description(request.getDescription())
                .reference(reference)
                .idempotencyKey(request.getIdempotencyKey())
                .build();

        deposit = depositRepository.save(deposit);

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
        transactionRepository.save(transaction);

        if (!internalMerchantFlow) {
            initializeModemPayCharge(request, deposit);
        }

        log.info("Deposit created: {} for user {} internal={}", deposit.getReference(), targetUser.getId(), internalMerchantFlow);
        return deposit;
    }

    @Transactional(readOnly = true)
    public List<DepositResponse> listForUser(Long userId) {
        return depositRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(DepositResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DepositResponse getById(Long id, Long userId) {
        Deposit deposit = depositRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Deposit not found"));
        if (deposit.getUser().getId() != userId) {
            throw new IllegalArgumentException("Deposit not found");
        }
        return DepositResponse.from(deposit);
    }

    @Transactional(readOnly = true)
    public DepositResponse getByReference(String reference) {
        Deposit deposit = depositRepository.findByReferenceIgnoreCase(reference)
                .orElseThrow(() -> new IllegalArgumentException("Deposit not found"));
        return DepositResponse.from(deposit);
    }

    private boolean isInternalMerchantFlow(User actor, CreateDepositRequest request) {
        if (SecurityRoleUtils.isMerchant(actor)) {
            return true;
        }
        return request.getPaymentMethod() == null || request.getPaymentMethod().isBlank();
    }

    private User resolveTargetUser(User actor, boolean isAgent, Long userId) {
        if (isAgent && userId != null) {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Target user not found"));
        }
        return actor;
    }

    private Wallet resolveWallet(User targetUser, Long walletId, String currency) {
        if (walletId != null) {
            return walletService.getWalletForUser(walletId, targetUser.getId());
        }
        return walletService.resolvePrimaryWallet(targetUser.getId(), currency);
    }

    private void initializeModemPayCharge(CreateDepositRequest request, Deposit deposit) {
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

            var recordOpt = idempotencyService.findByKey(idempotency);
            if (recordOpt.isPresent() && "COMPLETED".equals(recordOpt.get().getStatus()) && recordOpt.get().getResponsePayload() != null) {
                CreateChargeResponse cached = objectMapper.readValue(recordOpt.get().getResponsePayload(), CreateChargeResponse.class);
                deposit.setExternalPaymentId(cached.getId());
                deposit.setPaymentUrl(cached.getPaymentUrl());
                deposit.setIdempotencyKey(idempotency);
                depositRepository.save(deposit);
            } else {
                idempotencyService.createProcessing(idempotency, buildRequestHash(chargeReq));
                CreateChargeResponse chargeResp = modemPayClient.createCharge(chargeReq);
                if (chargeResp != null) {
                    idempotencyService.complete(idempotency, chargeResp);
                    deposit.setExternalPaymentId(chargeResp.getId());
                    deposit.setPaymentUrl(chargeResp.getPaymentUrl());
                    deposit.setIdempotencyKey(idempotency);
                    depositRepository.save(deposit);
                }
            }
        } catch (Exception ex) {
            log.warn("ModemPay charge creation failed for deposit {}: {}", deposit.getReference(), ex.getMessage());
        }
    }

    private String generateReference() {
        return "dep-" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
    }

    private String buildRequestHash(CreateChargeRequest req) {
        String s = req.getReference() + "|" + req.getAmount() + "|" + req.getCurrency() + "|" + req.getPhoneNumber();
        return Integer.toHexString(s.hashCode());
    }
}
