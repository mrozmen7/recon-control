# AI-SAST Architecture Review

Date: 2026-04-04
Scope: `infra/security`, `account`, `transaction`, `fraud`, `infra/outbox`, `infra/kafka`
Reviewer: Codex manual AI-SAST pass

## System Shape

- API layer: Spring Boot REST controllers under `/api/v1/**`
- Persistence: PostgreSQL via Spring Data JPA
- Cache/rate control: Redis-backed controls
- Events: Kafka + outbox publisher
- Observability: Actuator, Prometheus, Elasticsearch, Kibana, Grafana

## Security-Relevant Entry Points

- `POST /api/v1/auth/token`
- `POST /api/v1/accounts`
- `GET /api/v1/accounts/{accountId}`
- `POST /api/v1/transactions`
- `GET /api/v1/transactions`
- `GET /api/v1/transactions/{transactionId}`
- `POST /api/v1/transactions/{transactionId}/settlement-pending`
- `POST /api/v1/transactions/{transactionId}/settle`
- `GET /api/v1/fraud/cases`
- `GET /api/v1/fraud/cases/{fraudCaseId}`
- `GET /actuator/health/**`
- `GET /actuator/info`
- `GET /actuator/prometheus`
- `GET /actuator/metrics/**`

## Trust Boundaries

1. External client -> Spring Boot API
2. Spring Boot API -> PostgreSQL / Redis / Kafka
3. Spring Boot API -> Observability endpoints
4. Internal operator roles -> sensitive business objects (`Account`, `InternalTransaction`, `FraudCase`)

## Security Model Observed

- Authentication is JWT-based with symmetric HS256 signing.
- Authorization is role-based at route level.
- No resource-level entitlement checks were observed in read flows.
- No maker-checker or approval workflow was observed in transaction creation.

## Priority Review Areas

1. JWT and secret management
2. Missing authentication / overexposed endpoints
3. Resource-level authorization and entitlement gaps
4. Business logic controls around balance-changing actions
5. Outbound request surfaces / SSRF sinks

## Overall Assessment

The codebase has a good security baseline for a demo or internal training system, but it is not yet operating at bank-grade authorization and secret-management depth. The dominant risk is not classic injection; it is control-plane design: broad role-based access, static secrets, and unrestricted operational posting capabilities.
