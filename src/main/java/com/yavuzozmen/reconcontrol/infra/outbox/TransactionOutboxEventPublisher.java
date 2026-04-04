package com.yavuzozmen.reconcontrol.infra.outbox;

import com.yavuzozmen.reconcontrol.infra.kafka.KafkaTopicsProperties;
import com.yavuzozmen.reconcontrol.infra.kafka.event.TransactionEventPayload;
import com.yavuzozmen.reconcontrol.infra.kafka.event.TransactionEventType;
import com.yavuzozmen.reconcontrol.transaction.application.port.out.TransactionEventPublisher;
import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;
import java.util.Objects;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class TransactionOutboxEventPublisher implements TransactionEventPublisher {

    private final OutboxEventJpaRepository outboxEventJpaRepository;
    private final OutboxEventFactory outboxEventFactory;
    private final KafkaTopicsProperties topicsProperties;

    public TransactionOutboxEventPublisher(
        OutboxEventJpaRepository outboxEventJpaRepository,
        OutboxEventFactory outboxEventFactory,
        KafkaTopicsProperties topicsProperties
    ) {
        this.outboxEventJpaRepository = Objects.requireNonNull(
            outboxEventJpaRepository,
            "outboxEventJpaRepository must not be null"
        );
        this.outboxEventFactory = Objects.requireNonNull(
            outboxEventFactory,
            "outboxEventFactory must not be null"
        );
        this.topicsProperties = Objects.requireNonNull(
            topicsProperties,
            "topicsProperties must not be null"
        );
    }

    @Override
    public void publishTransactionBooked(InternalTransaction transaction) {
        store(TransactionEventType.TRANSACTION_BOOKED, transaction);
    }

    @Override
    public void publishSettlementPending(InternalTransaction transaction) {
        store(TransactionEventType.TRANSACTION_SETTLEMENT_PENDING, transaction);
    }

    @Override
    public void publishSettled(InternalTransaction transaction) {
        store(TransactionEventType.TRANSACTION_SETTLED, transaction);
    }

    private void store(TransactionEventType eventType, InternalTransaction transaction) {
        TransactionEventPayload payload = TransactionEventPayload.fromDomain(eventType, transaction);
        outboxEventJpaRepository.save(
            outboxEventFactory.create(
                "INTERNAL_TRANSACTION",
                transaction.id(),
                topicsProperties.transactionEvents(),
                transaction.id().toString(),
                eventType.name(),
                payload
            )
        );
    }
}
