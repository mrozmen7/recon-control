package com.yavuzozmen.reconcontrol.transaction.application;

import com.yavuzozmen.reconcontrol.transaction.application.port.out.InternalTransactionRepository;
import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;
import java.util.Objects;
import java.util.UUID;

public class GetInternalTransactionUseCase {

    private final InternalTransactionRepository transactionRepository;

    public GetInternalTransactionUseCase(InternalTransactionRepository transactionRepository) {
        this.transactionRepository = Objects.requireNonNull(
            transactionRepository,
            "transactionRepository must not be null"
        );
    }

    public InternalTransaction handle(UUID transactionId) {
        Objects.requireNonNull(transactionId, "transactionId must not be null");

        return transactionRepository.findById(transactionId)
            .orElseThrow(() -> new TransactionNotFoundException(transactionId));
    }
}
