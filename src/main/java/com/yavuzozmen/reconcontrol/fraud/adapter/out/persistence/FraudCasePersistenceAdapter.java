package com.yavuzozmen.reconcontrol.fraud.adapter.out.persistence;

import com.yavuzozmen.reconcontrol.fraud.application.port.out.FraudCaseRepository;
import com.yavuzozmen.reconcontrol.fraud.domain.FraudCase;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!test")
public class FraudCasePersistenceAdapter implements FraudCaseRepository {

    private final FraudCaseJpaRepository fraudCaseJpaRepository;

    public FraudCasePersistenceAdapter(FraudCaseJpaRepository fraudCaseJpaRepository) {
        this.fraudCaseJpaRepository = Objects.requireNonNull(
            fraudCaseJpaRepository,
            "fraudCaseJpaRepository must not be null"
        );
    }

    @Override
    public FraudCase save(FraudCase fraudCase) {
        return FraudCaseJpaMapper.toDomain(
            fraudCaseJpaRepository.saveAndFlush(FraudCaseJpaMapper.toJpaEntity(fraudCase))
        );
    }

    @Override
    public Optional<FraudCase> findById(UUID fraudCaseId) {
        return fraudCaseJpaRepository.findById(fraudCaseId).map(FraudCaseJpaMapper::toDomain);
    }

    @Override
    public List<FraudCase> findAll() {
        return fraudCaseJpaRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(FraudCaseJpaMapper::toDomain)
            .toList();
    }

    @Override
    public List<FraudCase> findByTransactionId(UUID transactionId) {
        return fraudCaseJpaRepository.findAllByTransactionIdOrderByCreatedAtDesc(transactionId)
            .stream()
            .map(FraudCaseJpaMapper::toDomain)
            .toList();
    }

    @Override
    public boolean existsBySourceEventIdAndRuleCode(UUID sourceEventId, String ruleCode) {
        return fraudCaseJpaRepository.findBySourceEventIdAndRuleCode(sourceEventId, ruleCode)
            .isPresent();
    }
}
