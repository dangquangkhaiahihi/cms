package com.management.cms.exception;

public class UsedInLastThreeTimePasswordException extends Exception {
    public UsedInLastThreeTimePasswordException(String errorMessage) {
        super(errorMessage);
    }
}
