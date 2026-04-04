package com.yavuzozmen.reconcontrol.infra.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yavuzozmen.reconcontrol.fraud.application.EvaluateTransactionForFraudUseCase;
import com.yavuzozmen.reconcontrol.infra.kafka.event.TransactionEventPayload;
import com.yavuzozmen.reconcontrol.infra.outbox.ProcessedMessageStore;
import java.util.Objects;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!test")
public class TransactionFraudKafkaConsumer {

    private static final String CONSUMER_NAME = "fraud-transaction-events-consumer";

    private final ObjectMapper objectMapper;
    private final EvaluateTransactionForFraudUseCase evaluateTransactionForFraudUseCase;
    private final ProcessedMessageStore processedMessageStore;

    public TransactionFraudKafkaConsumer(
        ObjectMapper objectMapper,
        EvaluateTransactionForFraudUseCase evaluateTransactionForFraudUseCase,
        ProcessedMessageStore processedMessageStore
    ) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.evaluateTransactionForFraudUseCase = Objects.requireNonNull(
            evaluateTransactionForFraudUseCase,
            "evaluateTransactionForFraudUseCase must not be null"
        );
        this.processedMessageStore = Objects.requireNonNull(
            processedMessageStore,
            "processedMessageStore must not be null"
        );
    }

    @KafkaListener(
        topics = "${app.kafka.topics.transaction-events}",
        groupId = "${app.kafka.consumer.fraud-group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void consume(String payload) {
        TransactionEventPayload event = parse(payload);

        if (processedMessageStore.isProcessed(CONSUMER_NAME, event.eventId())) {
            return;
        }

        if (event.referenceNo() != null && event.referenceNo().startsWith("DLQ-")) {
            throw new IllegalStateException("forced DLQ demo failure for payload " + event.referenceNo());
        }

        evaluateTransactionForFraudUseCase.handle(event);
        processedMessageStore.markProcessed(CONSUMER_NAME, event.eventId());
    }

    private TransactionEventPayload parse(String payload) {
        try {
            return objectMapper.readValue(payload, TransactionEventPayload.class);
        } catch (Exception exception) {
            throw new IllegalArgumentException("unable to parse transaction event payload", exception);
        }
    }
}
