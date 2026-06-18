package com.workshop.employee.dto;

import java.time.Instant;

public record ErrorResponse(
    String message,
    String errorCode,
    String timestamp
) {
    public static ErrorResponse of(String message, String errorCode) {
        return new ErrorResponse(message, errorCode, Instant.now().toString());
    }
}
