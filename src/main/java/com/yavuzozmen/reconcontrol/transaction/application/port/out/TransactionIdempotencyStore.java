package com.yavuzozmen.reconcontrol.transaction.application.port.out;

import com.yavuzozmen.reconcontrol.transaction.application.IdempotencyRecord;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public interface TransactionIdempotencyStore {

    Optional<IdempotencyRecord> find(String key);

    boolean markProcessing(String key, Duration ttl);

    void markCompleted(String key, UUID transactionId, Duration ttl);

    void clear(String key);
}
