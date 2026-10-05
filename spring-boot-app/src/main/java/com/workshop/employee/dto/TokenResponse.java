package com.workshop.employee.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response of {@code POST /oauth/token}. Field names follow the DataWeave
 * "Token Response" transform in {@code oauth-token-flow}, including the extra
 * {@code scope: "read write"} the Mule app always returns.
 */
public record TokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") int expiresIn,
        @JsonProperty("scope") String scope) {
}
