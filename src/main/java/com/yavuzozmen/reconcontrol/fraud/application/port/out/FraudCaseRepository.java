package com.yavuzozmen.reconcontrol.fraud.application.port.out;

import com.yavuzozmen.reconcontrol.fraud.domain.FraudCase;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FraudCaseRepository {

    FraudCase save(FraudCase fraudCase);

    Optional<FraudCase> findById(UUID fraudCaseId);

    List<FraudCase> findAll();

    List<FraudCase> findByTransactionId(UUID transactionId);

    boolean existsBySourceEventIdAndRuleCode(UUID sourceEventId, String ruleCode);
}
