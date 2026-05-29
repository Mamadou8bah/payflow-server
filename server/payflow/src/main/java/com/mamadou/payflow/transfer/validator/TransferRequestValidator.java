package com.mamadou.payflow.transfer.validator;

import com.mamadou.payflow.transfer.dto.TransferRequest;
import com.mamadou.payflow.wallet.exception.WalletOperationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransferRequestValidator {

    public void validate(TransferRequest request) {
        if (request.getSourceWalletId() == null) {
            throw new WalletOperationException("Source wallet is required");
        }
        if (request.getDestinationWalletId() == null) {
            throw new WalletOperationException("Destination wallet is required");
        }
        if (request.getSourceWalletId().equals(request.getDestinationWalletId())) {
            throw new WalletOperationException("Source and destination wallets must be different");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new WalletOperationException("Amount must be greater than zero");
        }
    }
}
