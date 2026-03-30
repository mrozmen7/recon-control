package com.yavuzozmen.reconcontrol.transaction.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yavuzozmen.reconcontrol.account.application.AccountNotFoundException;
import com.yavuzozmen.reconcontrol.account.application.port.out.AccountRepository;
import com.yavuzozmen.reconcontrol.account.domain.Account;
import com.yavuzozmen.reconcontrol.common.domain.CurrencyCode;
import com.yavuzozmen.reconcontrol.common.domain.Money;
import com.yavuzozmen.reconcontrol.transaction.application.port.out.InternalTransactionRepository;
import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;
import com.yavuzozmen.reconcontrol.transaction.domain.TransactionType;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateInternalTransactionUseCaseTest {

    @Test
    @DisplayName("should create internal transaction for active account")
    void shouldCreateInternalTransactionForActiveAccount() {
        InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();
        InMemoryTransactionRepository transactionRepository = new InMemoryTransactionRepository();

        Account account = Account.open("CH-001", "customer-1", CurrencyCode.CHF);
        accountRepository.save(account);

        CreateInternalTransactionUseCase useCase = new CreateInternalTransactionUseCase(
            transactionRepository,
            accountRepository
        );

        InternalTransaction transaction = useCase.handle(new CreateInternalTransactionCommand(
            "TX-001",
            account.id(),
            TransactionType.DEBIT,
            Money.of("50.0000", CurrencyCode.CHF),
            LocalDate.now()
        ));

        assertThat(transaction.referenceNo()).isEqualTo("TX-001");
        assertThat(transaction.accountId()).isEqualTo(account.id());
        assertThat(transactionRepository.savedTransaction).isEqualTo(transaction);
    }

    @Test
    @DisplayName("should reject transaction when account is not active")
    void shouldRejectTransactionWhenAccountIsNotActive() {
        InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();
        InMemoryTransactionRepository transactionRepository = new InMemoryTransactionRepository();

        Account account = Account.open("CH-001", "customer-1", CurrencyCode.CHF);
        account.freeze();
        accountRepository.save(account);

        CreateInternalTransactionUseCase useCase = new CreateInternalTransactionUseCase(
            transactionRepository,
            accountRepository
        );

        assertThatThrownBy(() -> useCase.handle(new CreateInternalTransactionCommand(
            "TX-001",
            account.id(),
            TransactionType.DEBIT,
            Money.of("50.0000", CurrencyCode.CHF),
            LocalDate.now()
        )))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("account must be active for transaction creation");
    }

    @Test
    @DisplayName("should reject transaction when account is missing")
    void shouldRejectTransactionWhenAccountIsMissing() {
        UUID missingAccountId = UUID.randomUUID();
        CreateInternalTransactionUseCase useCase = new CreateInternalTransactionUseCase(
            new InMemoryTransactionRepository(),
            new InMemoryAccountRepository()
        );

        assertThatThrownBy(() -> useCase.handle(new CreateInternalTransactionCommand(
            "TX-001",
            missingAccountId,
            TransactionType.DEBIT,
            Money.of("50.0000", CurrencyCode.CHF),
            LocalDate.now()
        )))
            .isInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining(missingAccountId.toString());
    }

    @Test
    @DisplayName("should reject transaction when account currency does not match")
    void shouldRejectTransactionWhenAccountCurrencyDoesNotMatch() {
        InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();
        InMemoryTransactionRepository transactionRepository = new InMemoryTransactionRepository();

        Account account = Account.open("CH-001", "customer-1", CurrencyCode.CHF);
        accountRepository.save(account);

        CreateInternalTransactionUseCase useCase = new CreateInternalTransactionUseCase(
            transactionRepository,
            accountRepository
        );

        assertThatThrownBy(() -> useCase.handle(new CreateInternalTransactionCommand(
            "TX-001",
            account.id(),
            TransactionType.DEBIT,
            Money.of("50.0000", CurrencyCode.USD),
            LocalDate.now()
        )))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("transaction currency must match account currency");
    }

    private static final class InMemoryAccountRepository implements AccountRepository {

        private final Map<UUID, Account> storage = new HashMap<>();

        @Override
        public Account save(Account account) {
            storage.put(account.id(), account);
            return account;
        }

        @Override
        public Optional<Account> findById(UUID accountId) {
            return Optional.ofNullable(storage.get(accountId));
        }
    }

    private static final class InMemoryTransactionRepository implements InternalTransactionRepository {

        private InternalTransaction savedTransaction;

        @Override
        public InternalTransaction save(InternalTransaction transaction) {
            this.savedTransaction = transaction;
            return transaction;
        }

        @Override
        public java.util.List<InternalTransaction> findAll() {
            return savedTransaction == null ? java.util.List.of() : java.util.List.of(savedTransaction);
        }

        @Override
        public java.util.List<InternalTransaction> findByAccountId(UUID accountId) {
            if (savedTransaction == null || !savedTransaction.accountId().equals(accountId)) {
                return java.util.List.of();
            }

            return java.util.List.of(savedTransaction);
        }
    }
}
