package com.yavuzozmen.reconcontrol.fraud.adapter.in.web;

import com.yavuzozmen.reconcontrol.fraud.application.FraudCaseView;
import java.util.UUID;

public record FraudCaseResponse(
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
    public static FraudCaseResponse fromView(FraudCaseView view) {
        return new FraudCaseResponse(
            view.id(),
            view.sourceEventId(),
            view.transactionId(),
            view.accountId(),
            view.referenceNo(),
            view.ruleCode(),
            view.severity(),
            view.status(),
            view.reason(),
            view.createdAt()
        );
    }
}
