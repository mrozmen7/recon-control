package com.yavuzozmen.reconcontrol.transaction.application;

import com.yavuzozmen.reconcontrol.common.domain.Money;
import com.yavuzozmen.reconcontrol.transaction.domain.TransactionType;
import java.time.LocalDate;
import java.util.UUID;

public record CreateInternalTransactionCommand(
    String referenceNo,
    UUID accountId,
    TransactionType type,
    Money amount,
    LocalDate valueDate,
    String idempotencyKey
) {
}
