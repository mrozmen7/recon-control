# recon-control

`recon-control` is a banking-grade reconciliation and settlement control
system built to learn and demonstrate real backend engineering patterns
used in financial systems.

## What Problem It Solves
The project targets real banking problems such as:

- duplicate or inconsistent transaction records across systems
- settlement lifecycle tracking gaps
- missing fraud-event propagation
- weak request traceability during incidents
- poor observability in production-like environments

## Technology Stack
- Java 21
- Spring Boot 3.5
- PostgreSQL
- Redis
- Kafka
- Flyway
- Spring Security + JWT
- Spring Boot Actuator + Prometheus
- Docker / Docker Compose
- GitHub Actions
- Testcontainers

## Architecture
- Modular Monolith
- Hexagonal Architecture
- Outbox Pattern
- Event-driven fraud evaluation
- Optimistic locking and idempotency

## Current Phase Coverage
### Faz 1
- core account and transaction foundation
- REST API, persistence, migrations

### Faz 2
- security, JWT, idempotency, rate limiting, transaction safety

### Faz 3
- Kafka, outbox, consumer flow, fraud detection, settlement events

### Faz 4
- containerization
- CI pipeline
- metrics and health endpoints
- structured logging and correlation id
- production-like runtime documentation

## Local Run
### Development profile
```bash
./mvnw spring-boot:run
```

### Production-like local stack
```bash
docker compose up -d --build
```

Useful URLs:
- Swagger: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Health: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- Prometheus metrics: [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)
- Prometheus UI: [http://localhost:9090](http://localhost:9090)

## Docs
- [ADR-004 Production Readiness](docs/adr/ADR-004-production-readiness.md)
- [Faz 4 Production Readiness Overview](docs/architecture/faz-4-production-readiness.md)

## Why This Matters For Banking Interviews
This project is intentionally not just CRUD. It demonstrates how a
backend service evolves from a simple transactional core into a
security-aware, event-driven, and observable production-style system.
