# Playbook: Convert MuleSoft API flows to a verified Spring Boot implementation

> **Facilitator / presenter:** this file is the source for a **Devin Playbook**.
> Copy its contents into your Devin organization (Settings → Playbooks → *Create
> a new Playbook*) so sessions can invoke it as `!convert-mulesoft-to-spring-boot`.
> See [Creating Playbooks](https://docs.devin.ai/product-guides/creating-playbooks).
> The repo-specific commands (make targets, namespaces, file paths) are kept
> in the companion Skill at `.agents/skills/mulesoft-to-spring-boot-migration/SKILL.md`,
> which Devin auto-loads when working in this repo.

## Overview

Convert **one or more** MuleSoft Mule 4 API flows into runnable, **verified**
Spring Boot 3.x controllers and services. The outcome is a PR containing the
Spring Boot implementation, passing unit tests, and contract verification
evidence proving the migrated app exposes the same API surface — same endpoints,
same request/response schemas, same HTTP status codes, same authentication flow —
as the source MuleSoft application.

## The one principle: the RAML/OpenAPI contract is the source of truth

A migration reproduces the MuleSoft API behavior faithfully — it does not
redesign it. If the source has an endpoint quirk (a non-standard error format, a
particular auth flow, a specific response structure), reproduce it and **flag
it** in a migration note — never silently "improve" it. Modernizing the API
design is a separate, deliberate decision made with the team, not a side effect
of migration. This is why "compiles and looks reasonable" review is not enough
and why every migration is gated by contract tests against the spec.

## Required from user

- **MuleSoft source repo** — the repo containing the Mule XML flows and RAML
  spec, e.g. `ts-java-mulesoft-employee-api`.
- **Endpoints to migrate** — which flows/endpoints to convert (or "all" for the
  full API surface).
- **Namespace** — an isolated branch name so concurrent runs do not collide,
  e.g. `migration/emp-goals` (outputs land on this branch).

## Procedure

1. Read the MuleSoft source: the Mule XML flow file(s) and the RAML/OAS API
   specification. Identify every endpoint (path + method), the authentication
   mechanism (OAuth2 client-credentials with token validation), database queries
   (SQL in `<db:select>`), DataWeave transformations, error handlers, and the
   exact response shapes.
2. Map each MuleSoft construct to its Spring Boot equivalent:
   - HTTP Listener → `@RestController` + `@RequestMapping`
   - APIKit router → Spring request routing (path variables, content types)
   - `<db:select>` / `<db:insert>` → Spring Data JPA Repository methods
   - DataWeave transforms → Java DTOs with Jackson serialization
   - `<choice>` + `<when>` → `if`/`switch` in service layer
   - `<try>` + `<on-error-propagate>` → `@ControllerAdvice` + exception handlers
   - Object Store token management → in-memory token store (ConcurrentHashMap or Spring Cache)
   - `<ee:transform>` response building → DTO construction in service methods
3. Write the Spring Boot implementation: controllers, services, repositories,
   DTOs, and configuration. Place all code in the `spring-boot-app/` directory
   following the existing package structure.
4. Write unit tests for each endpoint covering the happy path and primary error
   cases (unauthorized, not found).
5. Build and run the verification loop:
   - `./mvnw verify` in `spring-boot-app/` — must compile and pass unit tests
   - Start the app and run `./mvnw verify` in `verify/` — contract tests prove
     API parity against `contracts/openapi.yaml`
6. Close the loop: if any contract test fails, investigate **against the source
   MuleSoft flows and RAML** — do not relax the test. Correct the implementation
   and re-run until both the build and the contract tests are green.
7. Deliver a PR containing the implementation, passing tests, and a note
   documenting any source quirks reproduced (e.g., "token stored in memory
   matching ObjectStore behavior — not production-grade").

## Specifications (postconditions)

- The Spring Boot app builds cleanly (`./mvnw verify` green).
- Every endpoint defined in `contracts/openapi.yaml` is implemented and responds
  with the correct status codes and response shapes.
- OAuth2 client-credentials flow works identically: `POST /oauth/token` with
  valid credentials returns a bearer token; protected endpoints reject requests
  without a valid token.
- Contract verification tests pass: endpoint existence, schema validation,
  status code matching, auth flow parity.
- Any MuleSoft-specific behavior reproduced is explicitly flagged in code
  comments and the PR description — never silently changed.
- The PR targets the namespace branch, not `main`.

## Worked example: a real bug the verification catches

During conversion of the `/api/employee/{employeeId}/goals` endpoint, Devin
initially maps the MuleSoft `<choice>` error handler to a generic Spring
`ResponseEntity.notFound()`. The contract test catches that the source returns a
JSON body `{"message": "No goals found for employee 74"}` on 404, not an empty
body. Devin corrects the implementation to return the error response DTO with
the message field populated, matching the contract exactly. This is the kind of
subtle behavior mismatch that manual review misses but contract tests catch
systematically.

## Advice and pointers

- Start with the authentication flow — it gates all other endpoints and is the
  most likely source of contract divergence.
- The MuleSoft XML is verbose; focus on the `<flow>` elements and their
  `<db:select>` queries to understand what each endpoint actually does.
- DataWeave expressions in `<ee:transform>` define the response shape — these
  map directly to DTO field names and types.
- Use `@ActiveProfiles("test")` with H2 for unit tests; the contract tests run
  against a real PostgreSQL instance via Docker Compose.
- The `verify/` tests are integration tests (RestAssured against a running app),
  not mocks — they prove real behavior, not just compilation.

## Forbidden actions

- Do NOT modify `contracts/openapi.yaml` to make tests pass — the spec is the
  source of truth extracted from the MuleSoft RAML.
- Do NOT merge implementation into `main` — `main` is the durable before-state
  (scaffold + harness). Keep migrated code on the namespace branch.
- Do NOT skip the contract verification step — "it compiles" is not sufficient
  evidence of correctness.
- Do NOT redesign the API (change paths, rename fields, restructure responses)
  during migration — that is a separate task.
- Do NOT hardcode test credentials outside of the seed migration script.
