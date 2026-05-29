package com.mamadou.payflow.admin.service;

import com.mamadou.payflow.audit.entity.AuditLog;
import com.mamadou.payflow.audit.service.AuditLogService;
import com.mamadou.payflow.audit.service.AuditTrailBuilder;
import com.mamadou.payflow.reconciliation.dto.ReconciliationReportResponse;
import com.mamadou.payflow.reconciliation.service.ReconciliationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminReconciliationService {

    private final ReconciliationService reconciliationService;
    private final AuditLogService auditLogService;

    @Transactional
    public ReconciliationReportResponse triggerWalletReconciliation(Long adminId, String adminEmail) {
        log.info("Admin {} triggering wallet reconciliation", adminId);
        
        ReconciliationReportResponse report = reconciliationService.runWalletLedgerReconciliation();

        AuditLog auditLog = AuditTrailBuilder.create()
            .actor(adminId, adminEmail)
            .action("TRIGGER_RECONCILIATION")
            .entity("RECONCILIATION_REPORT", report.id())
            .changeDescription("Manually triggered wallet-ledger reconciliation")
            .build();
        
        auditLogService.createLog(auditLog);

        return report;
    }

    @Transactional
    public ReconciliationReportResponse triggerWebhookReconciliation(Long adminId, String adminEmail) {
        log.info("Admin {} triggering webhook reconciliation", adminId);
        
        ReconciliationReportResponse report = reconciliationService.runWebhookDepositReconciliation();

        AuditLog auditLog = AuditTrailBuilder.create()
            .actor(adminId, adminEmail)
            .action("TRIGGER_RECONCILIATION")
            .entity("RECONCILIATION_REPORT", report.id())
            .changeDescription("Manually triggered webhook-deposit reconciliation")
            .build();
        
        auditLogService.createLog(auditLog);

        return report;
    }

    public long getUnresolvedMismatchesCount() {
        return reconciliationService.getUnresolvedMismatchesCount();
    }
}
