package com.mamadou.payflow.wallet.dto;

import lombok.Data;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class WalletRequest {
    @Size(max = 60, message = "Wallet name cannot exceed 60 characters")
    private String name;

    @Pattern(regexp = "^[A-Za-z]{3}$", message = "Currency must be a 3-letter ISO code")
    private String currency;
}
