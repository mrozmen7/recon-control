package com.yavuzozmen.reconcontrol.infra.outbox;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {

    List<OutboxEventJpaEntity> findByStatusInOrderByCreatedAtAsc(
        Collection<OutboxStatus> statuses,
        Pageable pageable
    );
}
