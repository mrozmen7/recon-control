package com.yavuzozmen.reconcontrol.infra.outbox;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedKafkaMessageJpaRepository
    extends JpaRepository<ProcessedKafkaMessageJpaEntity, UUID> {

    Optional<ProcessedKafkaMessageJpaEntity> findByConsumerNameAndMessageId(
        String consumerName,
        UUID messageId
    );
}
