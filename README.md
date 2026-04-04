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
- Grafana
- Elasticsearch + Kibana + Filebeat
- Docker / Docker Compose
- GitHub Actions
- Testcontainers

## Architecture
- Modular Monolith
- Hexagonal Architecture
- Outbox Pattern
- Event-driven fraud evaluation
- Optimistic locking and idempotency
- Metrics + centralized logging observability stack

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
- Prometheus + Grafana dashboards
- Elasticsearch + Kibana + Filebeat log search
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
- Metrics: [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics)
- Prometheus scrape: [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)
- Prometheus UI: [http://localhost:9090](http://localhost:9090)
- Grafana: [http://localhost:3000](http://localhost:3000)
  admin / admin123
- Elasticsearch: [http://localhost:9200](http://localhost:9200)
- Kibana: [http://localhost:5601](http://localhost:5601)

## Docs
- [ADR-004 Production Readiness](/Users/yvz.o/Desktop/projects/recon-control/docs/adr/ADR-004-production-readiness.md)
- [ADR-005 Advanced Observability Stack](/Users/yvz.o/Desktop/projects/recon-control/docs/adr/ADR-005-observability-stack.md)
- [Faz 4 Production Readiness Overview](/Users/yvz.o/Desktop/projects/recon-control/docs/architecture/faz-4-production-readiness.md)
- [Faz 4 Advanced Observability Stack](/Users/yvz.o/Desktop/projects/recon-control/docs/architecture/faz-4-observability-stack.md)
- [AI-SAST Playbook](/Users/yvz.o/Desktop/projects/recon-control/docs/security/ai-sast-playbook.md)
- [Threat Model](/Users/yvz.o/Desktop/projects/recon-control/docs/security/threat-model.md)
- [Security Triage Policy](/Users/yvz.o/Desktop/projects/recon-control/docs/security/triage-policy.md)

## Why This Matters For Banking Interviews
This project is intentionally not just CRUD. It demonstrates how a
backend service evolves from a simple transactional core into a
security-aware, event-driven, and observable production-style system.
