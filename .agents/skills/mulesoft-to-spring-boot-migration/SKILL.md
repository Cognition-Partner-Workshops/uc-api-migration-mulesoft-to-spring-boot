---
name: mulesoft-to-spring-boot-migration
description: >
  Repo-specific mechanics for migrating MuleSoft Mule 4 APIs to Spring Boot 3.x.
  Auto-loaded when Devin works in this repository. Contains exact commands,
  file paths, namespaces, and verification steps.
---

# MuleSoft → Spring Boot Migration Skill

## Repository Layout

```
uc-api-migration-mulesoft-to-spring-boot/
├── contracts/openapi.yaml        ← SOURCE OF TRUTH (do not modify)
├── spring-boot-app/              ← Target: Devin writes implementation here
│   ├── pom.xml
│   ├── src/main/java/com/workshop/employee/
│   │   ├── EmployeeServiceApplication.java
│   │   ├── config/               ← @Configuration classes
│   │   ├── controller/           ← @RestController classes
│   │   ├── dto/                  ← Request/Response records
│   │   ├── model/                ← JPA @Entity classes
│   │   ├── repository/           ← Spring Data JPA interfaces
│   │   ├── service/              ← Business logic
│   │   └── exception/            ← @ControllerAdvice handlers
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/V1__create_tables.sql
│   └── src/test/java/            ← Unit tests
├── verify/                       ← Contract test harness
│   ├── pom.xml
│   └── src/test/java/.../ContractVerificationIT.java
├── docker/docker-compose.yml     ← Local PostgreSQL
└── Makefile                      ← Convenience targets
```

## Source Estate

The MuleSoft source lives at:
- **Repo:** `Cognition-Partner-Workshops/ts-java-mulesoft-employee-api`
- **Main flow:** `src/main/mule/employee-services-api.xml`
- **API spec:** `src/main/resources/api/employee-services-api.raml`
- **DB schema:** `database-setup.sql`

Clone it alongside this repo for reference:
```bash
git clone https://github.com/Cognition-Partner-Workshops/ts-java-mulesoft-employee-api.git ../ts-java-mulesoft-employee-api
```

## Commands

### Start local database
```bash
docker compose -f docker/docker-compose.yml up -d
```

### Build the Spring Boot app
```bash
cd spring-boot-app && ./mvnw clean verify
```

### Run the Spring Boot app (for contract tests)
```bash
cd spring-boot-app && ./mvnw spring-boot:run
```

### Run contract verification tests
```bash
cd verify && ./mvnw verify -Dapp.base-url=http://localhost:8080
```

### Full verification (convenience)
```bash
make verify
```

## Namespace Convention

Each migration run uses a branch: `migration/<namespace>`

Example: `migration/employee-api-full`

Create and push:
```bash
git checkout -b migration/<namespace>
# ... implement ...
git push origin migration/<namespace>
```

## MuleSoft → Spring Boot Mapping Reference

| MuleSoft Construct | Spring Boot Equivalent |
|---|---|
| `<http:listener path="/api/...">` | `@RestController` + `@GetMapping("/api/...")` |
| `<apikit:router>` | Spring MVC request routing |
| `<db:select>` with SQL | `@Repository` interface extending `JpaRepository` |
| `<db:insert>` | `repository.save(entity)` |
| `<ee:transform>` (DataWeave) | DTO construction / `@JsonProperty` |
| `<choice><when expr="...">` | `if`/`switch` in `@Service` |
| `<try><on-error-propagate>` | `@ControllerAdvice` + `@ExceptionHandler` |
| `<os:store>` / `<os:retrieve>` | `ConcurrentHashMap` or Spring Cache |
| `<set-variable>` | Local variables in service methods |
| `<logger>` | `@Slf4j` / `LoggerFactory` |
| RAML types | Java records / DTO classes |

## Verification Checklist

Before opening a PR, confirm:

- [ ] `cd spring-boot-app && ./mvnw clean verify` → BUILD SUCCESS
- [ ] App starts: `./mvnw spring-boot:run` serves on port 8080
- [ ] `POST /oauth/token` with `demo-client`/`demo-secret` returns a token
- [ ] All `/api/employee/{id}/*` endpoints respond correctly with token
- [ ] `cd verify && ./mvnw verify` → all contract tests GREEN
- [ ] No modifications to `contracts/openapi.yaml`
- [ ] Code on a `migration/*` branch, not `main`

## Error Response Format

All error responses MUST match this shape (per the OpenAPI contract):

```json
{
  "message": "Human-readable error description",
  "errorCode": "OPTIONAL_CODE",
  "timestamp": "2025-01-01T00:00:00Z"
}
```

## Database Connection (local dev)

| Property | Value |
|----------|-------|
| Host | `localhost` |
| Port | `5432` |
| Database | `employee_db` |
| Username | `employee_user` |
| Password | `employee_pass` |

These are set in `docker/docker-compose.yml` and referenced in
`spring-boot-app/src/main/resources/application.yml` via env vars with defaults.
