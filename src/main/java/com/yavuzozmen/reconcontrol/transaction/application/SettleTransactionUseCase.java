package com.yavuzozmen.reconcontrol.transaction.application;

import com.yavuzozmen.reconcontrol.transaction.application.port.out.InternalTransactionRepository;
import com.yavuzozmen.reconcontrol.transaction.application.port.out.TransactionEventPublisher;
import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;
import java.util.Objects;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

public class SettleTransactionUseCase {

    private final InternalTransactionRepository transactionRepository;
    private final TransactionEventPublisher transactionEventPublisher;

    public SettleTransactionUseCase(InternalTransactionRepository transactionRepository) {
        this(transactionRepository, new NoOpTransactionEventPublisher());
    }

    public SettleTransactionUseCase(
        InternalTransactionRepository transactionRepository,
        TransactionEventPublisher transactionEventPublisher
    ) {
        this.transactionRepository = Objects.requireNonNull(
            transactionRepository,
            "transactionRepository must not be null"
        );
        this.transactionEventPublisher = Objects.requireNonNull(
            transactionEventPublisher,
            "transactionEventPublisher must not be null"
        );
    }

    @Transactional
    public InternalTransaction handle(UUID transactionId) {
        Objects.requireNonNull(transactionId, "transactionId must not be null");

        InternalTransaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new TransactionNotFoundException(transactionId));
        transaction.markSettled();
        InternalTransaction savedTransaction = transactionRepository.save(transaction);
        transactionEventPublisher.publishSettled(savedTransaction);
        return savedTransaction;
    }

    private static final class NoOpTransactionEventPublisher implements TransactionEventPublisher {

        @Override
        public void publishTransactionBooked(InternalTransaction transaction) {}

        @Override
        public void publishSettlementPending(InternalTransaction transaction) {}

        @Override
        public void publishSettled(InternalTransaction transaction) {}
    }
}
