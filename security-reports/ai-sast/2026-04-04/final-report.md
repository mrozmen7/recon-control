# AI-SAST Final Report

Date: 2026-04-04
Scope: `infra/security`, `account`, `transaction`, `fraud`, `infra/outbox`, `infra/kafka`
Assessment mode: Manual AI-SAST assisted review
Disposition: Advisory findings pending human triage

## Executive Summary

The codebase has a solid demo-grade baseline: JWT auth exists, route-level roles exist, event processing is structured, and observability is strong. The main security concerns are not injection-heavy; they are control-depth concerns expected in banking systems:

1. broad role-only access to sensitive records
2. unrestricted balance-affecting transaction posting
3. static secrets and embedded demo identities
4. public exposure of detailed telemetry endpoints

## Ranked Findings

### 1. High: Unrestricted balance-affecting transaction creation

- Report: [businesslogic-results.md](/Users/yvz.o/Desktop/projects/recon-control/security-reports/ai-sast/2026-04-04/businesslogic-results.md)
- Why it matters:
  Any `OPS_USER` can post arbitrary credits or debits against any active account id they can reference.

### 2. High: Missing resource-level authorization / entitlement scoping

- Report: [idor-results.md](/Users/yvz.o/Desktop/projects/recon-control/security-reports/ai-sast/2026-04-04/idor-results.md)
- Why it matters:
  Internal read access is broad and global; there is no branch, desk, or case-assignment scoping.

### 3. Medium: Publicly exposed Prometheus and detailed metric endpoints

- Report: [missingauth-results.md](/Users/yvz.o/Desktop/projects/recon-control/security-reports/ai-sast/2026-04-04/missingauth-results.md)
- Why it matters:
  Runtime internals are exposed without auth on the main app port.

### 4. Medium: Static JWT secret and embedded demo identities

- Report: [jwt-results.md](/Users/yvz.o/Desktop/projects/recon-control/security-reports/ai-sast/2026-04-04/jwt-results.md)
- Why it matters:
  The current identity model is acceptable for local learning, but weak for any shared or higher environment.

## No-Finding Area

- SSRF: [ssrf-results.md](/Users/yvz.o/Desktop/projects/recon-control/security-reports/ai-sast/2026-04-04/ssrf-results.md)

## Recommended Remediation Order

1. Add authorization depth for resource access and transaction posting.
2. Harden transaction workflow with dual control / limit policies.
3. Move secrets and demo credentials behind environment-specific controls.
4. Restrict telemetry endpoints to internal scrape paths only.

## Release Readiness View

- Local training/demo: acceptable with clear non-production labeling
- Shared internal environment: needs secret hardening and telemetry restriction
- Bank-grade environment: needs entitlement model and transaction control redesign before sign-off
