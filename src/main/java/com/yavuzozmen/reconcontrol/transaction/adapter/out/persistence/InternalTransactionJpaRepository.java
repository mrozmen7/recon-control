package com.yavuzozmen.reconcontrol.transaction.adapter.out.persistence;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternalTransactionJpaRepository
    extends JpaRepository<InternalTransactionJpaEntity, UUID> {

    List<InternalTransactionJpaEntity> findAllByOrderByCreatedAtDesc();

    List<InternalTransactionJpaEntity> findAllByAccountIdOrderByCreatedAtDesc(UUID accountId);

    long countByAccountIdAndCreatedAtGreaterThanEqual(UUID accountId, OffsetDateTime threshold);
}
