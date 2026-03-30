package com.yavuzozmen.reconcontrol.transaction.adapter.out.persistence;

import com.yavuzozmen.reconcontrol.common.domain.Money;
import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;

final class InternalTransactionJpaMapper {

    private InternalTransactionJpaMapper() {
    }

    static InternalTransactionJpaEntity toJpaEntity(InternalTransaction transaction) {
        return new InternalTransactionJpaEntity(
            transaction.id(),
            transaction.referenceNo(),
            transaction.accountId(),
            transaction.type(),
            transaction.amount().amount(),
            transaction.amount().currency(),
            transaction.valueDate(),
            transaction.createdAt(),
            transaction.status()
        );
    }

    static InternalTransaction toDomain(InternalTransactionJpaEntity entity) {
        return InternalTransaction.rehydrate(
            entity.getId(),
            entity.getReferenceNo(),
            entity.getAccountId(),
            entity.getType(),
            new Money(entity.getAmount(), entity.getCurrency()),
            entity.getValueDate(),
            entity.getCreatedAt(),
            entity.getStatus()
        );
    }
}
