package com.workshop.employee.dto;

import java.time.Instant;

public record ErrorResponse(String message, String errorCode, String timestamp) {

    public ErrorResponse(String message) {
        this(message, null, Instant.now().toString());
    }

    public ErrorResponse(String message, String errorCode) {
        this(message, errorCode, Instant.now().toString());
    }
}
