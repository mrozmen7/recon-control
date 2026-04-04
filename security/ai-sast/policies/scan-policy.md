# AI-SAST Scan Policy

## Mandatory

- `sast-analysis`
- `sast-jwt`
- `sast-missingauth`
- `sast-idor`
- `sast-businesslogic`
- `sast-ssrf`
- `sast-report`

## Optional

- `sast-sqli`
- `sast-xss`
- `sast-rce`
- `sast-pathtraversal`
- `sast-xxe`
- `sast-ssti`

## Disabled

- `sast-graphql`
- `sast-fileupload`

## Review Priority

1. authorization and access control
2. transaction and settlement workflow logic
3. JWT and identity handling
4. outbound request surfaces
5. secondary hardening issues
