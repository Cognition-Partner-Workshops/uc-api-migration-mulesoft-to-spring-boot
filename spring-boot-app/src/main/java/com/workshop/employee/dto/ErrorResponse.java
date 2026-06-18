package com.workshop.employee.dto;

import java.time.Instant;

public class ErrorResponse {

    private String message;
    private String errorCode;
    private String timestamp;

    public ErrorResponse(String message, String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = Instant.now().toString();
    }

    public String getMessage() {
        return message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
