package com.yavuzozmen.reconcontrol.fraud.adapter.out.persistence;

import com.yavuzozmen.reconcontrol.fraud.domain.FraudCaseStatus;
import com.yavuzozmen.reconcontrol.fraud.domain.FraudSeverity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "fraud_cases")
public class FraudCaseJpaEntity {

    @Id
    private UUID id;

    @Column(name = "source_event_id", nullable = false)
    private UUID sourceEventId;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "reference_no", nullable = false)
    private String referenceNo;

    @Column(name = "rule_code", nullable = false)
    private String ruleCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FraudSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FraudCaseStatus status;

    @Column(nullable = false)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "reviewed_at")
    private OffsetDateTime reviewedAt;

    protected FraudCaseJpaEntity() {}

    FraudCaseJpaEntity(
        UUID id,
        UUID sourceEventId,
        UUID transactionId,
        UUID accountId,
        String referenceNo,
        String ruleCode,
        FraudSeverity severity,
        FraudCaseStatus status,
        String reason,
        OffsetDateTime createdAt,
        OffsetDateTime reviewedAt
    ) {
        this.id = id;
        this.sourceEventId = sourceEventId;
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.referenceNo = referenceNo;
        this.ruleCode = ruleCode;
        this.severity = severity;
        this.status = status;
        this.reason = reason;
        this.createdAt = createdAt;
        this.reviewedAt = reviewedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSourceEventId() {
        return sourceEventId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public FraudSeverity getSeverity() {
        return severity;
    }

    public FraudCaseStatus getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getReviewedAt() {
        return reviewedAt;
    }
}
