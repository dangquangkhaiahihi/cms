package com.management.cms.exception;

public class OldPasswordIsWrongException extends Exception {
    public OldPasswordIsWrongException(String errorMessage) {
        super(errorMessage);
    }
}