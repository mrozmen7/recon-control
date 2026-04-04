# Threat Model

## Primary Users

- operations users
- operations admins
- auditors
- backend/platform engineers

## Sensitive Assets

- account balances
- transaction records
- settlement state transitions
- fraud cases
- JWT secrets and auth configuration
- operational logs and metrics

## Entry Points

- REST API under `/api/v1`
- JWT issuance endpoint
- Kafka consumer and producer flow
- Actuator and observability endpoints
- Grafana, Kibana, and Prometheus UIs in local production-like mode

## Trust Boundaries

1. external client -> REST API
2. REST API -> application/domain services
3. application -> PostgreSQL
4. application -> Redis
5. application -> Kafka
6. logs/metrics -> observability stack

## Highest-Risk Abuse Paths

### Broken authorization

- non-admin user reaches settlement mutation endpoints
- auditor role reads data it should not access
- account or fraud case access bypasses ownership rules

### JWT issues

- weak signing configuration
- insecure token validation
- role escalation via claim handling mistakes

### IDOR

- user reads another account or transaction by guessing an identifier
- fraud cases are retrievable without proper authorization

### Business logic flaws

- duplicate transaction creation
- settlement transitions bypass expected state order
- money movement rules violated by incorrect orchestration

### SSRF and outbound call risk

- future AI/LLM or external-service integrations call unintended destinations

## Security Review Focus

The most important questions for this project are:

1. who can read and mutate financial records?
2. can request identity be forged or escalated?
3. can workflow states be skipped or replayed?
4. can logs or observability endpoints leak sensitive operational details?
5. can future outbound integrations be abused?
