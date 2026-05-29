package com.mamadou.payflow.deposit.dto;

import com.mamadou.payflow.deposit.entity.Deposit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO for creating a deposit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDepositRequest {

    /**
     * Wallet ID to deposit into
     */
    @NotNull(message = "Wallet ID is required")
    @Positive(message = "Wallet ID must be positive")
    private Long walletId;

    /**
     * Amount to deposit
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
     * Payment method
     */
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    /**
     * For mobile money deposits
     */
    private String phoneNumber;

    /**
     * Optional description
     */
    private String description;

    /**
     * For agent deposits - the user ID to deposit for (only for agents)
     */
    private Long userId;

    /**
     * Idempotency key to prevent duplicate deposits
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
 * Response DTO for deposit details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class DepositResponse {
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
                .completedAt(deposit.getCompletedAt())
                .createdAt(deposit.getCreatedAt())
                .updatedAt(deposit.getUpdatedAt())
                .build();
    }
}
