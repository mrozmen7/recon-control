package com.yavuzozmen.reconcontrol.infra.kafka.event;

import com.yavuzozmen.reconcontrol.fraud.domain.FraudCase;
import java.util.UUID;

public record FraudAlertEventPayload(
    UUID eventId,
    UUID fraudCaseId,
    UUID transactionId,
    UUID accountId,
    String referenceNo,
    String ruleCode,
    String severity,
    String status,
    String reason,
    String occurredAt
) {
    public static FraudAlertEventPayload fromDomain(FraudCase fraudCase) {
        return new FraudAlertEventPayload(
            UUID.randomUUID(),
            fraudCase.id(),
            fraudCase.transactionId(),
            fraudCase.accountId(),
            fraudCase.referenceNo(),
            fraudCase.ruleCode(),
            fraudCase.severity().name(),
            fraudCase.status().name(),
            fraudCase.reason(),
            fraudCase.createdAt().toString()
        );
    }
}
