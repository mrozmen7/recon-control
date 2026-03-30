package com.yavuzozmen.reconcontrol.transaction.application;

import com.yavuzozmen.reconcontrol.account.application.AccountNotFoundException;
import com.yavuzozmen.reconcontrol.account.application.port.out.AccountRepository;
import com.yavuzozmen.reconcontrol.account.domain.Account;
import com.yavuzozmen.reconcontrol.transaction.application.port.out.InternalTransactionRepository;
import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;
import java.util.Objects;

public class CreateInternalTransactionUseCase {

    private final InternalTransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public CreateInternalTransactionUseCase(
        InternalTransactionRepository transactionRepository,
        AccountRepository accountRepository
    ) {
        this.transactionRepository = Objects.requireNonNull(
            transactionRepository,
            "transactionRepository must not be null"
        );
        this.accountRepository = Objects.requireNonNull(
            accountRepository,
            "accountRepository must not be null"
        );
    }

    public InternalTransaction handle(CreateInternalTransactionCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        Account account = accountRepository.findById(command.accountId())
            .orElseThrow(() -> new AccountNotFoundException(command.accountId()));

        if (!account.isActive()) {
            throw new IllegalStateException("account must be active for transaction creation");
        }

        if (account.currency() != command.amount().currency()) {
            throw new IllegalArgumentException("transaction currency must match account currency");
        }

        InternalTransaction transaction = InternalTransaction.create(
            command.referenceNo(),
            command.accountId(),
            command.type(),
            command.amount(),
            command.valueDate()
        );

        return transactionRepository.save(transaction);
    }
}
