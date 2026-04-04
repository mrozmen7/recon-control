package com.yavuzozmen.reconcontrol.fraud.domain;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public final class FraudCase {

    private final UUID id;
    private final UUID sourceEventId;
    private final UUID transactionId;
    private final UUID accountId;
    private final String referenceNo;
    private final String ruleCode;
    private final FraudSeverity severity;
    private final String reason;
    private final OffsetDateTime createdAt;
    private FraudCaseStatus status;
    private OffsetDateTime reviewedAt;

    private FraudCase(
        UUID id,
        UUID sourceEventId,
        UUID transactionId,
        UUID accountId,
        String referenceNo,
        String ruleCode,
        FraudSeverity severity,
        String reason,
        OffsetDateTime createdAt,
        FraudCaseStatus status,
        OffsetDateTime reviewedAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.sourceEventId = Objects.requireNonNull(sourceEventId, "sourceEventId must not be null");
        this.transactionId = Objects.requireNonNull(transactionId, "transactionId must not be null");
        this.accountId = Objects.requireNonNull(accountId, "accountId must not be null");
        this.referenceNo = validateText(referenceNo, "referenceNo");
        this.ruleCode = validateText(ruleCode, "ruleCode");
        this.severity = Objects.requireNonNull(severity, "severity must not be null");
        this.reason = validateText(reason, "reason");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.reviewedAt = reviewedAt;
    }

    public static FraudCase open(
        UUID sourceEventId,
        UUID transactionId,
        UUID accountId,
        String referenceNo,
        String ruleCode,
        FraudSeverity severity,
        String reason
    ) {
        return new FraudCase(
            UUID.randomUUID(),
            sourceEventId,
            transactionId,
            accountId,
            referenceNo,
            ruleCode,
            severity,
            reason,
            OffsetDateTime.now(),
            FraudCaseStatus.OPEN,
            null
        );
    }

    public static FraudCase rehydrate(
        UUID id,
        UUID sourceEventId,
        UUID transactionId,
        UUID accountId,
        String referenceNo,
        String ruleCode,
        FraudSeverity severity,
        String reason,
        OffsetDateTime createdAt,
        FraudCaseStatus status,
        OffsetDateTime reviewedAt
    ) {
        return new FraudCase(
            id,
            sourceEventId,
            transactionId,
            accountId,
            referenceNo,
            ruleCode,
            severity,
            reason,
            createdAt,
            status,
            reviewedAt
        );
    }

    public UUID id() {
        return id;
    }

    public UUID sourceEventId() {
        return sourceEventId;
    }

    public UUID transactionId() {
        return transactionId;
    }

    public UUID accountId() {
        return accountId;
    }

    public String referenceNo() {
        return referenceNo;
    }

    public String ruleCode() {
        return ruleCode;
    }

    public FraudSeverity severity() {
        return severity;
    }

    public String reason() {
        return reason;
    }

    public OffsetDateTime createdAt() {
        return createdAt;
    }

    public FraudCaseStatus status() {
        return status;
    }

    public OffsetDateTime reviewedAt() {
        return reviewedAt;
    }

    public void review() {
        if (status == FraudCaseStatus.REVIEWED) {
            throw new IllegalStateException("fraud case already reviewed");
        }
        status = FraudCaseStatus.REVIEWED;
        reviewedAt = OffsetDateTime.now();
    }

    private static String validateText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return trimmed;
    }
}
