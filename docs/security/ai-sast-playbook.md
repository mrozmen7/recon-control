# AI-SAST Playbook

## Problem

Banking-style backends accumulate security risk as they grow:

- authorization gaps can appear between endpoints and domain rules
- JWT or role configuration mistakes can expose sensitive operations
- business logic flaws can bypass financial controls even when the code compiles and tests pass
- manual review alone becomes slow and inconsistent

The goal of AI-SAST in this project is to accelerate secure code review without replacing human judgment.

## Solution

Use an isolated AI-assisted SAST workflow that:

1. maps the architecture and trust boundaries first
2. runs vulnerability-class-specific analysis against the codebase
3. produces auditable reports in a versioned location
4. requires human triage before any finding becomes a real issue

This workflow is advisory by default. It helps engineers think like AppSec reviewers, but it does not replace AppSec, pentesting, or compliance controls.

## Architecture

```text
Developer branch
-> AI-SAST orchestration
-> architecture and threat analysis
-> mandatory scans
-> optional scans
-> final consolidated report
-> human security review
-> remediation ticket or merge decision
```

## Repository Structure

```text
docs/security/
  ai-sast-playbook.md
  threat-model.md
  triage-policy.md

security/ai-sast/
  AGENTS.md
  policies/scan-policy.md
  templates/finding-template.md

security-reports/ai-sast/
  README.md
  <date>/
    architecture.md
    *-results.md
    final-report.md
```

## Terms

- `SAST`: static application security testing; analyzes source code without executing the app
- `False positive`: tool says "vulnerability" but the issue is not real
- `False negative`: real vulnerability exists but the tool misses it
- `Threat model`: map of assets, trust boundaries, entry points, and abuse paths
- `IDOR`: insecure direct object reference; unauthorized access to someone else's record
- `Business logic flaw`: workflow or financial-control weakness rather than a low-level coding bug
- `Advisory finding`: candidate issue that still needs human review
- `Gating`: rule that blocks merge or release if a scan fails policy

## Policies

- Root project instructions stay in [`CLAUDE.md`](/Users/yvz.o/Desktop/projects/recon-control/CLAUDE.md); AI-SAST orchestration lives under [`security/ai-sast/AGENTS.md`](/Users/yvz.o/Desktop/projects/recon-control/security/ai-sast/AGENTS.md)
- AI findings are advisory until a human reviewer validates them
- Reports must be stored under a dated folder in [`security-reports/ai-sast`](/Users/yvz.o/Desktop/projects/recon-control/security-reports/ai-sast)
- High or critical findings must be triaged before merge
- Business logic findings are first-class citizens because this project models banking flows

## Mandatory Scans

- `sast-analysis`
- `sast-jwt`
- `sast-missingauth`
- `sast-idor`
- `sast-businesslogic`
- `sast-ssrf`
- `sast-report`

## Optional Scans

- `sast-sqli`
- `sast-xss`
- `sast-rce`
- `sast-pathtraversal`
- `sast-xxe`
- `sast-ssti`

## Disabled For Now

- `sast-graphql`
- `sast-fileupload`

These are currently not applicable because the project does not expose GraphQL or file upload flows.

## Why This Fits A Banking Project

This project contains:

- JWT-based security
- role-based access
- account and transaction ownership concerns
- settlement workflow state transitions
- fraud and operational case management

That makes authorization and business-logic review especially valuable. AI-SAST is most useful here as a secure review accelerator, not as a final authority.
