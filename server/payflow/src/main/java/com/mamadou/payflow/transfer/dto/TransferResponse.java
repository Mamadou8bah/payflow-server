package com.mamadou.payflow.transfer.dto;

import com.mamadou.payflow.transaction.dto.TransactionResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferResponse {
    private TransactionResponse transaction;
    private boolean idempotentReplay;
}
