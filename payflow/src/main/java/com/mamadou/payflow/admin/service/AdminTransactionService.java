package com.mamadou.payflow.admin.service;

import com.mamadou.payflow.audit.entity.AuditLog;
import com.mamadou.payflow.audit.service.AuditLogService;
import com.mamadou.payflow.audit.service.AuditTrailBuilder;
import com.mamadou.payflow.transaction.entity.Transaction;
import com.mamadou.payflow.transaction.enums.TransactionStatus;
import com.mamadou.payflow.transaction.repository.TransactionRepository;
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminTransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public void reverseTransaction(Long transactionId, String reason, boolean refundToWallet, Long adminId, String adminEmail) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));

        TransactionStatus previousStatus = transaction.getStatus();
        transaction.setStatus(TransactionStatus.REVERSED);

        transactionRepository.save(transaction);
        log.warn("Transaction {} reversed by admin {}: {}", transactionId, adminId, reason);

        if (refundToWallet && transaction.getDestinationWallet() != null) {
            Wallet destWallet = transaction.getDestinationWallet();
            
            if (destWallet != null) {
                log.info("Refunded {} to wallet {}", transaction.getAmount(), destWallet.getId());
                walletRepository.save(destWallet);
            }
        }

        AuditLog auditLog = AuditTrailBuilder.create()
            .actor(adminId, adminEmail)
            .action("REVERSE_TRANSACTION")
            .entity("TRANSACTION", transactionId)
            .previousState(previousStatus)
            .newState(TransactionStatus.REVERSED)
            .changeDescription(reason + (refundToWallet ? " (with refund)" : ""))
            .build();
        
        auditLogService.createLog(auditLog);
    }

    public long getFailedTransactionCount() {
        return transactionRepository.countByStatus("FAILED");
    }

    public long getPendingTransactionCount() {
        return transactionRepository.countByStatus("PENDING");
    }
}
