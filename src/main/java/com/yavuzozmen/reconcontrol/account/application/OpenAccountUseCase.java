package com.yavuzozmen.reconcontrol.account.application;

import com.yavuzozmen.reconcontrol.account.application.port.out.AccountRepository;
import com.yavuzozmen.reconcontrol.account.domain.Account;
import java.util.Objects;

public class OpenAccountUseCase {

    private final AccountRepository accountRepository;

    public OpenAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = Objects.requireNonNull(
            accountRepository,
            "accountRepository must not be null"
        );
    }

    public Account handle(OpenAccountCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        Account account = Account.open(
            command.accountNumber(),
            command.customerId(),
            command.currency()
        );

        return accountRepository.save(account);
    }
}
