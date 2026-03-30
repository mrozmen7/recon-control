package com.yavuzozmen.reconcontrol.transaction.adapter.out.persistence;

import com.yavuzozmen.reconcontrol.common.domain.CurrencyCode;
import com.yavuzozmen.reconcontrol.transaction.domain.TransactionStatus;
import com.yavuzozmen.reconcontrol.transaction.domain.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "internal_transactions")
public class InternalTransactionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "reference_no", nullable = false, unique = true, length = 64)
    private String referenceNo;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 16)
    private TransactionType type;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code", nullable = false, length = 3)
    private CurrencyCode currency;

    @Column(name = "value_date", nullable = false)
    private LocalDate valueDate;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private TransactionStatus status;

    protected InternalTransactionJpaEntity() {
    }

    InternalTransactionJpaEntity(
        UUID id,
        String referenceNo,
        UUID accountId,
        TransactionType type,
        BigDecimal amount,
        CurrencyCode currency,
        LocalDate valueDate,
        OffsetDateTime createdAt,
        TransactionStatus status
    ) {
        this.id = id;
        this.referenceNo = referenceNo;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.valueDate = valueDate;
        this.createdAt = createdAt;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public TransactionStatus getStatus() {
        return status;
    }
}
