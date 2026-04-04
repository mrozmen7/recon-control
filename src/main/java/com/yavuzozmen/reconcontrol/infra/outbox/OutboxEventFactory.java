package com.yavuzozmen.reconcontrol.infra.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventFactory {

    private final ObjectMapper objectMapper;

    public OutboxEventFactory(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    public OutboxEventJpaEntity create(
        String aggregateType,
        UUID aggregateId,
        String topic,
        String messageKey,
        String eventType,
        Object payload
    ) {
        return new OutboxEventJpaEntity(
            UUID.randomUUID(),
            aggregateType,
            aggregateId,
            topic,
            messageKey,
            eventType,
            serialize(payload),
            OutboxStatus.PENDING,
            0,
            null,
            OffsetDateTime.now(),
            null
        );
    }

    private String serialize(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("failed to serialize outbox payload", exception);
        }
    }
}
