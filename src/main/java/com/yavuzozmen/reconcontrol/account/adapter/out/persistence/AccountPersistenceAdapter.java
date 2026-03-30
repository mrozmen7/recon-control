package com.yavuzozmen.reconcontrol.account.adapter.out.persistence;

import com.yavuzozmen.reconcontrol.account.application.port.out.AccountRepository;
import com.yavuzozmen.reconcontrol.account.domain.Account;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!test")
public class AccountPersistenceAdapter implements AccountRepository {

    private final AccountJpaRepository accountJpaRepository;

    public AccountPersistenceAdapter(AccountJpaRepository accountJpaRepository) {
        this.accountJpaRepository = Objects.requireNonNull(
            accountJpaRepository,
            "accountJpaRepository must not be null"
        );
    }

    @Override
    public Account save(Account account) {
        AccountJpaEntity savedEntity = accountJpaRepository.save(AccountJpaMapper.toJpaEntity(account));
        return AccountJpaMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Account> findById(UUID accountId) {
        return accountJpaRepository.findById(accountId).map(AccountJpaMapper::toDomain);
    }
}
