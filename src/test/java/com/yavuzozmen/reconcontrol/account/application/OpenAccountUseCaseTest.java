package com.yavuzozmen.reconcontrol.account.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.yavuzozmen.reconcontrol.account.application.port.out.AccountRepository;
import com.yavuzozmen.reconcontrol.account.domain.Account;
import com.yavuzozmen.reconcontrol.common.domain.CurrencyCode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OpenAccountUseCaseTest {

    @Test
    @DisplayName("should open and save account")
    void shouldOpenAndSaveAccount() {
        InMemoryAccountRepository repository = new InMemoryAccountRepository();
        OpenAccountUseCase useCase = new OpenAccountUseCase(repository);

        Account account = useCase.handle(new OpenAccountCommand(
            "CH-001",
            "customer-1",
            CurrencyCode.CHF
        ));

        assertThat(account.accountNumber()).isEqualTo("CH-001");
        assertThat(account.customerId()).isEqualTo("customer-1");
        assertThat(account.currency()).isEqualTo(CurrencyCode.CHF);
        assertThat(repository.findById(account.id())).contains(account);
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
