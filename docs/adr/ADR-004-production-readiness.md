# ADR-004: Production Readiness Baseline

## Status
Accepted

## Context
By the end of Faz 3, the system already handled accounts, transactions,
security, idempotency, Kafka events, outbox publishing, and fraud case
creation. The next bottleneck was operability.

The team needed a baseline that answers these questions:

- Can the service be packaged and run consistently in containers?
- Can operators check health and scrape metrics?
- Can logs be correlated across requests?
- Can CI validate the build on every change?

## Decision
We adopt the following production-readiness baseline:

- multi-stage Docker build with a slim runtime image
- Docker Compose runtime including app, PostgreSQL, Redis, Kafka, and
  Prometheus
- Spring Boot Actuator for health, info, metrics, and Prometheus export
- correlation-id propagation through an HTTP filter
- structured JSON logging in the docker profile
- GitHub Actions CI for test, package, and Docker build validation

## Consequences
### Positive
- The service can be run in a production-like local topology.
- Operators can scrape metrics and inspect liveness/readiness quickly.
- Logs are easier to trace during incident analysis.
- CI catches build and test regressions earlier.

### Trade-offs
- Local setup becomes heavier because more infrastructure is involved.
- JSON logs are less human-friendly unless viewed through tooling.
- Additional configuration must stay aligned across local and CI
  environments.
