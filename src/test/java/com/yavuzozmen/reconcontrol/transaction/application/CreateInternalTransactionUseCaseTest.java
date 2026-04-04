package com.yavuzozmen.reconcontrol.transaction.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yavuzozmen.reconcontrol.account.application.AccountNotFoundException;
import com.yavuzozmen.reconcontrol.account.application.port.out.AccountRepository;
import com.yavuzozmen.reconcontrol.account.domain.Account;
import com.yavuzozmen.reconcontrol.common.domain.CurrencyCode;
import com.yavuzozmen.reconcontrol.common.domain.Money;
import com.yavuzozmen.reconcontrol.transaction.application.port.out.InternalTransactionRepository;
import com.yavuzozmen.reconcontrol.transaction.application.port.out.TransactionIdempotencyStore;
import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;
import com.yavuzozmen.reconcontrol.transaction.domain.TransactionStatus;
import com.yavuzozmen.reconcontrol.transaction.domain.TransactionType;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateInternalTransactionUseCaseTest {

    @Test
    @DisplayName("should create booked credit transaction and update balance")
    void shouldCreateBookedCreditTransactionAndUpdateBalance() {
        InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();
        InMemoryTransactionRepository transactionRepository = new InMemoryTransactionRepository();

        Account account = Account.open("CH-001", "customer-1", CurrencyCode.CHF);
        accountRepository.save(account);

        CreateInternalTransactionUseCase useCase = new CreateInternalTransactionUseCase(
            transactionRepository,
            accountRepository,
            new InMemoryTransactionIdempotencyStore(),
            Duration.ofHours(24)
        );

        TransactionCreationResult result = useCase.handle(new CreateInternalTransactionCommand(
            "TX-001",
            account.id(),
            TransactionType.CREDIT,
            Money.of("50.0000", CurrencyCode.CHF),
            LocalDate.now(),
            null
        ));

        assertThat(result.replayed()).isFalse();
        assertThat(result.transaction().referenceNo()).isEqualTo("TX-001");
        assertThat(result.transaction().status()).isEqualTo(TransactionStatus.BOOKED);
        assertThat(accountRepository.findById(account.id()).orElseThrow().balance().amount())
            .isEqualByComparingTo("50.0000");
        assertThat(transactionRepository.savedTransaction).isEqualTo(result.transaction());
    }

    @Test
    @DisplayName("should reject debit transaction when insufficient funds")
    void shouldRejectDebitTransactionWhenInsufficientFunds() {
        InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();
        InMemoryTransactionRepository transactionRepository = new InMemoryTransactionRepository();

        Account account = Account.open("CH-001", "customer-1", CurrencyCode.CHF);
        accountRepository.save(account);

        CreateInternalTransactionUseCase useCase = new CreateInternalTransactionUseCase(
            transactionRepository,
            accountRepository,
            new InMemoryTransactionIdempotencyStore(),
            Duration.ofHours(24)
        );

        assertThatThrownBy(() -> useCase.handle(new CreateInternalTransactionCommand(
            "TX-001",
            account.id(),
            TransactionType.DEBIT,
            Money.of("50.0000", CurrencyCode.CHF),
            LocalDate.now(),
            null
        )))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("insufficient funds");
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
            accountRepository,
            new InMemoryTransactionIdempotencyStore(),
            Duration.ofHours(24)
        );

        assertThatThrownBy(() -> useCase.handle(new CreateInternalTransactionCommand(
            "TX-001",
            account.id(),
            TransactionType.CREDIT,
            Money.of("50.0000", CurrencyCode.CHF),
            LocalDate.now(),
            null
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
            new InMemoryAccountRepository(),
            new InMemoryTransactionIdempotencyStore(),
            Duration.ofHours(24)
        );

        assertThatThrownBy(() -> useCase.handle(new CreateInternalTransactionCommand(
            "TX-001",
            missingAccountId,
            TransactionType.CREDIT,
            Money.of("50.0000", CurrencyCode.CHF),
            LocalDate.now(),
            null
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
            accountRepository,
            new InMemoryTransactionIdempotencyStore(),
            Duration.ofHours(24)
        );

        assertThatThrownBy(() -> useCase.handle(new CreateInternalTransactionCommand(
            "TX-001",
            account.id(),
            TransactionType.CREDIT,
            Money.of("50.0000", CurrencyCode.USD),
            LocalDate.now(),
            null
        )))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("transaction currency must match account currency");
    }

    @Test
    @DisplayName("should replay existing transaction when idempotency key is reused")
    void shouldReplayExistingTransactionWhenIdempotencyKeyIsReused() {
        InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();
        InMemoryTransactionRepository transactionRepository = new InMemoryTransactionRepository();
        InMemoryTransactionIdempotencyStore idempotencyStore = new InMemoryTransactionIdempotencyStore();

        Account account = Account.open("CH-001", "customer-1", CurrencyCode.CHF);
        accountRepository.save(account);

        CreateInternalTransactionUseCase useCase = new CreateInternalTransactionUseCase(
            transactionRepository,
            accountRepository,
            idempotencyStore,
            Duration.ofHours(24)
        );

        TransactionCreationResult first = useCase.handle(new CreateInternalTransactionCommand(
            "TX-001",
            account.id(),
            TransactionType.CREDIT,
            Money.of("50.0000", CurrencyCode.CHF),
            LocalDate.now(),
            "idem-001"
        ));
        TransactionCreationResult replay = useCase.handle(new CreateInternalTransactionCommand(
            "TX-001",
            account.id(),
            TransactionType.CREDIT,
            Money.of("50.0000", CurrencyCode.CHF),
            LocalDate.now(),
            "idem-001"
        ));

        assertThat(first.replayed()).isFalse();
        assertThat(replay.replayed()).isTrue();
        assertThat(replay.transaction().id()).isEqualTo(first.transaction().id());
        assertThat(transactionRepository.saveCount).isEqualTo(1);
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

        private final Map<UUID, InternalTransaction> storage = new HashMap<>();
        private InternalTransaction savedTransaction;
        private int saveCount;

        @Override
        public InternalTransaction save(InternalTransaction transaction) {
            this.savedTransaction = transaction;
            this.saveCount++;
            storage.put(transaction.id(), transaction);
            return transaction;
        }

        @Override
        public Optional<InternalTransaction> findById(UUID transactionId) {
            return Optional.ofNullable(storage.get(transactionId));
        }

        @Override
        public java.util.List<InternalTransaction> findAll() {
            return java.util.List.copyOf(storage.values());
        }

        @Override
        public java.util.List<InternalTransaction> findByAccountId(UUID accountId) {
            return storage.values().stream()
                .filter(transaction -> transaction.accountId().equals(accountId))
                .toList();
        }
    }

    private static final class InMemoryTransactionIdempotencyStore implements TransactionIdempotencyStore {

        private final Map<String, IdempotencyRecord> storage = new HashMap<>();

        @Override
        public Optional<IdempotencyRecord> find(String key) {
            return Optional.ofNullable(storage.get(key));
        }

        @Override
        public boolean markProcessing(String key, Duration ttl) {
            if (storage.containsKey(key)) {
                return false;
            }
            storage.put(key, new IdempotencyRecord(IdempotencyStatus.PROCESSING, null));
            return true;
        }

        @Override
        public void markCompleted(String key, UUID transactionId, Duration ttl) {
            storage.put(key, new IdempotencyRecord(IdempotencyStatus.COMPLETED, transactionId));
        }

        @Override
        public void clear(String key) {
            storage.remove(key);
        }
    }
}
