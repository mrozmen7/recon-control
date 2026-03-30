package com.yavuzozmen.reconcontrol.account.adapter.in.web;

import com.yavuzozmen.reconcontrol.account.domain.Account;
import com.yavuzozmen.reconcontrol.account.domain.AccountStatus;
import com.yavuzozmen.reconcontrol.common.domain.CurrencyCode;
import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
    UUID id,
    String accountNumber,
    String customerId,
    CurrencyCode currency,
    BigDecimal balance,
    AccountStatus status
) {

    public static AccountResponse fromDomain(Account account) {
        return new AccountResponse(
            account.id(),
            account.accountNumber(),
            account.customerId(),
            account.currency(),
            account.balance().amount(),
            account.status()
        );
    }
}
