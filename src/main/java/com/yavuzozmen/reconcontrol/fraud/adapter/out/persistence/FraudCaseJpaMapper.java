package com.yavuzozmen.reconcontrol.fraud.adapter.out.persistence;

import com.yavuzozmen.reconcontrol.fraud.domain.FraudCase;

final class FraudCaseJpaMapper {

    private FraudCaseJpaMapper() {}

    static FraudCaseJpaEntity toJpaEntity(FraudCase fraudCase) {
        return new FraudCaseJpaEntity(
            fraudCase.id(),
            fraudCase.sourceEventId(),
            fraudCase.transactionId(),
            fraudCase.accountId(),
            fraudCase.referenceNo(),
            fraudCase.ruleCode(),
            fraudCase.severity(),
            fraudCase.status(),
            fraudCase.reason(),
            fraudCase.createdAt(),
            fraudCase.reviewedAt()
        );
    }

    static FraudCase toDomain(FraudCaseJpaEntity entity) {
        return FraudCase.rehydrate(
            entity.getId(),
            entity.getSourceEventId(),
            entity.getTransactionId(),
            entity.getAccountId(),
            entity.getReferenceNo(),
            entity.getRuleCode(),
            entity.getSeverity(),
            entity.getReason(),
            entity.getCreatedAt(),
            entity.getStatus(),
            entity.getReviewedAt()
        );
    }
}
