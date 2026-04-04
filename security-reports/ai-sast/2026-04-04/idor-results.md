# AI-SAST Results: Resource-Level Authorization / IDOR-Like Access

Date: 2026-04-04
Status: Findings present

## Finding IDOR-01

- Severity: High
- Confidence: Medium-High
- Class: Missing resource-level authorization / broad entitlement gap

### Summary

Read access to accounts, transactions, and fraud cases is enforced only at the coarse role level. Once a caller has one of the allowed internal roles, the application accepts arbitrary UUIDs and returns the requested object without checking desk, branch, ownership, customer scope, or case assignment. In a banking context this is an entitlement gap, even if the caller is an internal user.

### Evidence

- Broad read routes are granted to internal roles in security config: [SecurityConfig.java](/Users/yvz.o/Desktop/projects/recon-control/src/main/java/com/yavuzozmen/reconcontrol/infra/SecurityConfig.java#L75)
- Account lookup uses raw `accountId` only: [AccountController.java](/Users/yvz.o/Desktop/projects/recon-control/src/main/java/com/yavuzozmen/reconcontrol/account/adapter/in/web/AccountController.java#L77), [GetAccountUseCase.java](/Users/yvz.o/Desktop/projects/recon-control/src/main/java/com/yavuzozmen/reconcontrol/account/application/GetAccountUseCase.java#L19)
- Transaction lookup/listing use raw ids and unrestricted repository reads: [TransactionController.java](/Users/yvz.o/Desktop/projects/recon-control/src/main/java/com/yavuzozmen/reconcontrol/transaction/adapter/in/web/TransactionController.java#L126), [ListInternalTransactionsUseCase.java](/Users/yvz.o/Desktop/projects/recon-control/src/main/java/com/yavuzozmen/reconcontrol/transaction/application/ListInternalTransactionsUseCase.java#L20), [GetInternalTransactionUseCase.java](/Users/yvz.o/Desktop/projects/recon-control/src/main/java/com/yavuzozmen/reconcontrol/transaction/application/GetInternalTransactionUseCase.java#L19)
- Fraud case reads return all cases or any case by id for allowed roles: [FraudCaseController.java](/Users/yvz.o/Desktop/projects/recon-control/src/main/java/com/yavuzozmen/reconcontrol/fraud/adapter/in/web/FraudCaseController.java#L43), [ListFraudCasesUseCase.java](/Users/yvz.o/Desktop/projects/recon-control/src/main/java/com/yavuzozmen/reconcontrol/fraud/application/ListFraudCasesUseCase.java#L19), [GetFraudCaseUseCase.java](/Users/yvz.o/Desktop/projects/recon-control/src/main/java/com/yavuzozmen/reconcontrol/fraud/application/GetFraudCaseUseCase.java#L18)

### Impact

- Any internal user with a broad read role can enumerate or fetch unrelated customer records.
- Auditor visibility is not scoped to case assignment or purpose limitation.
- Violates least-privilege expectations for a bank-grade internal operations platform.

### Recommendation

1. Add entitlement context to JWT/user identity: desk, branch, region, case-assignment, or customer-scope.
2. Enforce resource-level authorization in use cases, not only route matchers.
3. Default list endpoints to scope-bound results, not global `findAll()`.
4. Add audit reason / purpose-of-access controls for sensitive read endpoints.

### Human Triage Note

This is not a classic public-customer IDOR. It is an internal entitlement design gap. In banking environments, that distinction matters operationally but not from a least-privilege perspective.
