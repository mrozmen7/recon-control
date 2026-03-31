package com.yavuzozmen.reconcontrol.infra.kafka.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransactionEventPayload(
    UUID eventId,
    TransactionEventType eventType,
    UUID transactionId,
    UUID accountId,
    String referenceNo,
    String transactionType,
    String status,
    BigDecimal amount,
    String currency,
    @JsonFormat(pattern = "yyyy-MM-dd") LocalDate valueDate,
    OffsetDateTime occurredAt
) {
    public static TransactionEventPayload fromDomain(
        TransactionEventType eventType,
        InternalTransaction transaction
    ) {
        return new TransactionEventPayload(
            UUID.randomUUID(),
            eventType,
            transaction.id(),
            transaction.accountId(),
            transaction.referenceNo(),
            transaction.type().name(),
            transaction.status().name(),
            transaction.amount().amount(),
            transaction.amount().currency().name(),
            transaction.valueDate(),
            OffsetDateTime.now()
        );
    }
}
