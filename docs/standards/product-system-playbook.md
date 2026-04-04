# Product And System Thinking Playbook

## Purpose
This project must be designed and explained like a real production banking
system, not like a tutorial CRUD application.

The team must always think in this order:

1. Problem
2. Solution
3. Technology

## Who The Users Are In This Project
The primary users are:

- operations analysts who investigate transaction issues
- settlement and reconciliation teams who need traceability
- auditors who need evidence and history
- backend and platform engineers who need observability and incident insight

## Core Working Rules

### 1. Problem First
Before any feature is added, answer:

- who is affected?
- what pain exists today?
- why is it important in real banking operations?

If the problem is weak or unclear, the feature should not be implemented.

### 2. Solution Before Code
Every feature discussion must follow:

- Problem
- Solution
- Technology

Technology is not the reason. It is only the implementation choice.

### 3. No Meaningless Features
Reject any feature that does not create real user or operator value.

Examples to reject:

- cosmetic endpoints with no operational purpose
- CRUD-only additions without business meaning
- architecture complexity without a problem to justify it

### 4. Explain Every Decision
For each technical choice, explain:

- why it exists
- what problem it solves
- why a simpler option was or was not enough

### 5. Prefer The Simplest Working Design
Choose the smallest design that safely solves the problem.
Do not introduce abstractions before the system actually needs them.

### 6. Think In System Components
Always describe the interaction between:

- API
- database
- cache
- events
- observability
- optional AI capability

### 7. AI-First Mindset
For every major feature, ask:

- can AI generate insight for operators?
- can AI help classify, summarize, or recommend actions?

In this project, AI is optional and must only be added when it clearly helps
real users such as fraud analysts, reconciliation teams, or operations staff.

### 8. Mandatory Feature Checklist
Before implementation, confirm:

- user problem is defined
- solution is clear
- API design is clear
- edge cases are considered
- error handling is included
- performance impact is considered
- the feature can be demonstrated

### 9. Validate User Value
Before closing a feature, answer:

- what changed for the user?
- what changed for operations?
- what became faster, safer, clearer, or easier?

### 10. Demo Thinking
Every feature must be demonstrable using one or more of:

- Swagger endpoint
- observable logs
- dashboard or metrics view
- clear scenario narrative

### 11. README Structure
Project documentation should normally follow:

1. Problem
2. Solution
3. Architecture
4. Tech Stack
5. How It Works
6. Why This Design

### 12. Communication Standard
Every major feature should be explainable in one sentence like this:

I built this system to solve X problem for Y users using Z approach.

### 13. Reject Bad Engineering Habits
Do not allow:

- tutorial copy-paste architecture
- unexplained code
- feature creep
- tool-driven development without a user problem

### 14. Daily Execution Mode
Work feature by feature.
For each step:

- define the problem
- explain the solution
- explain the chosen technology
- implement
- test
- validate user value

## Applying This To Recon Control

### Product Problem
Banking teams cannot rely only on successful API responses. They need to know:

- whether a transaction was created
- whether it was settled
- whether it triggered fraud review
- whether logs and metrics show what happened during incidents

### Product Solution
Recon Control provides:

- transaction lifecycle visibility
- deterministic audit trail
- fraud case generation
- production-style observability

### Technology Mapping
Only after the solution is clear do we use technology:

- Spring Boot for API and business orchestration
- PostgreSQL for durable records
- Redis for idempotency and rate limiting
- Kafka for event-driven reactions
- Prometheus for metrics
- Elasticsearch and Kibana for log search
- Grafana for dashboards
- Filebeat for log shipping

## Observability Rule For This Project
Observability is not a cosmetic add-on.
It exists to solve real production questions:

- why did this request fail?
- which transaction caused the alert?
- what was the correlation id?
- did latency or error rate change?
- can operators and engineers see the same truth?

If an observability component does not answer a real operational question, it
should not be added.
