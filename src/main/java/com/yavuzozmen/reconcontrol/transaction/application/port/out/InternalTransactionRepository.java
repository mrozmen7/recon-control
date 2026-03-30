package com.yavuzozmen.reconcontrol.transaction.application.port.out;

import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;
import java.util.List;
import java.util.UUID;

public interface InternalTransactionRepository {

    InternalTransaction save(InternalTransaction transaction);

    List<InternalTransaction> findAll();

    List<InternalTransaction> findByAccountId(UUID accountId);
}
