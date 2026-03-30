package com.yavuzozmen.reconcontrol.transaction.domain;

import com.yavuzozmen.reconcontrol.common.domain.Money;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Core internal transaction entity.
 *
 * <p>Represents a transaction known by the bank's own system before reconciliation.
 */
public final class InternalTransaction {

    private final UUID id;
    private final String referenceNo;
    private final UUID accountId;
    private final TransactionType type;
    private final Money amount;
    private final LocalDate valueDate;
    private final OffsetDateTime createdAt;
    private TransactionStatus status;

    private InternalTransaction(
        UUID id,
        String referenceNo,
        UUID accountId,
        TransactionType type,
        Money amount,
        LocalDate valueDate,
        OffsetDateTime createdAt,
        TransactionStatus status
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.referenceNo = validateText(referenceNo, "referenceNo");
        this.accountId = Objects.requireNonNull(accountId, "accountId must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.valueDate = Objects.requireNonNull(valueDate, "valueDate must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");

        if (!amount.isPositive()) {
            throw new IllegalArgumentException("transaction amount must be positive");
        }
    }

    public static InternalTransaction create(
        String referenceNo,
        UUID accountId,
        TransactionType type,
        Money amount,
        LocalDate valueDate
    ) {
        return new InternalTransaction(
            UUID.randomUUID(),
            referenceNo,
            accountId,
            type,
            amount,
            valueDate,
            OffsetDateTime.now(),
            TransactionStatus.RECEIVED
        );
    }

    public static InternalTransaction rehydrate(
        UUID id,
        String referenceNo,
        UUID accountId,
        TransactionType type,
        Money amount,
        LocalDate valueDate,
        OffsetDateTime createdAt,
        TransactionStatus status
    ) {
        return new InternalTransaction(
            id,
            referenceNo,
            accountId,
            type,
            amount,
            valueDate,
            createdAt,
            status
        );
    }

    public UUID id() {
        return id;
    }

    public String referenceNo() {
        return referenceNo;
    }

    public UUID accountId() {
        return accountId;
    }

    public TransactionType type() {
        return type;
    }

    public Money amount() {
        return amount;
    }

    public LocalDate valueDate() {
        return valueDate;
    }

    public OffsetDateTime createdAt() {
        return createdAt;
    }

    public TransactionStatus status() {
        return status;
    }

    public void markBooked() {
        ensureStatus(TransactionStatus.RECEIVED, "only received transaction can be booked");
        status = TransactionStatus.BOOKED;
    }

    public void reject() {
        ensureStatus(TransactionStatus.RECEIVED, "only received transaction can be rejected");
        status = TransactionStatus.REJECTED;
    }

    public void markSettlementPending() {
        ensureStatus(TransactionStatus.BOOKED, "only booked transaction can become settlement pending");
        status = TransactionStatus.SETTLEMENT_PENDING;
    }

    public void markSettled() {
        ensureStatus(
            TransactionStatus.SETTLEMENT_PENDING,
            "only settlement pending transaction can be settled"
        );
        status = TransactionStatus.SETTLED;
    }

    private void ensureStatus(TransactionStatus expected, String message) {
        if (status != expected) {
            throw new IllegalStateException(message);
        }
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
