package com.yavuzozmen.reconcontrol.transaction.application.port.out;

import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InternalTransactionRepository {

    InternalTransaction save(InternalTransaction transaction);

    Optional<InternalTransaction> findById(UUID transactionId);

    List<InternalTransaction> findAll();

    List<InternalTransaction> findByAccountId(UUID accountId);

    default long countCreatedAfter(UUID accountId, OffsetDateTime threshold) {
        return 0L;
    }
}
