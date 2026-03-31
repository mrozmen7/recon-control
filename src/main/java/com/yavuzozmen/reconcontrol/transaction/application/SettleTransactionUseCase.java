package com.yavuzozmen.reconcontrol.transaction.application;

import com.yavuzozmen.reconcontrol.transaction.application.port.out.InternalTransactionRepository;
import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;
import java.util.Objects;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

public class SettleTransactionUseCase {

    private final InternalTransactionRepository transactionRepository;

    public SettleTransactionUseCase(InternalTransactionRepository transactionRepository) {
        this.transactionRepository = Objects.requireNonNull(
            transactionRepository,
            "transactionRepository must not be null"
        );
    }

    @Transactional
    public InternalTransaction handle(UUID transactionId) {
        Objects.requireNonNull(transactionId, "transactionId must not be null");

        InternalTransaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new TransactionNotFoundException(transactionId));
        transaction.markSettled();
        return transactionRepository.save(transaction);
    }
}
