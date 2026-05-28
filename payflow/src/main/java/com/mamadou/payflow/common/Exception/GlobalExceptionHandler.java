package com.mamadou.payflow.common.Exception;

import com.mamadou.payflow.common.response.ApiResponse;
import com.mamadou.payflow.ledger.exception.LedgerException;
import com.mamadou.payflow.transaction.exception.TransactionException;
import com.mamadou.payflow.wallet.exception.WalletAlreadyExistsException;
import com.mamadou.payflow.wallet.exception.WalletNotFoundException;
import com.mamadou.payflow.wallet.exception.WalletOperationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProfileAlreadyExistException.class)
    public ApiResponse<String> profileAlreadyExistException(ProfileAlreadyExistException e) {
        return new ApiResponse<>(false,e.getMessage(),null);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ApiResponse<String> accountNotFoundException(AccountNotFoundException e) {
        return new ApiResponse<>(false,e.getMessage(),null);
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ApiResponse<String> walletNotFoundException(WalletNotFoundException e) {
        return new ApiResponse<>(false,e.getMessage(),null);
    }

    @ExceptionHandler(WalletAlreadyExistsException.class)
    public ApiResponse<String> walletAlreadyExistsException(WalletAlreadyExistsException e) {
        return new ApiResponse<>(false,e.getMessage(),null);
    }

    @ExceptionHandler(WalletOperationException.class)
    public ApiResponse<String> walletOperationException(WalletOperationException e) {
        return new ApiResponse<>(false,e.getMessage(),null);
    }

    @ExceptionHandler(LedgerException.class)
    public ApiResponse<String> ledgerException(LedgerException e) {
        return new ApiResponse<>(false,e.getMessage(),null);
    }

    @ExceptionHandler(TransactionException.class)
    public ApiResponse<String> transactionException(TransactionException e) {
        return new ApiResponse<>(false,e.getMessage(),null);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ApiResponse<String> invalidPasswordException(InvalidPasswordException e) {
        return new ApiResponse<>(false,e.getMessage(),null);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ApiResponse<String> invalidTokenException(InvalidTokenException e) {
        return new ApiResponse<>(false,e.getMessage(),null);
    }

    @ExceptionHandler(APIKeyNotFoundException.class)
    public ApiResponse<String> apiKeyNotFoundException(APIKeyNotFoundException e) {
        return new ApiResponse<>(false,e.getMessage(),null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<String> illegalArgumentException(IllegalArgumentException e) {
        return new ApiResponse<>(false,e.getMessage(),null);
    }

}
