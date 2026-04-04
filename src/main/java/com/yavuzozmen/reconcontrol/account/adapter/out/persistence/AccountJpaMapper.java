package com.yavuzozmen.reconcontrol.account.adapter.out.persistence;

import com.yavuzozmen.reconcontrol.account.domain.Account;
import com.yavuzozmen.reconcontrol.common.domain.Money;

final class AccountJpaMapper {

    private AccountJpaMapper() {
    }

    static AccountJpaEntity toJpaEntity(Account account) {
        return toJpaEntity(account, account.version());
    }

    static AccountJpaEntity toNewJpaEntity(Account account) {
        return toJpaEntity(account, null);
    }

    private static AccountJpaEntity toJpaEntity(Account account, Long version) {
        return new AccountJpaEntity(
            account.id(),
            version,
            account.accountNumber(),
            account.customerId(),
            account.currency(),
            account.balance().amount(),
            account.status()
        );
    }

    static Account toDomain(AccountJpaEntity entity) {
        return Account.rehydrate(
            entity.getId(),
            entity.getVersion(),
            entity.getAccountNumber(),
            entity.getCustomerId(),
            entity.getCurrency(),
            new Money(entity.getBalanceAmount(), entity.getCurrency()),
            entity.getStatus()
        );
    }
}
