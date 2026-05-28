package com.mamadou.payflow.wallet.dto;

import com.mamadou.payflow.wallet.enums.WalletStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletResponse {
    private Long id;
    private String name;
    private String currency;
    private WalletStatus status;
    private String ledgerAccountCode;
    private Long ownerId;
}
