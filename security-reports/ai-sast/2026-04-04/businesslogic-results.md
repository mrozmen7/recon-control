# AI-SAST Results: Business Logic

Date: 2026-04-04
Status: Findings present

## Finding BL-01

- Severity: High
- Confidence: High
- Class: Unrestricted balance-affecting operation

### Summary

Any `OPS_USER` can create a `CREDIT` or `DEBIT` transaction against any active account they can reference. The transaction flow validates account status and currency, but it does not enforce transaction amount limits, dual control, account entitlement, approval workflow, or reference uniqueness. In a bank-like system, this leaves the most sensitive operation guarded only by a coarse role check.

### Evidence

- Route access allows `OPS_USER` and `OPS_ADMIN` to create transactions: [SecurityConfig.java](/Users/yvz.o/Desktop/projects/recon-control/src/main/java/com/yavuzozmen/reconcontrol/infra/SecurityConfig.java#L83)
- Controller accepts arbitrary `accountId`, `type`, `amount`, and `referenceNo`: [TransactionController.java](/Users/yvz.o/Desktop/projects/recon-control/src/main/java/com/yavuzozmen/reconcontrol/transaction/adapter/in/web/TransactionController.java#L94)
- Use case directly applies deposit/withdraw balance effects after only active/currency checks: [CreateInternalTransactionUseCase.java](/Users/yvz.o/Desktop/projects/recon-control/src/main/java/com/yavuzozmen/reconcontrol/transaction/application/CreateInternalTransactionUseCase.java#L137)

### Impact

- A broad internal role can post arbitrary value movements if it knows a valid account UUID.
- No maker-checker or approval boundary exists for sensitive balance changes.
- Fraud detection happens after the posting event, so it does not prevent the movement itself.

### Recommendation

1. Separate initiator and approver roles for balance-changing actions.
2. Add transaction-type and amount-based limits per role.
3. Add account entitlement checks before applying balance effects.
4. Enforce stronger invariants on `referenceNo` and operational workflow state.
5. Consider a pending-approval state before booked balance mutation for sensitive operations.

### Human Triage Note

This may be acceptable for a training demo, but it is the biggest gap if the system is presented as bank-grade operations software.
