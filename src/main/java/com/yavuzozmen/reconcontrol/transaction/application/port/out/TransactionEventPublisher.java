package com.yavuzozmen.reconcontrol.transaction.application.port.out;

import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;

public interface TransactionEventPublisher {

    void publishTransactionBooked(InternalTransaction transaction);

    void publishSettlementPending(InternalTransaction transaction);

    void publishSettled(InternalTransaction transaction);
}
