# recon-control Project Constitution


## Project Overview

Banking-grade reconciliation and settlement control system.

Primary goal:

- compare transaction records across systems
- classify mismatches deterministically
- track settlement lifecycle
- turn exceptions into actionable operational cases

## Architecture

- Pattern: Modular Monolith
- Internal style: Hexagonal Architecture
- Package root: `com.yavuzozmen.reconcontrol`

## Non-Negotiable Rules

- Domain logic must not import Spring, JPA, Redis, or Kafka classes
- All monetary values must use `BigDecimal`
- No Lombok
- Constructor injection only
- No field injection
- No business logic in controllers, listeners, schedulers, or repositories
- Mutable entities must use optimistic locking where appropriate
- DTOs and entities must stay separate
- Every public use case must have tests

## Initial Modules

- `common`
- `account`
- `transaction`
- `reconciliation`
- `settlement`
- `casemanagement`
- `audit`
- `infra`

## Technology Baseline

- Java 21
- Spring Boot 3.5.x
- PostgreSQL
- Redis
- Flyway
- Spring Security
- Testcontainers

## API Standards

- REST + `/api/v1`
- validation on all incoming DTOs
- clear business error responses
- pagination on list endpoints

## Git Standards

- main branch protected conceptually
- use small focused commits
- branch names: `feature/...`, `docs/...`, `refactor/...`
- no unrelated changes in the same commit
