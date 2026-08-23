package com.mamadou.payflow.withdrawal.dto;



import jakarta.validation.constraints.DecimalMin;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;

import lombok.Builder;

import lombok.Data;

import lombok.NoArgsConstructor;



import java.math.BigDecimal;



/**

 * Request DTO for creating a withdrawal

 */

@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class CreateWithdrawalRequest {



    /**

     * Wallet ID to withdraw from (optional — primary wallet is used when omitted)

     */

    private Long walletId;



    /**

     * Amount to withdraw

     */

    @NotNull(message = "Amount is required")

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")

    private BigDecimal amount;



    /**

     * Currency (e.g., GMD, USD). Defaults from wallet when omitted.

     */

    private String currency;



    /**

     * Withdrawal method. Omit for internal merchant/agent-assisted withdrawals.

     */

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

}

