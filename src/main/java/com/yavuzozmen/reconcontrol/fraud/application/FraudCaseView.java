package com.yavuzozmen.reconcontrol.fraud.application;

import com.yavuzozmen.reconcontrol.fraud.domain.FraudCase;
import java.util.UUID;

public record FraudCaseView(
    UUID id,
    UUID sourceEventId,
    UUID transactionId,
    UUID accountId,
    String referenceNo,
    String ruleCode,
    String severity,
    String status,
    String reason,
    String createdAt
) {
    public static FraudCaseView fromDomain(FraudCase fraudCase) {
        return new FraudCaseView(
            fraudCase.id(),
            fraudCase.sourceEventId(),
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
