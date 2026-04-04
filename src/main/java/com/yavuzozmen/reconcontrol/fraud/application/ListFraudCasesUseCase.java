package com.yavuzozmen.reconcontrol.fraud.application;

import com.yavuzozmen.reconcontrol.fraud.application.port.out.FraudCaseRepository;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListFraudCasesUseCase {

    private final FraudCaseRepository fraudCaseRepository;

    public ListFraudCasesUseCase(FraudCaseRepository fraudCaseRepository) {
        this.fraudCaseRepository = Objects.requireNonNull(
            fraudCaseRepository,
            "fraudCaseRepository must not be null"
        );
    }

    public List<FraudCaseView> handle(UUID transactionId) {
        if (transactionId == null) {
            return fraudCaseRepository.findAll()
                .stream()
                .map(FraudCaseView::fromDomain)
                .toList();
        }

        return fraudCaseRepository.findByTransactionId(transactionId)
            .stream()
            .map(FraudCaseView::fromDomain)
            .toList();
    }
}
