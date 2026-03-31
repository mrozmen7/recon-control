package com.yavuzozmen.reconcontrol.fraud.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudCaseJpaRepository extends JpaRepository<FraudCaseJpaEntity, UUID> {

    List<FraudCaseJpaEntity> findAllByOrderByCreatedAtDesc();

    List<FraudCaseJpaEntity> findAllByTransactionIdOrderByCreatedAtDesc(UUID transactionId);

    Optional<FraudCaseJpaEntity> findBySourceEventIdAndRuleCode(UUID sourceEventId, String ruleCode);
}
