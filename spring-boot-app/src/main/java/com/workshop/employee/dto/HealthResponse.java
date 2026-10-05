package com.workshop.employee.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Response of {@code GET /health}. The Mule {@code health-check-flow} used the literal
 * values {@code healthy}/{@code unhealthy}; the contract's {@code HealthResponse}
 * schema constrains {@code status} to {@code UP}/{@code DOWN}, so those are used here.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record HealthResponse(String status, String database, String message, String timestamp) {
}
