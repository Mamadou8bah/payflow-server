package com.mamadou.payflow.transfer.service;

import com.mamadou.payflow.auth.service.CurrentUserService;
import com.mamadou.payflow.common.metrics.PayFlowMetricsService;
import com.mamadou.payflow.transaction.entity.Transaction;
import com.mamadou.payflow.transaction.enums.TransactionType;
import com.mamadou.payflow.transaction.repository.TransactionRepository;
import com.mamadou.payflow.transaction.service.IdempotencyService;
import com.mamadou.payflow.transaction.service.TransactionService;
import com.mamadou.payflow.transfer.dto.TransferListItem;
import com.mamadou.payflow.transfer.dto.TransferRequest;
import com.mamadou.payflow.transfer.dto.TransferResponse;
import com.mamadou.payflow.transfer.dto.TransferValidationResult;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {

    private final TransferValidationService transferValidationService;
    private final TransferExecutionService transferExecutionService;
    private final IdempotencyService idempotencyService;
    private final TransactionService transactionService;
    private final PayFlowMetricsService metricsService;
    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<TransferListItem> listForCurrentUser() {
        Long userId = currentUserService.getCurrentUserId();
        return transactionRepository
                .findByInitiatedByIdAndTypeOrderByCreatedAtDesc(userId, TransactionType.TRANSFER)
                .stream()
                .map(TransferListItem::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransferResponse transfer(TransferRequest request, String headerIdempotencyKey) {
        metricsService.recordTransferInitiationAttempt();
        Timer.Sample sample = metricsService.startTransferExecutionTimer();
        
        log.info("Transfer initiated from walletId={} to walletId={} amount={}", 
            request.getSourceWalletId(), request.getDestinationWalletId(), request.getAmount());
        
        try {
            String idempotencyKey = resolveIdempotencyKey(request.getIdempotencyKey(), headerIdempotencyKey);
            String fingerprint = fingerprint(request);

            TransferResponse response = idempotencyService.findExistingTransaction(idempotencyKey, fingerprint)
                    .map(existingTransaction -> {
                        log.info("Transfer idempotent replay detected, transactionId={}", existingTransaction.getId());
                        return TransferResponse.builder()
                                .transaction(transactionService.toResponse(existingTransaction))
                                .idempotentReplay(true)
                                .build();
                    })
                    .orElseGet(() -> {
                        TransferValidationResult validationResult = transferValidationService.validateForExecution(request);
                        Transaction transaction = transferExecutionService.execute(request, validationResult);
                        idempotencyService.saveKey(idempotencyKey, fingerprint, transaction);
                        
                        log.info("Transfer completed successfully, transactionId={}, status={}", 
                            transaction.getId(), transaction.getStatus());
                        metricsService.recordTransferInitiationSuccess();
                        
                        return TransferResponse.builder()
                                .transaction(transactionService.toResponse(transaction))
                                .idempotentReplay(false)
                                .build();
                    });
            
            return response;
        } catch (Exception e) {
            log.error("Transfer failed with exception", e);
            metricsService.recordTransferInitiationFailure();
            throw e;
        } finally {
            metricsService.stopTransferExecutionTimer(sample);
        }
    }

    private String resolveIdempotencyKey(String requestKey, String headerKey) {
        if (headerKey != null && !headerKey.isBlank()) {
            return headerKey.trim();
        }
        if (requestKey != null && !requestKey.isBlank()) {
            return requestKey.trim();
        }
        return null;
    }

    private String fingerprint(TransferRequest request) {
        return request.getSourceWalletId() + "|"
                + request.getDestinationWalletId() + "|"
                + request.getAmount() + "|"
                + nullToBlank(request.getDescription()) + "|"
                + nullToBlank(request.getReference());
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value.trim();
    }
}
