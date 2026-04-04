# recon-control

`recon-control` is a banking-oriented backend project designed to model how a real internal financial platform evolves from a transactional core into a secure, event-driven, observable, and operations-aware system.

The project is intentionally not a basic CRUD application. It brings together account management, transaction booking, settlement lifecycle control, fraud evaluation, event propagation, observability, security review, and AI-ready operational support in a single learning system.

## 1. Problem

Modern banking systems do not fail because teams cannot create tables and APIs. They fail when:

- transaction state changes are not propagated safely across systems
- fraud evaluation happens too late or outside the event flow
- duplicate or replayed requests create financial inconsistencies
- operators cannot trace a request during incidents
- logs and metrics exist, but remain too fragmented to act on quickly
- security review is informal and business-logic risks are missed

This project was built to learn and demonstrate how to address those problems in a production-oriented way.

## 2. Solution

The system models a realistic internal banking backend with these capabilities:

- account creation and retrieval
- internal transaction booking
- settlement lifecycle transitions
- event-driven fraud evaluation
- Kafka plus outbox-based event publication
- structured logging, metrics, dashboards, and searchable logs
- AI-ready operations endpoints for incident summary, correlation explanation, and operational Q and A
- markdown-coordinated AI-SAST security workflow for threat modeling and security triage

## 3. Architecture

High-level design choices:

- Modular monolith
- Hexagonal architecture
- PostgreSQL as the system of record
- Redis for supporting runtime concerns
- Kafka plus outbox pattern for reliable event publication
- Spring Security plus JWT for internal role-based access
- Prometheus, Grafana, Elasticsearch, Kibana, and Filebeat for observability
- Markdown-driven AI-SAST workflow coordinated through `CLAUDE.md` and security playbooks

### Architectural flow

```text
Internal users / Swagger
-> Spring Boot APIs
-> account / transaction / fraud / ops modules
-> PostgreSQL + Redis
-> outbox + Kafka event flow
-> observability stack (Prometheus, Grafana, Elasticsearch, Kibana, Filebeat)
-> ops assistant layer over logs, metrics, and internal docs
```

## 4. Technology Stack

- Java 21
- Spring Boot 3.5
- PostgreSQL
- Redis
- Kafka
- Flyway
- Spring Security + JWT
- Spring Boot Actuator
- Prometheus
- Grafana
- Elasticsearch
- Kibana
- Filebeat
- Docker / Docker Compose
- JUnit
- Testcontainers
- GitHub Actions

## 5. Phase Coverage

### Phase 1

- core account and transaction foundation
- REST APIs, persistence, migrations

### Phase 2

- security hardening
- JWT authentication
- idempotency
- rate limiting
- transaction safety controls

### Phase 3

- Kafka integration
- outbox pattern
- event-driven fraud evaluation
- settlement event flow

### Phase 4

- production-like local stack with Docker Compose
- health, metrics, and Prometheus exposure
- Grafana dashboards
- Elasticsearch, Kibana, and Filebeat log pipeline
- structured logging and correlation id support
- CI pipeline and runtime documentation

### Phase 5 MVP

- incident summary API
- correlation explanation API
- ops assistant API
- heuristic operational intelligence over logs, metrics, and internal docs
- AI-ready extension path for future LLM integration

## 6. Security Architecture

One of the most important parts of the project is the security workflow, not just the application code.

The repository includes a markdown-based AI-SAST foundation with:

- threat model
- scan policy
- triage policy
- finding template
- dated security reports

This workflow is designed so that AI-assisted security analysis remains:

- controlled
- repeatable
- reviewable
- team-friendly

Rather than replacing engineering judgment, it supports human review and remediation planning.

### AI-SAST review flow

```text
Developer branch
-> AI-SAST orchestration
-> architecture / threat analysis
-> mandatory scans
-> optional scans
-> dated reports
-> human triage
-> remediation or merge decision
```

## 7. How It Works

### Core runtime

1. An internal user authenticates and receives a JWT.
2. Accounts and transactions are managed through secured Spring Boot APIs.
3. Transaction state changes are persisted in PostgreSQL.
4. Outbox entries are published to Kafka.
5. Fraud evaluation reacts to transaction events.
6. Logs and metrics are emitted continuously.
7. Prometheus and Filebeat collect telemetry.
8. Grafana and Kibana provide operational visibility.
9. The ops assistant endpoints summarize and explain system state using recent logs, metrics, and internal docs.

### Ops assistant layer

The project includes three operations-oriented endpoints:

- `POST /api/v1/ops/incident-summary`
- `GET /api/v1/ops/correlation/{correlationId}`
- `POST /api/v1/ops/assistant`

These endpoints are intentionally implemented as a local, testable MVP. They do not depend on an external LLM provider yet, but they are structured to support future LLM and retrieval integration.

## 8. Local Run

### Development profile

```bash
./mvnw spring-boot:run
```

### Production-like local stack

```bash
docker compose up -d --build
```

### Useful local URLs

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- Health: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- Metrics: [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics)
- Prometheus scrape endpoint: [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)
- Prometheus UI: [http://localhost:9090](http://localhost:9090)
- Grafana: [http://localhost:3000](http://localhost:3000)
- Elasticsearch: [http://localhost:9200](http://localhost:9200)
- Kibana: [http://localhost:5601](http://localhost:5601)

Grafana default local credentials:

- username: `admin`
- password: `admin123`

## 9. Testing

Run the automated test suite with:

```bash
./mvnw -q test
```

The project includes controller, integration, and event-flow tests, including validation of the Phase 5 ops assistant APIs.

## 10. Documentation

### ADRs

- [ADR-004 Production Readiness](docs/adr/ADR-004-production-readiness.md)
- [ADR-005 Observability Stack](docs/adr/ADR-005-observability-stack.md)

### Architecture docs

- [Phase 4 Production Readiness](docs/architecture/faz-4-production-readiness.md)
- [Phase 4 Observability Stack](docs/architecture/faz-4-observability-stack.md)
- [Professional Architecture Diagram](docs/architecture/recon-control-professional-architecture.svg)

### Security docs

- [AI-SAST Playbook](docs/security/ai-sast-playbook.md)
- [Threat Model](docs/security/threat-model.md)
- [Security Triage Policy](docs/security/triage-policy.md)

## 11. Why This Project Matters

This project is meant to demonstrate more than framework familiarity.

It shows how a backend service can be designed with:

- banking-style transactional thinking
- event-driven integration patterns
- production-like observability
- operational traceability
- security-oriented workflow discipline
- AI-ready extension paths for future operations tooling

In short, the goal is not just to build a working backend, but to build one that is closer to how real engineering teams think about reliability, security, and operational clarity.
