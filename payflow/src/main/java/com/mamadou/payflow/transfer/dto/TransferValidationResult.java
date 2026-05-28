package com.mamadou.payflow.transfer.dto;

import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.wallet.entity.Wallet;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferValidationResult {
    private Wallet sourceWallet;
    private Wallet destinationWallet;
    private User initiatedBy;
}
