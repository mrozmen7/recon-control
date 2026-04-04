# Security Triage Policy

## Purpose

AI-SAST helps us generate candidate findings quickly. This policy defines how those findings become validated issues.

## Severity Model

- `Critical`: immediate exposure of sensitive data, auth bypass, remote compromise, or financial workflow compromise
- `High`: realistic privilege, identity, or transaction control weakness
- `Medium`: meaningful security weakness with narrower exploitability
- `Low`: hygiene or defense-in-depth improvement
- `Informational`: architecture or hardening recommendation

## Triage Rules

### Critical / High

- human review is mandatory before merge
- create a remediation ticket if confirmed
- do not close as false positive without short written justification

### Medium

- human review required before release
- may merge temporarily only with explicit acceptance and follow-up issue

### Low / Informational

- may be batched into hardening work
- still recorded for auditability

## Finding Lifecycle

```text
AI finding
-> triage
-> confirmed or false positive
-> remediation ticket
-> fix
-> re-scan
-> close with evidence
```

## Evidence Requirements

Each confirmed finding should include:

- affected file or module
- vulnerable flow summary
- exploitability rationale
- remediation recommendation
- retest note after fix

## Special Rule For This Project

`Business logic` findings must not be downgraded casually. In banking-style systems, business-rule bypasses can be more dangerous than low-level code issues.
