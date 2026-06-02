# MuleSoft → Spring Boot API Migration

> Target landing zone for migrating MuleSoft Mule 4 APIs to Spring Boot 3.x.
> The source estate lives in
> [`ts-java-mulesoft-employee-api`](https://github.com/Cognition-Partner-Workshops/ts-java-mulesoft-employee-api).

## Purpose

This repo is the **migration target** — it holds:

| Directory | Purpose |
|-----------|---------|
| `contracts/` | OpenAPI 3.0 spec extracted from the MuleSoft RAML — the **source of truth** for API parity |
| `spring-boot-app/` | Spring Boot 3.5 application scaffold (Devin populates the implementation live) |
| `verify/` | Contract-test harness that proves API parity between the spec and the running Spring Boot app |
| `docker/` | Docker Compose for local PostgreSQL + the Spring Boot app |
| `.workshop/playbooks/` | Portable Devin Playbook (`!convert-mulesoft-to-spring-boot`) |
| `.agents/skills/` | Repo-specific Skill auto-loaded by Devin |

## Demo Flow (how it works)

1. **Devin reads** the MuleSoft source (XML flows + RAML) from `ts-java-mulesoft-employee-api`
2. **Devin writes** Spring Boot controllers, services, repositories, and DTOs in `spring-boot-app/`
3. **Verification loop** — programmatic, not "looks good":
   - `./mvnw verify` in `spring-boot-app/` — must compile, pass unit tests
   - `./mvnw verify` in `verify/` — contract tests compare the running app against `contracts/openapi.yaml`
   - Any parity mismatch → Devin investigates against the RAML source of truth and fixes
4. **PR** with the implementation + passing verification evidence

## Quick Start (local development)

```bash
# Start PostgreSQL
docker compose -f docker/docker-compose.yml up -d

# Build the Spring Boot app (after Devin has produced the implementation)
cd spring-boot-app && ./mvnw clean verify

# Run contract tests
cd ../verify && ./mvnw clean verify
```

## Verification Controls

The contract test harness (`verify/`) checks:

- **Endpoint existence** — every path in the OpenAPI spec has a corresponding controller
- **Request/response schemas** — JSON payloads match the defined models
- **HTTP status codes** — success, 404, 401 match the contract
- **Authentication flow** — OAuth2 client-credentials token endpoint works identically
- **Error responses** — error shapes match the contract

## Prerequisites

- Java 21
- Maven 3.9+
- Docker & Docker Compose
- Access to `ts-java-mulesoft-employee-api` (the MuleSoft source)

## Namespace Isolation

Each demo run uses an isolated branch (`migration/<namespace>`) so concurrent
sessions never collide. The playbook handles namespace assignment automatically.
