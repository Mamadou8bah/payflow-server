package com.mamadou.payflow.wallet.dto;

import com.mamadou.payflow.wallet.enums.WalletStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WalletBalanceResponse {
    private Long id;
    private WalletStatus walletStatus;
    private String ledgerAccountCode;
    private BigDecimal balance;
}
