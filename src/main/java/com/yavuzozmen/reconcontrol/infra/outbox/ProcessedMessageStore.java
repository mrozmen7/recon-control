package com.yavuzozmen.reconcontrol.infra.outbox;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class ProcessedMessageStore {

    private final ProcessedKafkaMessageJpaRepository repository;

    public ProcessedMessageStore(ProcessedKafkaMessageJpaRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    public boolean isProcessed(String consumerName, UUID messageId) {
        return repository.findByConsumerNameAndMessageId(consumerName, messageId).isPresent();
    }

    public void markProcessed(String consumerName, UUID messageId) {
        repository.saveAndFlush(
            new ProcessedKafkaMessageJpaEntity(
                UUID.randomUUID(),
                consumerName,
                messageId,
                OffsetDateTime.now()
            )
        );
    }
}
