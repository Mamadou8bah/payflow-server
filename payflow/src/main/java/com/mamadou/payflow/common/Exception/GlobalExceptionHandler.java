package com.mamadou.payflow.common.Exception;

import com.mamadou.payflow.common.response.ApiResponse;
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

}
