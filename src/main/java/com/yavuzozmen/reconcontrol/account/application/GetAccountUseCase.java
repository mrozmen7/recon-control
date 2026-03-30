package com.yavuzozmen.reconcontrol.account.application;

import com.yavuzozmen.reconcontrol.account.application.port.out.AccountRepository;
import com.yavuzozmen.reconcontrol.account.domain.Account;
import java.util.Objects;
import java.util.UUID;

public class GetAccountUseCase {

    private final AccountRepository accountRepository;

    public GetAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = Objects.requireNonNull(
            accountRepository,
            "accountRepository must not be null"
        );
    }

    public Account handle(UUID accountId) {
        Objects.requireNonNull(accountId, "accountId must not be null");

        return accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException(accountId));
    }
}
