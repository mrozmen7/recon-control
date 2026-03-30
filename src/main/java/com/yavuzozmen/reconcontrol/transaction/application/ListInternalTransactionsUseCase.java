package com.yavuzozmen.reconcontrol.transaction.application;

import com.yavuzozmen.reconcontrol.transaction.application.port.out.InternalTransactionRepository;
import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListInternalTransactionsUseCase {

    private final InternalTransactionRepository transactionRepository;

    public ListInternalTransactionsUseCase(InternalTransactionRepository transactionRepository) {
        this.transactionRepository = Objects.requireNonNull(
            transactionRepository,
            "transactionRepository must not be null"
        );
    }

    public List<InternalTransaction> handle(UUID accountId) {
        if (accountId == null) {
            return transactionRepository.findAll();
        }

        return transactionRepository.findByAccountId(accountId);
    }
}
