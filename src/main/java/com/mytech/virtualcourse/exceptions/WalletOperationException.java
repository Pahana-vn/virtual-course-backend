package com.mytech.virtualcourse.exceptions;

public class WalletOperationException extends RuntimeException {
    public WalletOperationException(String message) {
        super(message);
    }
}