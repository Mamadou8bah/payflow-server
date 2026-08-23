package com.mamadou.payflow.deposit.dto;



import jakarta.validation.constraints.DecimalMin;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;

import lombok.Builder;

import lombok.Data;

import lombok.NoArgsConstructor;



import java.math.BigDecimal;



/**

 * Request DTO for creating a deposit

 */

@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class CreateDepositRequest {



    /**

     * Wallet ID to deposit into (optional — primary wallet is used when omitted)

     */

    private Long walletId;



    /**

     * Amount to deposit

     */

    @NotNull(message = "Amount is required")

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")

    private BigDecimal amount;



    /**

     * Currency (e.g., GMD, USD). Defaults from wallet when omitted.

     */

    private String currency;



    /**

     * Payment method for external collections. Omit for internal merchant/agent-assisted deposits.

     */

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

}

