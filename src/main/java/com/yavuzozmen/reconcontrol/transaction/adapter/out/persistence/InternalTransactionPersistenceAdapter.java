package com.yavuzozmen.reconcontrol.transaction.adapter.out.persistence;

import com.yavuzozmen.reconcontrol.transaction.application.port.out.InternalTransactionRepository;
import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!test")
public class InternalTransactionPersistenceAdapter implements InternalTransactionRepository {

    private final InternalTransactionJpaRepository internalTransactionJpaRepository;

    public InternalTransactionPersistenceAdapter(
        InternalTransactionJpaRepository internalTransactionJpaRepository
    ) {
        this.internalTransactionJpaRepository = Objects.requireNonNull(
            internalTransactionJpaRepository,
            "internalTransactionJpaRepository must not be null"
        );
    }

    @Override
    public InternalTransaction save(InternalTransaction transaction) {
        return InternalTransactionJpaMapper.toDomain(
            internalTransactionJpaRepository.saveAndFlush(
                InternalTransactionJpaMapper.toJpaEntity(transaction)
            )
        );
    }

    @Override
    public Optional<InternalTransaction> findById(UUID transactionId) {
        return internalTransactionJpaRepository.findById(transactionId)
            .map(InternalTransactionJpaMapper::toDomain);
    }

    @Override
    public List<InternalTransaction> findAll() {
        return internalTransactionJpaRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(InternalTransactionJpaMapper::toDomain)
            .toList();
    }

    @Override
    public List<InternalTransaction> findByAccountId(UUID accountId) {
        return internalTransactionJpaRepository.findAllByAccountIdOrderByCreatedAtDesc(accountId)
            .stream()
            .map(InternalTransactionJpaMapper::toDomain)
            .toList();
    }

    @Override
    public long countCreatedAfter(UUID accountId, OffsetDateTime threshold) {
        return internalTransactionJpaRepository.countByAccountIdAndCreatedAtGreaterThanEqual(
            accountId,
            threshold
        );
    }
}
