package com.workshop.employee.exception;

public class ServiceUnavailableException extends RuntimeException {
    private final String errorCode;

    public ServiceUnavailableException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() { return errorCode; }
}
