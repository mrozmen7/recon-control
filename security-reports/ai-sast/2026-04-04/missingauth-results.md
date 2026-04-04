# AI-SAST Results: Missing Auth / Overexposure

Date: 2026-04-04
Status: Findings present

## Finding AUTH-01

- Severity: Medium
- Confidence: High
- Class: Missing authentication on operational telemetry

### Summary

Operational telemetry endpoints are explicitly exposed without authentication on the main application port. This includes both Prometheus-formatted metrics and detailed metric enumeration. In a bank-grade environment, this discloses internal URI structure, traffic shape, and runtime behavior to any caller that can reach the app port.

### Evidence

- Security permits unauthenticated access to `/actuator/prometheus` and `/actuator/metrics/**`: [SecurityConfig.java](/Users/yvz.o/Desktop/projects/recon-control/src/main/java/com/yavuzozmen/reconcontrol/infra/SecurityConfig.java#L63)
- Management exposure includes `metrics` and `prometheus`: [application.yml](/Users/yvz.o/Desktop/projects/recon-control/src/main/resources/application.yml#L85)
- Prometheus endpoint access is marked `unrestricted`: [application.yml](/Users/yvz.o/Desktop/projects/recon-control/src/main/resources/application.yml#L85)

### Impact

- Reveals sensitive runtime internals and endpoint inventory.
- Makes traffic fingerprinting easier for an attacker.
- Increases intelligence available for follow-on abuse.

### Recommendation

1. Restrict Prometheus scraping to internal network paths only.
2. Remove unauthenticated `/actuator/metrics/**` exposure from the public app port.
3. Consider separate management port or sidecar-only exposure.
4. Gate detailed metrics behind strong auth or network policy in non-local environments.

### Human Triage Note

This is low friction for local observability, but should be considered a hardening requirement before deployment beyond a developer machine.
