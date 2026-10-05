package com.workshop.employee.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

/**
 * Error body per {@code contracts/openapi.yaml#/components/schemas/ErrorResponse}.
 *
 * <p>The MuleSoft flows emitted several ad-hoc error shapes ({@code {"error", "error_description"}}
 * for OAuth, {@code {"error", "employeeId", "details"}} for DB failures). The contract
 * normalises them to {@code message}/{@code errorCode}/{@code timestamp}; the Mule
 * {@code error} code is carried in {@code errorCode} where one existed.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String message, String errorCode, String timestamp) {

    public static ErrorResponse of(String message) {
        return new ErrorResponse(message, null, Instant.now().toString());
    }

    public static ErrorResponse of(String message, String errorCode) {
        return new ErrorResponse(message, errorCode, Instant.now().toString());
    }
}
