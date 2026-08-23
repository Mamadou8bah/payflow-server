package com.mamadou.payflow.deposit.dto;

import com.mamadou.payflow.deposit.entity.Deposit;
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
public class DepositResponse {
    private Long id;
    private Long walletId;
    private Long userId;
    private Long agentId;
    private String depositType;
    private String status;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String reference;
    private String description;
    private String failureReason;
    private String phoneNumber;
    private String paymentUrl;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DepositResponse from(Deposit deposit) {
        return DepositResponse.builder()
                .id(deposit.getId())
                .walletId(deposit.getWallet().getId())
                .userId(deposit.getUser().getId())
                .agentId(deposit.getAgent() != null ? deposit.getAgent().getId() : null)
                .depositType(deposit.getDepositType().name())
                .status(deposit.getStatus().name())
                .amount(deposit.getAmount())
                .currency(deposit.getCurrency())
                .paymentMethod(deposit.getPaymentMethod())
                .reference(deposit.getReference())
                .description(deposit.getDescription())
                .failureReason(deposit.getFailureReason())
                .phoneNumber(deposit.getPhoneNumber())
                .paymentUrl(deposit.getPaymentUrl())
                .completedAt(deposit.getCompletedAt())
                .createdAt(deposit.getCreatedAt())
                .updatedAt(deposit.getUpdatedAt())
                .build();
    }
}
