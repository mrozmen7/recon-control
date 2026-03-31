package com.yavuzozmen.reconcontrol.transaction.application;

import com.yavuzozmen.reconcontrol.account.application.AccountNotFoundException;
import com.yavuzozmen.reconcontrol.account.application.port.out.AccountRepository;
import com.yavuzozmen.reconcontrol.account.domain.Account;
import com.yavuzozmen.reconcontrol.common.domain.Money;
import com.yavuzozmen.reconcontrol.transaction.application.port.out.TransactionIdempotencyStore;
import com.yavuzozmen.reconcontrol.transaction.application.port.out.InternalTransactionRepository;
import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;
import com.yavuzozmen.reconcontrol.transaction.domain.TransactionType;
import io.github.resilience4j.retry.annotation.Retry;
import java.time.Duration;
import java.util.Objects;
import org.springframework.transaction.annotation.Transactional;

public class CreateInternalTransactionUseCase {

    private final InternalTransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionIdempotencyStore idempotencyStore;
    private final Duration idempotencyTtl;

    public CreateInternalTransactionUseCase(
        InternalTransactionRepository transactionRepository,
        AccountRepository accountRepository,
        TransactionIdempotencyStore idempotencyStore,
        Duration idempotencyTtl
    ) {
        this.transactionRepository = Objects.requireNonNull(
            transactionRepository,
            "transactionRepository must not be null"
        );
        this.accountRepository = Objects.requireNonNull(
            accountRepository,
            "accountRepository must not be null"
        );
        this.idempotencyStore = Objects.requireNonNull(
            idempotencyStore,
            "idempotencyStore must not be null"
        );
        this.idempotencyTtl = Objects.requireNonNull(
            idempotencyTtl,
            "idempotencyTtl must not be null"
        );
    }

    @Transactional
    @Retry(name = "transactionCreation")
    public TransactionCreationResult handle(CreateInternalTransactionCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        String idempotencyKey = normalizeIdempotencyKey(command.idempotencyKey());
        if (idempotencyKey != null) {
            return handleWithIdempotency(command, idempotencyKey);
        }

        return createTransaction(command);
    }

    private TransactionCreationResult handleWithIdempotency(
        CreateInternalTransactionCommand command,
        String idempotencyKey
    ) {
        return idempotencyStore.find(idempotencyKey)
            .map(record -> resolveExistingIdempotentRequest(record, idempotencyKey))
            .orElseGet(() -> processNewIdempotentRequest(command, idempotencyKey));
    }

    private TransactionCreationResult processNewIdempotentRequest(
        CreateInternalTransactionCommand command,
        String idempotencyKey
    ) {
        boolean claimed = idempotencyStore.markProcessing(idempotencyKey, idempotencyTtl);
        if (!claimed) {
            return idempotencyStore.find(idempotencyKey)
                .map(record -> resolveExistingIdempotentRequest(record, idempotencyKey))
                .orElseThrow(() -> new IdempotencyRequestInProgressException(idempotencyKey));
        }

        try {
            TransactionCreationResult result = createTransaction(command);
            idempotencyStore.markCompleted(
                idempotencyKey,
                result.transaction().id(),
                idempotencyTtl
            );
            return result;
        } catch (RuntimeException exception) {
            idempotencyStore.clear(idempotencyKey);
            throw exception;
        }
    }

    private TransactionCreationResult resolveExistingIdempotentRequest(
        IdempotencyRecord record,
        String idempotencyKey
    ) {
        if (record.status() == IdempotencyStatus.PROCESSING) {
            throw new IdempotencyRequestInProgressException(idempotencyKey);
        }

        if (record.transactionId() == null) {
            throw new IllegalStateException(
                "completed idempotency record must reference a transaction"
            );
        }

        InternalTransaction transaction = transactionRepository.findById(record.transactionId())
            .orElseThrow(() -> new IllegalStateException(
                "idempotency record points to missing transaction: " + record.transactionId()
            ));
        return TransactionCreationResult.replayed(transaction);
    }

    private TransactionCreationResult createTransaction(CreateInternalTransactionCommand command) {
        Account account = accountRepository.findById(command.accountId())
            .orElseThrow(() -> new AccountNotFoundException(command.accountId()));

        if (!account.isActive()) {
            throw new IllegalStateException("account must be active for transaction creation");
        }

        if (account.currency() != command.amount().currency()) {
            throw new IllegalArgumentException("transaction currency must match account currency");
        }

        applyBalanceEffect(account, command.type(), command.amount());
        accountRepository.save(account);

        InternalTransaction transaction = InternalTransaction.create(
            command.referenceNo(),
            command.accountId(),
            command.type(),
            command.amount(),
            command.valueDate()
        );
        transaction.markBooked();

        return TransactionCreationResult.created(transactionRepository.save(transaction));
    }

    private void applyBalanceEffect(Account account, TransactionType type, Money amount) {
        if (type == TransactionType.DEBIT) {
            account.withdraw(amount);
            return;
        }

        account.deposit(amount);
    }

    private String normalizeIdempotencyKey(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
