package com.mamadou.payflow.common.Exception;

public class ProfileAlreadyExistException extends RuntimeException {
    public ProfileAlreadyExistException(String message) {
        super(message);
    }
}
