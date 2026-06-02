package com.workshop.verify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * API Contract Verification Tests.
 *
 * These tests prove parity between the OpenAPI spec (contracts/openapi.yaml)
 * and the running Spring Boot application. They validate:
 * - Endpoint existence (every path in the spec has a handler)
 * - Response schemas (JSON shapes match the contract)
 * - HTTP status codes (success, 401, 404 match expected behavior)
 * - Authentication flow (OAuth2 client-credentials works identically)
 *
 * Run against a live instance:
 *   mvn verify -Dapp.base-url=http://localhost:8080
 */
class ContractVerificationIT {

    private static final String BASE_URL = System.getProperty("app.base-url", "http://localhost:8080");
    private static JsonNode openApiSpec;

    @BeforeAll
    static void setup() throws IOException {
        RestAssured.baseURI = BASE_URL;
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        File specFile = new File("../contracts/openapi.yaml");
        openApiSpec = yamlMapper.readTree(specFile);
    }

    @Nested
    @DisplayName("Health Endpoint")
    class HealthEndpoint {

        @Test
        @DisplayName("GET /health returns 200 with status field")
        void healthReturns200() {
            given()
                .when()
                    .get("/health")
                .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("status", notNullValue());
        }
    }

    @Nested
    @DisplayName("OAuth2 Token Endpoint")
    class OAuthEndpoint {

        @Test
        @DisplayName("POST /oauth/token with valid credentials returns token")
        void validCredentialsReturnToken() {
            given()
                .contentType(ContentType.URLENC)
                .formParam("grant_type", "client_credentials")
                .formParam("client_id", "demo-client")
                .formParam("client_secret", "demo-secret")
            .when()
                .post("/oauth/token")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("access_token", notNullValue())
                .body("token_type", equalTo("Bearer"))
                .body("expires_in", equalTo(3600));
        }

        @Test
        @DisplayName("POST /oauth/token with invalid credentials returns 401")
        void invalidCredentialsReturn401() {
            given()
                .contentType(ContentType.URLENC)
                .formParam("grant_type", "client_credentials")
                .formParam("client_id", "bad-client")
                .formParam("client_secret", "bad-secret")
            .when()
                .post("/oauth/token")
            .then()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("message", notNullValue());
        }
    }

    @Nested
    @DisplayName("Employee Goals Endpoint")
    class GoalsEndpoint {

        @Test
        @DisplayName("GET /api/employee/{id}/goals without token returns 401")
        void noTokenReturns401() {
            given()
            .when()
                .get("/api/employee/EMP001/goals")
            .then()
                .statusCode(401);
        }

        @Test
        @DisplayName("GET /api/employee/{id}/goals with valid token returns goals array")
        void validTokenReturnsGoals() {
            String token = obtainToken();

            given()
                .header("Authorization", "Bearer " + token)
            .when()
                .get("/api/employee/EMP001/goals")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", isA(java.util.List.class))
                .body("size()", greaterThan(0));
        }

        @Test
        @DisplayName("GET /api/employee/{id}/goals for unknown employee returns 404")
        void unknownEmployeeReturns404() {
            String token = obtainToken();

            given()
                .header("Authorization", "Bearer " + token)
            .when()
                .get("/api/employee/UNKNOWN999/goals")
            .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("message", notNullValue());
        }
    }

    @Nested
    @DisplayName("Employee Learning Status Endpoint")
    class LearningEndpoint {

        @Test
        @DisplayName("GET /api/employee/{id}/learning-status returns course data")
        void returnsLearningStatus() {
            String token = obtainToken();

            given()
                .header("Authorization", "Bearer " + token)
            .when()
                .get("/api/employee/EMP001/learning-status")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("employeeId", equalTo("EMP001"))
                .body("courses", notNullValue())
                .body("courses.size()", greaterThan(0))
                .body("courses[0].courseName", notNullValue())
                .body("courses[0].status", notNullValue())
                .body("courses[0].progress", notNullValue());
        }
    }

    @Nested
    @DisplayName("Next Pay Date Endpoint")
    class PayDateEndpoint {

        @Test
        @DisplayName("GET /api/employee/{id}/next-pay-date returns pay date info")
        void returnsPayDate() {
            String token = obtainToken();

            given()
                .header("Authorization", "Bearer " + token)
            .when()
                .get("/api/employee/EMP001/next-pay-date")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("employeeId", equalTo("EMP001"))
                .body("nextPayDate", notNullValue());
        }
    }

    @Nested
    @DisplayName("PTO Balance Endpoint")
    class PtoBalanceEndpoint {

        @Test
        @DisplayName("GET /api/employee/{id}/pto/balance returns balance info")
        void returnsPtoBalance() {
            String token = obtainToken();

            given()
                .header("Authorization", "Bearer " + token)
            .when()
                .get("/api/employee/EMP001/pto/balance")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("employeeId", equalTo("EMP001"))
                .body("balance", notNullValue())
                .body("used", notNullValue())
                .body("total", notNullValue());
        }
    }

    @Nested
    @DisplayName("PTO Schedule Endpoint")
    class PtoScheduleEndpoint {

        @Test
        @DisplayName("POST /api/employee/{id}/pto/schedule creates a PTO request")
        void schedulePto() {
            String token = obtainToken();

            given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "startDate": "2025-12-22",
                        "endDate": "2025-12-26",
                        "hours": 40.0
                    }
                    """)
            .when()
                .post("/api/employee/EMP001/pto/schedule")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("message", notNullValue())
                .body("hoursScheduled", equalTo(40.0f));
        }

        @Test
        @DisplayName("POST /api/employee/{id}/pto/schedule without token returns 401")
        void noTokenReturns401() {
            given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "startDate": "2025-12-22",
                        "endDate": "2025-12-26",
                        "hours": 40.0
                    }
                    """)
            .when()
                .post("/api/employee/EMP001/pto/schedule")
            .then()
                .statusCode(401);
        }
    }

    @Nested
    @DisplayName("OpenAPI Spec Completeness")
    class SpecCompleteness {

        @Test
        @DisplayName("All spec paths have corresponding endpoints that respond (not 405)")
        void allSpecPathsExist() {
            JsonNode paths = openApiSpec.get("paths");
            String token = obtainToken();

            paths.fieldNames().forEachRemaining(path -> {
                // Replace path parameter placeholder
                String resolvedPath = path.replace("{employeeId}", "EMP001");

                JsonNode methods = paths.get(path);
                methods.fieldNames().forEachRemaining(method -> {
                    Response response;
                    if ("get".equals(method)) {
                        response = given()
                            .header("Authorization", "Bearer " + token)
                            .get(resolvedPath);
                    } else if ("post".equals(method)) {
                        response = given()
                            .header("Authorization", "Bearer " + token)
                            .contentType(ContentType.JSON)
                            .body("{}")
                            .post(resolvedPath);
                    } else {
                        return;
                    }
                    // 405 means the endpoint doesn't exist — contract violation
                    assert response.statusCode() != 405 :
                        "Endpoint " + method.toUpperCase() + " " + path + " not implemented (got 405)";
                });
            });
        }
    }

    private static String obtainToken() {
        return given()
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "client_credentials")
            .formParam("client_id", "demo-client")
            .formParam("client_secret", "demo-secret")
        .when()
            .post("/oauth/token")
        .then()
            .statusCode(200)
            .extract()
            .path("access_token");
    }
}
