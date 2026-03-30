package com.yavuzozmen.reconcontrol.account.application.port.out;

import com.yavuzozmen.reconcontrol.account.domain.Account;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(UUID accountId);
}
