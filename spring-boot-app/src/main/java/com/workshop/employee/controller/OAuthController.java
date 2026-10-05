package com.workshop.employee.controller;

import com.workshop.employee.dto.ErrorResponse;
import com.workshop.employee.dto.TokenResponse;
import com.workshop.employee.service.OAuthService;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring equivalent of {@code oauth-token-flow} ({@code POST /oauth/token}).
 *
 * <p>MIGRATION NOTES (source behaviour reproduced, not redesigned):
 * <ul>
 *   <li>Credentials come from the request body ({@code client_id}/{@code client_secret}) or,
 *       when an {@code Authorization} header is present, from HTTP Basic auth — the header wins.</li>
 *   <li>{@code grant_type} is read with a default of {@code client_credentials} and never validated.</li>
 *   <li>The 401 body in Mule was {@code {"error": "invalid_client", "error_description": ...}};
 *       the contract's {@code ErrorResponse} shape is used with the Mule code in {@code errorCode}.</li>
 *   <li>The optional {@code callback_url} / Salesforce async callback is not part of the
 *       contract and is not migrated.</li>
 * </ul>
 */
@RestController
public class OAuthController {

    private static final Logger log = LoggerFactory.getLogger(OAuthController.class);
    private static final String BASIC_PREFIX = "Basic ";
    private static final String TOKEN_TYPE = "Bearer";
    private static final String SCOPE = "read write";

    private final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @PostMapping(value = "/oauth/token", produces = "application/json")
    public ResponseEntity<?> token(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestParam(value = "grant_type", required = false, defaultValue = "client_credentials") String grantType,
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam(value = "client_secret", required = false) String clientSecret) {

        log.info("OAuth token request received");

        String effectiveClientId = clientId;
        String effectiveClientSecret = clientSecret;
        if (authorization != null) {
            String[] basicCredentials = decodeBasic(authorization);
            effectiveClientId = basicCredentials[0];
            effectiveClientSecret = basicCredentials[1];
        }

        log.info("Validating client credentials for client: {}", effectiveClientId);
        final String resolvedClientId = effectiveClientId;

        return oAuthService.issueToken(effectiveClientId, effectiveClientSecret)
                .<ResponseEntity<?>>map(token -> ResponseEntity.ok(
                        new TokenResponse(token, TOKEN_TYPE, oAuthService.getTokenExpirySeconds(), SCOPE)))
                .orElseGet(() -> {
                    log.warn("Invalid client credentials: {}", resolvedClientId);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(ErrorResponse.of("Client authentication failed", "invalid_client"));
                });
    }

    /** Mule: {@code vars.authHeader[6 to -1]} base64-decoded and split on the first ':'. */
    private static String[] decodeBasic(String authorization) {
        if (!authorization.regionMatches(true, 0, BASIC_PREFIX, 0, BASIC_PREFIX.length())) {
            return new String[] {null, null};
        }
        try {
            String decoded = new String(
                    Base64.getDecoder().decode(authorization.substring(BASIC_PREFIX.length()).trim()),
                    StandardCharsets.UTF_8);
            int separator = decoded.indexOf(':');
            if (separator < 0) {
                return new String[] {decoded, null};
            }
            return new String[] {decoded.substring(0, separator), decoded.substring(separator + 1)};
        } catch (IllegalArgumentException malformedBase64) {
            return new String[] {null, null};
        }
    }
}
