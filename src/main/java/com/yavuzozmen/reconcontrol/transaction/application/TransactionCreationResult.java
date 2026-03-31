package com.yavuzozmen.reconcontrol.transaction.application;

import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;
import java.util.Objects;

public record TransactionCreationResult(
    InternalTransaction transaction,
    boolean replayed
) {

    public TransactionCreationResult {
        Objects.requireNonNull(transaction, "transaction must not be null");
    }

    public static TransactionCreationResult created(InternalTransaction transaction) {
        return new TransactionCreationResult(transaction, false);
    }

    public static TransactionCreationResult replayed(InternalTransaction transaction) {
        return new TransactionCreationResult(transaction, true);
    }
}
