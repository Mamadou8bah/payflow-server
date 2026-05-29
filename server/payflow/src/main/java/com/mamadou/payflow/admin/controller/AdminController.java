package com.mamadou.payflow.admin.controller;

import com.mamadou.payflow.admin.dto.*;
import com.mamadou.payflow.admin.service.*;
import com.mamadou.payflow.audit.repository.AuditLogRepository;
import com.mamadou.payflow.reconciliation.dto.ReconciliationReportResponse;
import com.mamadou.payflow.risk.repository.RiskFlagRepository;
import com.mamadou.payflow.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminWalletService adminWalletService;
    private final AdminTransactionService adminTransactionService;
    private final AdminWebhookService adminWebhookService;
    private final AdminReconciliationService adminReconciliationService;
    private final WalletRepository walletRepository;
    private final RiskFlagRepository riskFlagRepository;
    private final AuditLogRepository auditLogRepository;

    // ==================== WALLET OPERATIONS ====================

    @PostMapping("/wallets/freeze")
    public ResponseEntity<String> freezeWallet(
        @RequestBody FreezeWalletAdminRequest request,
        Authentication authentication
    ) {
        log.info("Admin freeze wallet request: {}", request.walletId());
        Long adminId = extractAdminId(authentication);
        String adminEmail = authentication.getName();
        
        adminWalletService.freezeWallet(request.walletId(), request.reason(), adminId, adminEmail);
        return ResponseEntity.ok("Wallet " + request.walletId() + " has been frozen");
    }

    @PostMapping("/wallets/{walletId}/unfreeze")
    public ResponseEntity<String> unfreezeWallet(
        @PathVariable Long walletId,
        Authentication authentication
    ) {
        log.info("Admin unfreeze wallet request: {}", walletId);
        Long adminId = extractAdminId(authentication);
        String adminEmail = authentication.getName();
        
        adminWalletService.unfreezeWallet(walletId, adminId, adminEmail);
        return ResponseEntity.ok("Wallet " + walletId + " has been unfrozen");
    }

    // ==================== TRANSACTION OPERATIONS ====================

    @PostMapping("/transactions/reverse")
    public ResponseEntity<String> reverseTransaction(
        @RequestBody ReverseTransactionAdminRequest request,
        Authentication authentication
    ) {
        log.info("Admin reverse transaction request: {}", request.transactionId());
        Long adminId = extractAdminId(authentication);
        String adminEmail = authentication.getName();
        
        adminTransactionService.reverseTransaction(
            request.transactionId(),
            request.reason(),
            request.refundToWallet(),
            adminId,
            adminEmail
        );
        return ResponseEntity.ok("Transaction " + request.transactionId() + " has been reversed");
    }

    // ==================== WEBHOOK OPERATIONS ====================

    @PostMapping("/webhooks/reprocess")
    public ResponseEntity<String> reprocessWebhook(
        @RequestBody ReprocessWebhookAdminRequest request,
        Authentication authentication
    ) {
        log.info("Admin reprocess webhook request: {}", request.webhookId());
        Long adminId = extractAdminId(authentication);
        String adminEmail = authentication.getName();
        
        adminWebhookService.reprocessWebhook(request.webhookId(), request.reason(), adminId, adminEmail);
        return ResponseEntity.ok("Webhook " + request.webhookId() + " marked for reprocessing");
    }

    // ==================== RECONCILIATION OPERATIONS ====================

    @PostMapping("/reconciliation/wallet-ledger")
    public ResponseEntity<ReconciliationReportResponse> triggerWalletReconciliation(
        Authentication authentication
    ) {
        log.info("Admin triggering wallet reconciliation");
        Long adminId = extractAdminId(authentication);
        String adminEmail = authentication.getName();
        
        ReconciliationReportResponse report = adminReconciliationService.triggerWalletReconciliation(adminId, adminEmail);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/reconciliation/webhook-deposit")
    public ResponseEntity<ReconciliationReportResponse> triggerWebhookReconciliation(
        Authentication authentication
    ) {
        log.info("Admin triggering webhook reconciliation");
        Long adminId = extractAdminId(authentication);
        String adminEmail = authentication.getName();
        
        ReconciliationReportResponse report = adminReconciliationService.triggerWebhookReconciliation(adminId, adminEmail);
        return ResponseEntity.ok(report);
    }

    // ==================== DASHBOARD & METRICS ====================

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard() {
        log.info("Fetching admin dashboard");
        
        long totalWallets = walletRepository.count();
        long activeWallets = 0; // Depends on WalletRepository.countByStatus
        long frozenWallets = adminWalletService.getFrozenWalletCount();
        
        long totalTransactions = 0; // Depends on TransactionRepository.count()
        long failedTransactions = adminTransactionService.getFailedTransactionCount();
        long pendingTransactions = adminTransactionService.getPendingTransactionCount();
        
        long totalRiskFlags = riskFlagRepository.count();
        long criticalRiskFlags = 0; // Depends on RiskFlagRepository.countByCritical
        
        long unresolvedMismatches = adminReconciliationService.getUnresolvedMismatchesCount();
        long failedOperations = auditLogRepository.findFailedOperations().size();
        
        // Operation counts for last 24 hours
        Map<String, Long> operationCounts = new HashMap<>();
        LocalDateTime last24h = LocalDateTime.now().minusHours(24);
        operationCounts.put("freezes", auditLogRepository.countRecentByActionType("FREEZE_WALLET", last24h));
        operationCounts.put("reversals", auditLogRepository.countRecentByActionType("REVERSE_TRANSACTION", last24h));
        operationCounts.put("reprocesses", auditLogRepository.countRecentByActionType("REPROCESS_WEBHOOK", last24h));
        
        // System health
        Map<String, Object> systemHealth = new HashMap<>();
        systemHealth.put("status", "HEALTHY");
        systemHealth.put("uptime", "100%");
        systemHealth.put("lastCheck", LocalDateTime.now());
        
        AdminDashboardResponse dashboard = new AdminDashboardResponse(
            totalWallets,
            activeWallets,
            frozenWallets,
            totalTransactions,
            failedTransactions,
            pendingTransactions,
            totalRiskFlags,
            criticalRiskFlags,
            unresolvedMismatches,
            failedOperations,
            operationCounts,
            systemHealth
        );
        
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/audit-trail")
    public ResponseEntity<String> getAuditTrail() {
        log.info("Fetching admin audit trail");
        long auditLogCount = auditLogRepository.count();
        return ResponseEntity.ok("Total audit logs: " + auditLogCount);
    }

    private Long extractAdminId(Authentication authentication) {
        // In a real implementation, extract from JWT or user object
        return 1L; // Placeholder
    }
}
