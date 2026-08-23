package com.mamadou.payflow.admin.service;

import com.mamadou.payflow.admin.dto.AdminWalletResponse;
import com.mamadou.payflow.audit.entity.AuditLog;
import com.mamadou.payflow.audit.service.AuditLogService;
import com.mamadou.payflow.audit.service.AuditTrailBuilder;
import com.mamadou.payflow.ledger.service.LedgerBalanceComputationService;
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.enums.WalletStatus;
import com.mamadou.payflow.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminWalletService {

    private final WalletRepository walletRepository;
    private final LedgerBalanceComputationService ledgerBalanceComputationService;
    private final AuditLogService auditLogService;

    @Transactional(readOnly = true)
    public List<AdminWalletResponse> listWallets() {
        return walletRepository.findAll().stream()
                .map(this::toAdminWallet)
                .collect(Collectors.toList());
    }

    private AdminWalletResponse toAdminWallet(Wallet wallet) {
        BigDecimal balance = wallet.getLedgerAccount() != null
                ? ledgerBalanceComputationService.computeBalance(wallet.getLedgerAccount())
                : BigDecimal.ZERO;
        String ownerEmail = wallet.getUser() != null ? wallet.getUser().getEmail() : "";
        return new AdminWalletResponse(
                wallet.getId(),
                wallet.getName(),
                wallet.getCurrency(),
                wallet.getStatus(),
                wallet.getUser() != null ? wallet.getUser().getId() : null,
                ownerEmail,
                balance
        );
    }

    @Transactional
    public void freezeWallet(Long walletId, String reason, Long adminId, String adminEmail) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new RuntimeException("Wallet not found: " + walletId));

        WalletStatus previousStatus = wallet.getStatus();
        wallet.setStatus(WalletStatus.SUSPENDED);

        walletRepository.save(wallet);
        log.warn("Wallet {} suspended (frozen) by admin {}: {}", walletId, adminId, reason);

        AuditLog auditLog = AuditTrailBuilder.create()
            .actor(adminId, adminEmail)
            .action("FREEZE_WALLET")
            .entity("WALLET", walletId)
            .previousState(previousStatus)
            .newState(WalletStatus.SUSPENDED)
            .changeDescription(reason)
            .build();
        
        auditLogService.createLog(auditLog);
    }

    @Transactional
    public void unfreezeWallet(Long walletId, Long adminId, String adminEmail) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new RuntimeException("Wallet not found: " + walletId));

        WalletStatus previousStatus = wallet.getStatus();
        wallet.setStatus(WalletStatus.ACTIVE);

        walletRepository.save(wallet);
        log.info("Wallet {} unfrozen by admin {}", walletId, adminId);

        AuditLog auditLog = AuditTrailBuilder.create()
            .actor(adminId, adminEmail)
            .action("UNFREEZE_WALLET")
            .entity("WALLET", walletId)
            .previousState(previousStatus)
            .newState(WalletStatus.ACTIVE)
            .changeDescription("Admin unfreeze")
            .build();
        
        auditLogService.createLog(auditLog);
    }

    public long getFrozenWalletCount() {
        return walletRepository.countByStatus(WalletStatus.SUSPENDED);
    }
}
