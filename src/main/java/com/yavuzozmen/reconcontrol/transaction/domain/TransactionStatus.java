package com.yavuzozmen.reconcontrol.transaction.domain;

public enum TransactionStatus {
    RECEIVED,
    BOOKED,
    REJECTED,
    SETTLEMENT_PENDING,
    SETTLED
}
