package com.yavuzozmen.reconcontrol.infra.outbox;

import com.yavuzozmen.reconcontrol.fraud.application.port.out.FraudAlertEventPublisher;
import com.yavuzozmen.reconcontrol.fraud.domain.FraudCase;
import com.yavuzozmen.reconcontrol.infra.kafka.KafkaTopicsProperties;
import com.yavuzozmen.reconcontrol.infra.kafka.event.FraudAlertEventPayload;
import java.util.Objects;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class FraudAlertOutboxEventPublisher implements FraudAlertEventPublisher {

    private final OutboxEventJpaRepository outboxEventJpaRepository;
    private final OutboxEventFactory outboxEventFactory;
    private final KafkaTopicsProperties topicsProperties;

    public FraudAlertOutboxEventPublisher(
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
    public void publishFraudDetected(FraudCase fraudCase) {
        FraudAlertEventPayload payload = FraudAlertEventPayload.fromDomain(fraudCase);
        outboxEventJpaRepository.save(
            outboxEventFactory.create(
                "FRAUD_CASE",
                fraudCase.id(),
                topicsProperties.fraudAlertEvents(),
                fraudCase.id().toString(),
                "FRAUD_CASE_OPENED",
                payload
            )
        );
    }
}
