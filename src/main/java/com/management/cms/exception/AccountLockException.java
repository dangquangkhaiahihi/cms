package com.management.cms.exception;

public class AccountLockException extends Exception {
    public AccountLockException(String errorMessage) {
        super(errorMessage);
    }
}