package com.yavuzozmen.reconcontrol.infra.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "processed_kafka_messages")
public class ProcessedKafkaMessageJpaEntity {

    @Id
    private UUID id;

    @Column(name = "consumer_name", nullable = false)
    private String consumerName;

    @Column(name = "message_id", nullable = false)
    private UUID messageId;

    @Column(name = "processed_at", nullable = false)
    private OffsetDateTime processedAt;

    protected ProcessedKafkaMessageJpaEntity() {}

    public ProcessedKafkaMessageJpaEntity(
        UUID id,
        String consumerName,
        UUID messageId,
        OffsetDateTime processedAt
    ) {
        this.id = id;
        this.consumerName = consumerName;
        this.messageId = messageId;
        this.processedAt = processedAt;
    }
}
