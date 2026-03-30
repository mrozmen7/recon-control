package com.yavuzozmen.reconcontrol.account.adapter.in.web;

import com.yavuzozmen.reconcontrol.common.domain.CurrencyCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OpenAccountRequest(
    @NotBlank String accountNumber,
    @NotBlank String customerId,
    @NotNull CurrencyCode currency
) {
}
