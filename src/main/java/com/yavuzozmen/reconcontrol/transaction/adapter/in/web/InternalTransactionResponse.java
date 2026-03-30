package com.yavuzozmen.reconcontrol.transaction.adapter.in.web;

import com.yavuzozmen.reconcontrol.common.domain.CurrencyCode;
import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;
import com.yavuzozmen.reconcontrol.transaction.domain.TransactionStatus;
import com.yavuzozmen.reconcontrol.transaction.domain.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record InternalTransactionResponse(
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

    public static InternalTransactionResponse fromDomain(InternalTransaction transaction) {
        return new InternalTransactionResponse(
            transaction.id(),
            transaction.referenceNo(),
            transaction.accountId(),
            transaction.type(),
            transaction.amount().amount(),
            transaction.amount().currency(),
            transaction.valueDate(),
            transaction.createdAt(),
            transaction.status()
        );
    }
}
