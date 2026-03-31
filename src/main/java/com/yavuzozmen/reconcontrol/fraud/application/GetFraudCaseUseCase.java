package com.yavuzozmen.reconcontrol.fraud.application;

import com.yavuzozmen.reconcontrol.fraud.application.port.out.FraudCaseRepository;
import java.util.Objects;
import java.util.UUID;

public class GetFraudCaseUseCase {

    private final FraudCaseRepository fraudCaseRepository;

    public GetFraudCaseUseCase(FraudCaseRepository fraudCaseRepository) {
        this.fraudCaseRepository = Objects.requireNonNull(
            fraudCaseRepository,
            "fraudCaseRepository must not be null"
        );
    }

    public FraudCaseView handle(UUID fraudCaseId) {
        Objects.requireNonNull(fraudCaseId, "fraudCaseId must not be null");

        return fraudCaseRepository.findById(fraudCaseId)
            .map(FraudCaseView::fromDomain)
            .orElseThrow(() -> new FraudCaseNotFoundException(fraudCaseId));
    }
}
