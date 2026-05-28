package com.mamadou.payflow.reconciliation.service;

import com.mamadou.payflow.reconciliation.entity.ReconciliationMismatch;
import com.mamadou.payflow.reconciliation.entity.ReconciliationReport;
import com.mamadou.payflow.reconciliation.enums.ReconciliationStatus;
import com.mamadou.payflow.reconciliation.repository.ReconciliationMismatchRepository;
import com.mamadou.payflow.transaction.entity.Transaction;
import com.mamadou.payflow.transaction.enums.TransactionStatus;
import com.mamadou.payflow.transaction.repository.TransactionRepository;
import com.mamadou.payflow.webhook.entity.WebhookEvent;
import com.mamadou.payflow.webhook.repository.WebhookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookReconciliationService {

    private final WebhookRepository webhookRepository;
    private final TransactionRepository transactionRepository;
    private final ReconciliationMismatchRepository mismatchRepository;
    private final ObjectMapper objectMapper;

    // Configuration constants for reconciliation logic
    private static final int RECONCILIATION_WINDOW_HOURS = 3;
    private static final int BATCH_SIZE = 100;
    private static final BigDecimal AMOUNT_TOLERANCE = new BigDecimal("0.01");  // Allow 1 cent variance

    @Transactional
    public void reconcileWebhookVsDeposit(ReconciliationReport report) {
        log.info("Starting webhook vs deposit reconciliation for report {}", report.getId());
        long startTime = System.currentTimeMillis();

        try {
            // Step 1: Fetch all webhooks from reconciliation window
            LocalDateTime cutoffTime = LocalDateTime.now().minusHours(RECONCILIATION_WINDOW_HOURS);
            List<WebhookEvent> webhooks = webhookRepository.findByReceivedAtAfter(cutoffTime);

            if (webhooks.isEmpty()) {
                log.info("No webhooks found for reconciliation in the last {} hours", RECONCILIATION_WINDOW_HOURS);
                report.setTotalRecordsChecked(0);
                report.setMismatchesFound(0);
                return;
            }

            log.info("Found {} webhooks to reconcile", webhooks.size());

            // Step 2: Process webhooks in parallel batches
            AtomicLong totalMismatches = new AtomicLong(0);
            AtomicLong successfulMatches = new AtomicLong(0);

            // Process each webhook for reconciliation
            webhooks.forEach(webhook -> {
                try {
                    ReconciliationResult result = reconcileSingleWebhook(report, webhook);

                    if (result.isMismatched) {
                        totalMismatches.incrementAndGet();
                        createMismatch(report, webhook, result);
                    } else {
                        successfulMatches.incrementAndGet();
                    }
                } catch (Exception e) {
                    log.error("Error reconciling webhook {}", webhook.getId(), e);
                    totalMismatches.incrementAndGet();
                    createMismatchFromException(report, webhook, e);
                }
            });

            // Step 3: Update report with final metrics
            long totalRecords = webhooks.size();
            long mismatches = totalMismatches.get();

            report.setTotalRecordsChecked(totalRecords);
            report.setMismatchesFound(mismatches);
            report.setStatus(mismatches > 0 ? ReconciliationStatus.COMPLETED_WITH_MISMATCHES : ReconciliationStatus.COMPLETED);
            report.setCompletedAt(LocalDateTime.now());

            long duration = System.currentTimeMillis() - startTime;
            log.info("Webhook reconciliation completed in {}ms. Total: {}, Matched: {}, Mismatches: {}",
                    duration, totalRecords, successfulMatches.get(), mismatches);

        } catch (Exception e) {
            log.error("Error during webhook reconciliation for report {}", report.getId(), e);
            report.setStatus(ReconciliationStatus.FAILED);
            report.setErrorDetails("Error: " + e.getMessage());
            throw new RuntimeException("Webhook reconciliation failed", e);
        }
    }

    /**
     * Reconcile a single webhook against transaction records
     */
    private ReconciliationResult reconcileSingleWebhook(ReconciliationReport report, WebhookEvent webhook) {
        // Step 1: Extract reference from webhook payload
        String transactionReference = extractTransactionReference(webhook);

        if (transactionReference == null || transactionReference.isBlank()) {
            return new ReconciliationResult(
                    true,
                    "MISSING_TRANSACTION_REFERENCE",
                    "Cannot extract transaction reference from webhook payload"
            );
        }

        // Step 2: Look up transaction by reference
        Optional<Transaction> transactionOpt = transactionRepository.findByReference(transactionReference);

        if (transactionOpt.isEmpty()) {
            return new ReconciliationResult(
                    true,
                    "TRANSACTION_NOT_FOUND",
                    "No transaction found with reference: " + transactionReference
            );
        }

        Transaction transaction = transactionOpt.get();

        // Step 3: Validate transaction status is terminal (not pending)
        if (!isTerminalStatus(transaction.getStatus())) {
            return new ReconciliationResult(
                    true,
                    "TRANSACTION_PENDING",
                    "Transaction still in pending state: " + transaction.getStatus()
            );
        }

        // Step 4: Validate amount matches
        BigDecimal webhookAmount = extractAmount(webhook);
        if (webhookAmount == null || !amountsMatch(transaction.getAmount(), webhookAmount)) {
            return new ReconciliationResult(
                    true,
                    "AMOUNT_MISMATCH",
                    String.format("Amount mismatch - Transaction: %s, Webhook: %s",
                            transaction.getAmount(), webhookAmount)
            );
        }

        // Step 5: Validate timestamp within window
        LocalDateTime webhookTime = webhook.getReceivedAt();
        LocalDateTime transactionTime = transaction.getCreatedAt();

        if (!isTimestampWithinWindow(webhookTime, transactionTime)) {
            return new ReconciliationResult(
                    true,
                    "TIMESTAMP_MISMATCH",
                    String.format("Timestamps outside acceptable window - Transaction: %s, Webhook: %s",
                            transactionTime, webhookTime)
            );
        }

        // Step 6: Check for duplicate webhook processing (idempotency)
        if (webhook.getStatus().equals("PROCESSED") && webhook.getAttempts() > 1) {
            log.warn("Webhook already processed multiple times - potential duplicate: {}", webhook.getId());
            // Still consider as matched, but log warning for investigation
        }

        // Step 7: All validations passed - this is a successful match
        log.debug("Webhook {} successfully matched with transaction {}", webhook.getId(), transaction.getId());
        return new ReconciliationResult(false, null, null);
    }

    /**
     * Extract transaction reference from webhook JSON payload
     */
    private String extractTransactionReference(WebhookEvent webhook) {
        try {
            Map<String, Object> payload = objectMapper.readValue(webhook.getRawPayload(), Map.class);

            if (payload.containsKey("transactionId")) {
                return (String) payload.get("transactionId");
            }
            if (payload.containsKey("reference")) {
                return (String) payload.get("reference");
            }
            if (payload.containsKey("externalReference")) {
                return (String) payload.get("externalReference");
            }
            if (payload.containsKey("orderId")) {
                return (String) payload.get("orderId");
            }

            if (payload.containsKey("data")) {
                Object data = payload.get("data");
                if (data instanceof Map) {
                    Map<String, Object> dataMap = (Map<String, Object>) data;
                    if (dataMap.containsKey("transactionId")) {
                        return (String) dataMap.get("transactionId");
                    }
                    if (dataMap.containsKey("reference")) {
                        return (String) dataMap.get("reference");
                    }
                }
            }

            return webhook.getExternalReference();
        } catch (Exception e) {
            log.error("Error extracting transaction reference from webhook {}", webhook.getId(), e);
            return null;
        }
    }

    private BigDecimal extractAmount(WebhookEvent webhook) {
        try {
            Map<String, Object> payload = objectMapper.readValue(webhook.getRawPayload(), Map.class);

            for (String fieldName : Arrays.asList("amount", "value", "total", "price")) {
                if (payload.containsKey(fieldName)) {
                    Object val = payload.get(fieldName);
                    if (val instanceof Number) {
                        return new BigDecimal(val.toString());
                    } else if (val instanceof String) {
                        return new BigDecimal((String) val);
                    }
                }
            }

            if (payload.containsKey("data") && payload.get("data") instanceof Map) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                for (String fieldName : Arrays.asList("amount", "value", "total")) {
                    if (data.containsKey(fieldName)) {
                        Object val = data.get(fieldName);
                        if (val instanceof Number) {
                            return new BigDecimal(val.toString());
                        } else if (val instanceof String) {
                            return new BigDecimal((String) val);
                        }
                    }
                }
            }

            return null;
        } catch (Exception e) {
            log.error("Error extracting amount from webhook {}", webhook.getId(), e);
            return null;
        }
    }

    /**
     * Check if amounts match within tolerance
     */
    private boolean amountsMatch(BigDecimal transactionAmount, BigDecimal webhookAmount) {
        if (transactionAmount == null || webhookAmount == null) {
            return false;
        }
        BigDecimal difference = transactionAmount.subtract(webhookAmount).abs();
        return difference.compareTo(AMOUNT_TOLERANCE) <= 0;
    }

    /**
     * Check if timestamp is within acceptable reconciliation window
     */
    private boolean isTimestampWithinWindow(LocalDateTime webhookTime, LocalDateTime transactionTime) {
        if (webhookTime == null || transactionTime == null) {
            return false;
        }

        long minutesDiff = Math.abs(ChronoUnit.MINUTES.between(transactionTime, webhookTime));
        return minutesDiff <= (RECONCILIATION_WINDOW_HOURS * 60);
    }

    /**
     * Check if transaction is in terminal status
     */
    private boolean isTerminalStatus(TransactionStatus status) {
        return status == TransactionStatus.COMPLETED ||
               status == TransactionStatus.FAILED;
    }

    /**
     * Create reconciliation mismatch record
     */
    private void createMismatch(ReconciliationReport report, WebhookEvent webhook,
                                ReconciliationResult result) {
        ReconciliationMismatch mismatch = ReconciliationMismatch.builder()
                .report(report)
                .mismatchType(result.mismatchType)
                .entityId(webhook.getId())
                .entityType("WEBHOOK_EVENT")
                .expectedValue(extractTransactionReference(webhook) != null ? 
                        "Webhook Reference: " + extractTransactionReference(webhook) : "UNKNOWN")
                .actualValue(webhook.getExternalReference() != null ? 
                        webhook.getExternalReference() : "NULL")
                .variance(BigDecimal.ZERO)
                .description(result.description)
                .resolved(false)
                .build();

        mismatchRepository.save(mismatch);
        log.warn("Reconciliation mismatch created: Type={}, Webhook={}, Reason={}",
                result.mismatchType, webhook.getId(), result.description);
    }

    /**
     * Create mismatch from exception
     */
    private void createMismatchFromException(ReconciliationReport report, WebhookEvent webhook,
                                            Exception e) {
        ReconciliationMismatch mismatch = ReconciliationMismatch.builder()
                .report(report)
                .mismatchType("RECONCILIATION_ERROR")
                .entityId(webhook.getId())
                .entityType("WEBHOOK_EVENT")
                .expectedValue("Valid reconciliation")
                .actualValue("Error: " + e.getMessage())
                .variance(BigDecimal.ZERO)
                .description("Exception during reconciliation: " + e.getClass().getSimpleName() + " - " + e.getMessage())
                .resolved(false)
                .build();

        mismatchRepository.save(mismatch);
        log.error("Reconciliation error recorded for webhook {}", webhook.getId(), e);
    }

    /**
     * Result holder for reconciliation outcome
     */
    private static class ReconciliationResult {
        final boolean isMismatched;
        final String mismatchType;
        final String description;

        ReconciliationResult(boolean isMismatched, String mismatchType, String description) {
            this.isMismatched = isMismatched;
            this.mismatchType = mismatchType;
            this.description = description;
        }
    }
}
