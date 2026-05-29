package com.mamadou.payflow.withdrawal.dto;

import com.mamadou.payflow.withdrawal.entity.Withdrawal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO for creating a withdrawal
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWithdrawalRequest {

    /**
     * Wallet ID to withdraw from
     */
    @NotNull(message = "Wallet ID is required")
    @Positive(message = "Wallet ID must be positive")
    private Long walletId;

    /**
     * Amount to withdraw
     */
    @NotNull(message = "Amount is required")
    @DecimalPositive(message = "Amount must be greater than 0")
    private BigDecimal amount;

    /**
     * Currency (e.g., GMD, USD)
     */
    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency;

    /**
     * Withdrawal method
     */
    @NotBlank(message = "Withdrawal method is required")
    private String withdrawalMethod;

    /**
     * Phone number for mobile money withdrawals
     */
    private String phoneNumber;

    /**
     * Bank account for bank transfer withdrawals
     */
    private String bankAccount;

    /**
     * Optional description
     */
    private String description;

    /**
     * For agent withdrawals - the user ID to withdraw for (only for agents)
     */
    private Long userId;

    /**
     * Idempotency key to prevent duplicate withdrawals
     */
    private String idempotencyKey;

    /**
     * Custom constraint annotation
     */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = DecimalPositiveValidator.class)
    public @interface DecimalPositive {
        String message() default "Amount must be greater than 0";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    @jakarta.validation.ConstraintValidator
    public static class DecimalPositiveValidator implements ConstraintValidator<DecimalPositive, BigDecimal> {
        @Override
        public void initialize(DecimalPositive constraintAnnotation) {}

        @Override
        public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
            return value == null || value.compareTo(BigDecimal.ZERO) > 0;
        }
    }
}

/**
 * Request DTO for approving/rejecting a withdrawal (agent action)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class WithdrawalApprovalRequest {
    @NotNull(message = "Withdrawal ID is required")
    private Long withdrawalId;

    @NotNull(message = "Approved flag is required")
    private Boolean approved;

    private String rejectionReason;
}

/**
 * Response DTO for withdrawal details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class WithdrawalResponse {
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
