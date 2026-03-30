package com.yavuzozmen.reconcontrol.account.application;

import com.yavuzozmen.reconcontrol.common.domain.CurrencyCode;

public record OpenAccountCommand(
    String accountNumber,
    String customerId,
    CurrencyCode currency
) {
}
