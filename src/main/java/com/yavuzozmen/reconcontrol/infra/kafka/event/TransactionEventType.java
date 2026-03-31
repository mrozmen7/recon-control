package com.yavuzozmen.reconcontrol.infra.kafka.event;

public enum TransactionEventType {
    TRANSACTION_BOOKED,
    TRANSACTION_SETTLEMENT_PENDING,
    TRANSACTION_SETTLED
}
