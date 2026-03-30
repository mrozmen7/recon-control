package com.yavuzozmen.reconcontrol.account.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yavuzozmen.reconcontrol.account.application.port.out.AccountRepository;
import com.yavuzozmen.reconcontrol.account.domain.Account;
import com.yavuzozmen.reconcontrol.common.domain.CurrencyCode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GetAccountUseCaseTest {

    @Test
    void shouldReturnAccountWhenItExists() {
        InMemoryAccountRepository repository = new InMemoryAccountRepository();
        Account storedAccount = repository.save(
            Account.open("CH1000000001", "cust-001", CurrencyCode.CHF)
        );
        GetAccountUseCase useCase = new GetAccountUseCase(repository);

        Account result = useCase.handle(storedAccount.id());

        assertThat(result.id()).isEqualTo(storedAccount.id());
        assertThat(result.accountNumber()).isEqualTo("CH1000000001");
    }

    @Test
    void shouldThrowWhenAccountDoesNotExist() {
        GetAccountUseCase useCase = new GetAccountUseCase(new InMemoryAccountRepository());
        UUID missingId = UUID.randomUUID();

        assertThatThrownBy(() -> useCase.handle(missingId))
            .isInstanceOf(AccountNotFoundException.class)
            .hasMessageContaining(missingId.toString());
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
}
