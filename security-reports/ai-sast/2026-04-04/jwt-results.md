# AI-SAST Results: JWT / Identity

Date: 2026-04-04
Status: Findings present

## Finding JWT-01

- Severity: Medium
- Confidence: High
- Class: Insecure JWT / secret management

### Summary

The application signs JWTs with a static symmetric secret stored in versioned configuration and uses hardcoded demo identities with embedded passwords. If the same pattern were carried into shared or production-like environments, anyone with repository or artifact access could mint valid tokens or reuse known credentials.

### Evidence

- `app.security.jwt.secret` is stored directly in config: [application.yml](/Users/yvz.o/Desktop/projects/recon-control/src/main/resources/application.yml#L35)
- The signing key is built directly from that configured secret: [SecurityConfig.java](/Users/yvz.o/Desktop/projects/recon-control/src/main/java/com/yavuzozmen/reconcontrol/infra/SecurityConfig.java#L114)
- Demo credentials are embedded in code: [DemoIdentityService.java](/Users/yvz.o/Desktop/projects/recon-control/src/main/java/com/yavuzozmen/reconcontrol/infra/security/DemoIdentityService.java#L18)
- Tokens only rely on static HMAC key material and role claims: [JwtTokenService.java](/Users/yvz.o/Desktop/projects/recon-control/src/main/java/com/yavuzozmen/reconcontrol/infra/security/JwtTokenService.java#L24)

### Impact

- Secret leakage enables token forgery.
- Credential reuse risk increases if demo accounts bleed into shared environments.
- Key rotation and separation-of-duty are weak with a single static shared secret.

### Recommendation

1. Move JWT key material to environment-specific secret storage.
2. Replace static demo identities with environment-gated seed data or external identity integration.
3. Prefer asymmetric signing for non-local environments so verification and signing keys can be separated.
4. Introduce explicit local-dev profiles so demo credentials cannot be enabled accidentally in higher environments.

### Human Triage Note

This is acceptable for local learning environments, but should be treated as a release blocker before any real shared environment.
