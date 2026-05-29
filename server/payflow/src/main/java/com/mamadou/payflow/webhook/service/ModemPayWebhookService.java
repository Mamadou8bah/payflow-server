package com.mamadou.payflow.webhook.service;

import com.mamadou.payflow.transaction.entity.Transaction;
import com.mamadou.payflow.transaction.enums.TransactionStatus;
import com.mamadou.payflow.transaction.repository.TransactionRepository;
import com.mamadou.payflow.deposit.repository.DepositRepository;
import com.mamadou.payflow.deposit.entity.Deposit;
import com.mamadou.payflow.withdrawal.repository.WithdrawalRepository;
import com.mamadou.payflow.withdrawal.entity.Withdrawal;
import com.mamadou.payflow.webhook.dto.ModemPayWebhookPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for handling Modem Pay webhook events
 * Processes payment status updates and other webhook events
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ModemPayWebhookService {

    private final TransactionRepository transactionRepository;
    private final WebhookSignatureService signatureService;
    private final DepositRepository depositRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final WithdrawalProcessingService withdrawalProcessingService;

    /**
     * Process incoming Modem Pay webhook
     * 
     * @param payload Webhook payload
     * @param signature Webhook signature for verification
     */
    @Transactional
    public void processWebhook(ModemPayWebhookPayload payload, String signature) {
        // Verify signature first
        String payloadJson = serializePayload(payload);
        if (!signatureService.verifySignature(payloadJson, signature)) {
            throw new IllegalArgumentException("Invalid webhook signature");
        }

        log.info("Processing Modem Pay webhook - Event: {}, Reference: {}", 
                payload.getEvent(), payload.getData().getReference());

        switch (payload.getEvent()) {
            case "charge.succeeded":
                handleChargeSucceeded(payload);
                break;
            case "charge.failed":
                handleChargeFailed(payload);
                break;
            case "charge.cancelled":
                handleChargeCancelled(payload);
                break;
            case "charge.pending":
                handleChargePending(payload);
                break;
            case "payout.succeeded":
                // delegate to withdrawal processing which records ledger postings
                withdrawalProcessingService.process(payload);
                break;
            case "payout.failed":
                handlePayoutFailed(payload);
                break;
            default:
                log.warn("Unknown webhook event: {}", payload.getEvent());
        }
    }

    private void handlePayoutFailed(ModemPayWebhookPayload payload) {
        ModemPayWebhookPayload.ModemPayChargeData data = payload.getData();
        String reference = data.getReference();

        Optional<Transaction> transactionOpt = transactionRepository
            .findByReference(reference);

        if (transactionOpt.isEmpty()) {
            log.warn("Transaction not found for reference: {}", reference);
        } else {
            Transaction transaction = transactionOpt.get();
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setExternalTransactionId(data.getChargeId());
            transaction.setDescription("Modem Pay payout failed - " + data.getFailureReason());
            transactionRepository.save(transaction);
        }

        Optional<Withdrawal> withdrawalOpt = withdrawalRepository.findByReferenceIgnoreCase(reference);
        if (withdrawalOpt.isPresent()) {
            Withdrawal withdrawal = withdrawalOpt.get();
            withdrawal.setStatus(Withdrawal.WithdrawalStatus.FAILED);
            withdrawal.setRejectionReason(data.getFailureReason());
            withdrawalRepository.save(withdrawal);
        }
    }

    /**
     * Handle successful payment from Modem Pay
     */
    private void handleChargeSucceeded(ModemPayWebhookPayload payload) {
        ModemPayWebhookPayload.ModemPayChargeData chargeData = payload.getData();
        String reference = chargeData.getReference();

        Optional<Transaction> transactionOpt = transactionRepository
            .findByReference(reference);

        if (transactionOpt.isEmpty()) {
            log.warn("Transaction not found for reference: {}", reference);
            return;
        }

        Transaction transaction = transactionOpt.get();
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setExternalTransactionId(chargeData.getChargeId());
        transaction.setDescription("Modem Pay charge succeeded - " + chargeData.getReference());
        
        transactionRepository.save(transaction);
        // Update deposit if exists
        Optional<Deposit> depositOpt = depositRepository.findByReferenceIgnoreCase(reference);
        if (depositOpt.isPresent()) {
            Deposit deposit = depositOpt.get();
            deposit.setStatus(Deposit.DepositStatus.COMPLETED);
            deposit.setCompletedAt(java.time.LocalDateTime.now());
            deposit.setExternalPaymentId(chargeData.getChargeId());
            depositRepository.save(deposit);
        }
        
        log.info("Transaction marked as completed - Reference: {}, Amount: {} {}", 
                reference, chargeData.getAmount(), chargeData.getCurrency());
    }

    /**
     * Handle failed payment from Modem Pay
     */
    private void handleChargeFailed(ModemPayWebhookPayload payload) {
        ModemPayWebhookPayload.ModemPayChargeData chargeData = payload.getData();
        String reference = chargeData.getReference();

        Optional<Transaction> transactionOpt = transactionRepository
            .findByReference(reference);

        if (transactionOpt.isEmpty()) {
            log.warn("Transaction not found for reference: {}", reference);
            return;
        }

        Transaction transaction = transactionOpt.get();
        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setExternalTransactionId(chargeData.getChargeId());
        String failureReason = chargeData.getFailureReason() != null ? 
                chargeData.getFailureReason() : "Payment failed at Modem Pay";
        transaction.setDescription("Modem Pay charge failed - " + failureReason);
        
        transactionRepository.save(transaction);
        Optional<Deposit> depositOpt = depositRepository.findByReferenceIgnoreCase(reference);
        if (depositOpt.isPresent()) {
            Deposit deposit = depositOpt.get();
            deposit.setStatus(Deposit.DepositStatus.FAILED);
            deposit.setFailureReason(failureReason);
            depositRepository.save(deposit);
        }
        
        log.warn("Transaction marked as failed - Reference: {}, Reason: {}", 
                reference, failureReason);
    }

    /**
     * Handle cancelled payment from Modem Pay
     */
    private void handleChargeCancelled(ModemPayWebhookPayload payload) {
        ModemPayWebhookPayload.ModemPayChargeData chargeData = payload.getData();
        String reference = chargeData.getReference();

        Optional<Transaction> transactionOpt = transactionRepository
            .findByReference(reference);

        if (transactionOpt.isEmpty()) {
            log.warn("Transaction not found for reference: {}", reference);
            return;
        }

        Transaction transaction = transactionOpt.get();
        transaction.setStatus(TransactionStatus.CANCELLED);
        transaction.setExternalTransactionId(chargeData.getChargeId());
        transaction.setDescription("Modem Pay charge cancelled");
        
        transactionRepository.save(transaction);
        Optional<Deposit> depositOpt = depositRepository.findByReferenceIgnoreCase(reference);
        if (depositOpt.isPresent()) {
            Deposit deposit = depositOpt.get();
            deposit.setStatus(Deposit.DepositStatus.CANCELLED);
            depositRepository.save(deposit);
        }
        
        log.info("Transaction marked as cancelled - Reference: {}", reference);
    }

    /**
     * Handle pending payment from Modem Pay
     */
    private void handleChargePending(ModemPayWebhookPayload payload) {
        ModemPayWebhookPayload.ModemPayChargeData chargeData = payload.getData();
        String reference = chargeData.getReference();

        Optional<Transaction> transactionOpt = transactionRepository
            .findByReference(reference);

        if (transactionOpt.isEmpty()) {
            log.warn("Transaction not found for reference: {}", reference);
            return;
        }

        Transaction transaction = transactionOpt.get();
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setExternalTransactionId(chargeData.getChargeId());
        transaction.setDescription("Modem Pay charge pending");
        
        transactionRepository.save(transaction);
        Optional<Deposit> depositOpt = depositRepository.findByReferenceIgnoreCase(reference);
        if (depositOpt.isPresent()) {
            Deposit deposit = depositOpt.get();
            deposit.setStatus(Deposit.DepositStatus.PENDING);
            depositRepository.save(deposit);
        }
        
        log.info("Transaction marked as pending - Reference: {}", reference);
    }

    /**
     * Serialize payload to JSON string for signature verification
     */
    private String serializePayload(ModemPayWebhookPayload payload) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = 
                    new com.fasterxml.jackson.databind.ObjectMapper();
            return objectMapper.writeValueAsString(payload);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("Error serializing webhook payload", e);
            return "";
        }
    }
}
