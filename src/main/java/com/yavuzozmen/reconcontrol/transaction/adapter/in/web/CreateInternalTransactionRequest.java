package com.yavuzozmen.reconcontrol.transaction.adapter.in.web;

import com.yavuzozmen.reconcontrol.common.domain.CurrencyCode;
import com.yavuzozmen.reconcontrol.transaction.domain.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateInternalTransactionRequest(
    @NotBlank String referenceNo,
    @NotNull UUID accountId,
    @NotNull TransactionType type,
    @NotNull @DecimalMin(value = "0.0001") BigDecimal amount,
    @NotNull CurrencyCode currency,
    @NotNull LocalDate valueDate
) {
}
