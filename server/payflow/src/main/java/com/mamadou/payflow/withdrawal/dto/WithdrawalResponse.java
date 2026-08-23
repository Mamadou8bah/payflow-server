package com.mamadou.payflow.withdrawal.dto;

import com.mamadou.payflow.withdrawal.entity.Withdrawal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalResponse {
    private Long id;
    private Long walletId;
    private Long userId;
    private Long agentId;
    private String withdrawalType;
    private String status;
    private BigDecimal amount;
    private BigDecimal feesAmount;
    private String currency;
    private String withdrawalMethod;
    private String phoneNumber;
    private String bankAccount;
    private String reference;
    private String description;
    private String rejectionReason;
    private LocalDateTime approvedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static WithdrawalResponse from(Withdrawal withdrawal) {
        return WithdrawalResponse.builder()
                .id(withdrawal.getId())
                .walletId(withdrawal.getWallet().getId())
                .userId(withdrawal.getUser().getId())
                .agentId(withdrawal.getAgent() != null ? withdrawal.getAgent().getId() : null)
                .withdrawalType(withdrawal.getWithdrawalType().name())
                .status(withdrawal.getStatus().name())
                .amount(withdrawal.getAmount())
                .feesAmount(withdrawal.getFeesAmount())
                .currency(withdrawal.getCurrency())
                .withdrawalMethod(withdrawal.getWithdrawalMethod())
                .phoneNumber(withdrawal.getPhoneNumber())
                .bankAccount(withdrawal.getBankAccount())
                .reference(withdrawal.getReference())
                .description(withdrawal.getDescription())
                .rejectionReason(withdrawal.getRejectionReason())
                .approvedAt(withdrawal.getApprovedAt())
                .completedAt(withdrawal.getCompletedAt())
                .createdAt(withdrawal.getCreatedAt())
                .updatedAt(withdrawal.getUpdatedAt())
                .build();
    }
}
