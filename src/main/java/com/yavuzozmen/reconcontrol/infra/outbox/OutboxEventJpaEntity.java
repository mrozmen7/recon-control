package com.yavuzozmen.reconcontrol.infra.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEventJpaEntity {

    @Id
    private UUID id;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(nullable = false)
    private String topic;

    @Column(name = "message_key", nullable = false)
    private String messageKey;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(nullable = false, columnDefinition = "text")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    protected OutboxEventJpaEntity() {}

    public OutboxEventJpaEntity(
        UUID id,
        String aggregateType,
        UUID aggregateId,
        String topic,
        String messageKey,
        String eventType,
        String payload,
        OutboxStatus status,
        int retryCount,
        String lastError,
        OffsetDateTime createdAt,
        OffsetDateTime publishedAt
    ) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.topic = topic;
        this.messageKey = messageKey;
        this.eventType = eventType;
        this.payload = payload;
        this.status = status;
        this.retryCount = retryCount;
        this.lastError = lastError;
        this.createdAt = createdAt;
        this.publishedAt = publishedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getPayload() {
        return payload;
    }

    public String getEventType() {
        return eventType;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void markPublished(OffsetDateTime publishedAt) {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = publishedAt;
        this.lastError = null;
    }

    public void markFailed(String lastError) {
        this.status = OutboxStatus.FAILED;
        this.retryCount = retryCount + 1;
        this.lastError = lastError;
    }
}
