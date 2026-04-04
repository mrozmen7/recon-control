# Initial Manual AI-SAST Scan Plan

## Problem

The first AI-assisted security scan should not try to scan every module equally. In a banking-style backend, the highest-value review starts where identity, authorization, money movement, and workflow control intersect.

## Goal

Run the first manual AI-SAST exercise against the parts of the codebase where a real financial-system vulnerability would matter most.

## Scan Order

### 1. `infra/security`

Why first:

- contains JWT issuance and validation
- contains role and endpoint access rules
- mistakes here affect the entire system

Primary questions:

- can roles be bypassed?
- are public endpoints intentionally public?
- is JWT handling strict enough?

Relevant scans:

- `sast-jwt`
- `sast-missingauth`

### 2. `account`

Why second:

- account reads and writes are ownership-sensitive
- account-level IDOR is a classic banking risk

Primary questions:

- can one user read another account?
- is account state respected consistently?

Relevant scans:

- `sast-idor`
- `sast-businesslogic`

### 3. `transaction`

Why third:

- this is the financial core
- transaction creation, booking, and settlement are high-risk flows

Primary questions:

- can transaction states be skipped?
- can duplicate or unauthorized transaction actions occur?
- can business rules be bypassed under edge conditions?

Relevant scans:

- `sast-businesslogic`
- `sast-idor`
- `sast-missingauth`

### 4. `fraud`

Why fourth:

- fraud cases carry operational and risk significance
- false access or workflow gaps here are serious

Primary questions:

- who can read fraud cases?
- who can change fraud-related flows?
- can fraud signals be bypassed or duplicated incorrectly?

Relevant scans:

- `sast-idor`
- `sast-businesslogic`

### 5. `infra/outbox` and `infra/kafka`

Why fifth:

- event publication and consumption are security-relevant for integrity
- future outbound integrations can raise SSRF-like or trust-boundary risks

Primary questions:

- can outbound targets be abused?
- is event handling trust too broad?
- can duplicate events corrupt downstream behavior?

Relevant scans:

- `sast-ssrf`
- `sast-businesslogic`

## Not First-Priority For Manual Pass 1

- `common`
- observability configuration
- Docker / deployment details

These still matter, but they are lower-value than auth, authorization, transaction flow, and fraud workflow for the first pass.

## Manual Review Questions

For every finding, ask:

1. is this exploit actually reachable?
2. does it cross a trust boundary?
3. does it expose financial or operational data?
4. can it alter money movement or workflow control?
5. is this a real issue or a false positive?

## Output Location

Store the first manual run under:

```text
security-reports/ai-sast/2026-04-04/
```

Recommended files:

- `architecture.md`
- `jwt-results.md`
- `missingauth-results.md`
- `idor-results.md`
- `businesslogic-results.md`
- `ssrf-results.md`
- `final-report.md`
