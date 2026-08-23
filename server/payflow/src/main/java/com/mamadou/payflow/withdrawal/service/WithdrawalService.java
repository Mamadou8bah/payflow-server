package com.mamadou.payflow.withdrawal.service;

import com.mamadou.payflow.common.security.SecurityRoleUtils;
import com.mamadou.payflow.fraud.service.FraudDetectionService;
import com.mamadou.payflow.transaction.entity.Transaction;
import com.mamadou.payflow.transaction.enums.TransactionStatus;
import com.mamadou.payflow.transaction.enums.TransactionType;
import com.mamadou.payflow.transaction.repository.TransactionRepository;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.user.repository.UserRepository;
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.service.WalletService;
import com.mamadou.payflow.webhook.client.CreatePayoutRequest;
import com.mamadou.payflow.webhook.client.CreatePayoutResponse;
import com.mamadou.payflow.webhook.client.ModemPayClient;
import com.mamadou.payflow.withdrawal.dto.CreateWithdrawalRequest;
import com.mamadou.payflow.withdrawal.dto.WithdrawalResponse;
import com.mamadou.payflow.withdrawal.entity.Withdrawal;
import com.mamadou.payflow.withdrawal.repository.WithdrawalRepository;
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
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final ModemPayClient modemPayClient;
    private final com.mamadou.payflow.idempotency.service.IdempotencyService idempotencyService;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
    private final WalletService walletService;
    private final FraudDetectionService fraudDetectionService;

    @Transactional
    public Withdrawal createWithdrawal(CreateWithdrawalRequest request, Long actorId, boolean isAgent) {
        User actor = userRepository.findById(actorId).orElseThrow(() -> new IllegalArgumentException("Actor not found"));
        User targetUser = resolveTargetUser(actor, isAgent, request.getUserId());
        Wallet wallet = resolveWallet(targetUser, request.getWalletId(), request.getCurrency());

        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            var existing = withdrawalRepository.findByIdempotencyKey(request.getIdempotencyKey());
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
                "withdrawal"
        );

        Withdrawal withdrawal = Withdrawal.builder()
                .wallet(wallet)
                .user(targetUser)
                .agent(isAgent ? actor : null)
                .withdrawalType(isAgent ? Withdrawal.WithdrawalType.AGENT : Withdrawal.WithdrawalType.SELF)
                .status(internalMerchantFlow ? Withdrawal.WithdrawalStatus.AWAITING_AGENT : Withdrawal.WithdrawalStatus.PENDING)
                .amount(request.getAmount())
                .currency(currency)
                .withdrawalMethod(internalMerchantFlow ? null : request.getWithdrawalMethod())
                .phoneNumber(request.getPhoneNumber())
                .bankAccount(request.getBankAccount())
                .description(request.getDescription())
                .reference(reference)
                .idempotencyKey(request.getIdempotencyKey())
                .build();

        withdrawal = withdrawalRepository.save(withdrawal);

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

        if (!internalMerchantFlow) {
            initializeModemPayPayout(request, withdrawal);
        }

        log.info("Withdrawal created: {} for user {} internal={}", withdrawal.getReference(), targetUser.getId(), internalMerchantFlow);
        return withdrawal;
    }

    @Transactional(readOnly = true)
    public List<WithdrawalResponse> listForUser(Long userId) {
        return withdrawalRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(WithdrawalResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WithdrawalResponse getById(Long id, Long userId) {
        Withdrawal withdrawal = withdrawalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Withdrawal not found"));
        if (withdrawal.getUser().getId() != userId) {
            throw new IllegalArgumentException("Withdrawal not found");
        }
        return WithdrawalResponse.from(withdrawal);
    }

    @Transactional(readOnly = true)
    public WithdrawalResponse getByReference(String reference) {
        Withdrawal withdrawal = withdrawalRepository.findByReferenceIgnoreCase(reference)
                .orElseThrow(() -> new IllegalArgumentException("Withdrawal not found"));
        return WithdrawalResponse.from(withdrawal);
    }

    private boolean isInternalMerchantFlow(User actor, CreateWithdrawalRequest request) {
        if (SecurityRoleUtils.isMerchant(actor)) {
            return true;
        }
        return request.getWithdrawalMethod() == null || request.getWithdrawalMethod().isBlank();
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

    private void initializeModemPayPayout(CreateWithdrawalRequest request, Withdrawal withdrawal) {
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
                CreatePayoutResponse cached = objectMapper.readValue(recordOpt.get().getResponsePayload(), CreatePayoutResponse.class);
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
    }

    private String generateReference() {
        return "wd-" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
    }

    private String buildPayoutHash(CreatePayoutRequest req) {
        String s = req.getReference() + "|" + req.getAmount() + "|" + req.getCurrency() + "|" + req.getBankAccount() + "|" + req.getPhoneNumber();
        return Integer.toHexString(s.hashCode());
    }
}
