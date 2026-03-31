package com.yavuzozmen.reconcontrol.infra.outbox;

import io.github.resilience4j.retry.annotation.Retry;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.PageRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!test")
public class OutboxPublisher {

    private final OutboxEventJpaRepository outboxEventJpaRepository;
    private final OutboxPublisherProperties properties;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(
        OutboxEventJpaRepository outboxEventJpaRepository,
        OutboxPublisherProperties properties,
        KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.outboxEventJpaRepository = Objects.requireNonNull(
            outboxEventJpaRepository,
            "outboxEventJpaRepository must not be null"
        );
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate, "kafkaTemplate must not be null");
    }

    @Scheduled(fixedDelayString = "${app.outbox.publisher.delay-ms:1000}")
    @Transactional
    @Retry(name = "outboxPublishing")
    public void publishPendingEvents() {
        List<OutboxEventJpaEntity> batch = outboxEventJpaRepository.findByStatusInOrderByCreatedAtAsc(
            List.of(OutboxStatus.PENDING, OutboxStatus.FAILED),
            PageRequest.of(0, properties.batchSize())
        );

        for (OutboxEventJpaEntity outboxEvent : batch) {
            try {
                kafkaTemplate.send(
                    outboxEvent.getTopic(),
                    outboxEvent.getMessageKey(),
                    outboxEvent.getPayload()
                ).get();
                outboxEvent.markPublished(OffsetDateTime.now());
            } catch (Exception exception) {
                outboxEvent.markFailed(exception.getMessage());
            }
        }
    }
}
