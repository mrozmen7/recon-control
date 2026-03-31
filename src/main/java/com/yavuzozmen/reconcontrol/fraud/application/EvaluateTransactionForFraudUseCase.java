package com.yavuzozmen.reconcontrol.fraud.application;

import com.yavuzozmen.reconcontrol.fraud.application.port.out.FraudAlertEventPublisher;
import com.yavuzozmen.reconcontrol.fraud.application.port.out.FraudCaseRepository;
import com.yavuzozmen.reconcontrol.fraud.domain.FraudCase;
import com.yavuzozmen.reconcontrol.fraud.domain.FraudMatch;
import com.yavuzozmen.reconcontrol.infra.kafka.event.TransactionEventPayload;
import com.yavuzozmen.reconcontrol.infra.kafka.event.TransactionEventType;
import com.yavuzozmen.reconcontrol.transaction.application.port.out.InternalTransactionRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.transaction.annotation.Transactional;

public class EvaluateTransactionForFraudUseCase {

    private final FraudCaseRepository fraudCaseRepository;
    private final FraudAlertEventPublisher fraudAlertEventPublisher;
    private final InternalTransactionRepository internalTransactionRepository;
    private final FraudRuleEngine fraudRuleEngine;
    private final FraudRulesProperties properties;

    public EvaluateTransactionForFraudUseCase(
        FraudCaseRepository fraudCaseRepository,
        FraudAlertEventPublisher fraudAlertEventPublisher,
        InternalTransactionRepository internalTransactionRepository,
        FraudRuleEngine fraudRuleEngine,
        FraudRulesProperties properties
    ) {
        this.fraudCaseRepository = Objects.requireNonNull(
            fraudCaseRepository,
            "fraudCaseRepository must not be null"
        );
        this.fraudAlertEventPublisher = Objects.requireNonNull(
            fraudAlertEventPublisher,
            "fraudAlertEventPublisher must not be null"
        );
        this.internalTransactionRepository = Objects.requireNonNull(
            internalTransactionRepository,
            "internalTransactionRepository must not be null"
        );
        this.fraudRuleEngine = Objects.requireNonNull(
            fraudRuleEngine,
            "fraudRuleEngine must not be null"
        );
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
    }

    @Transactional
    public List<FraudCase> handle(TransactionEventPayload event) {
        Objects.requireNonNull(event, "event must not be null");

        if (event.eventType() != TransactionEventType.TRANSACTION_BOOKED) {
            return List.of();
        }

        OffsetDateTime windowStart = event.occurredAt()
            .minusMinutes(properties.burstWindowMinutes());
        long recentTransactionCount = internalTransactionRepository.countCreatedAfter(
            event.accountId(),
            windowStart
        );

        List<FraudMatch> matches = fraudRuleEngine.evaluate(event, recentTransactionCount);
        if (matches.isEmpty()) {
            return List.of();
        }

        return matches.stream()
            .filter(match -> !fraudCaseRepository.existsBySourceEventIdAndRuleCode(
                event.eventId(),
                match.ruleCode()
            ))
            .map(match -> createFraudCase(event, match))
            .toList();
    }

    private FraudCase createFraudCase(TransactionEventPayload event, FraudMatch match) {
        FraudCase fraudCase = FraudCase.open(
            event.eventId(),
            event.transactionId(),
            event.accountId(),
            event.referenceNo(),
            match.ruleCode(),
            match.severity(),
            match.reason()
        );
        FraudCase saved = fraudCaseRepository.save(fraudCase);
        fraudAlertEventPublisher.publishFraudDetected(saved);
        return saved;
    }
}
