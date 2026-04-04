package com.yavuzozmen.reconcontrol.transaction.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.yavuzozmen.reconcontrol.common.domain.CurrencyCode;
import com.yavuzozmen.reconcontrol.common.domain.Money;
import com.yavuzozmen.reconcontrol.transaction.application.port.out.InternalTransactionRepository;
import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;
import com.yavuzozmen.reconcontrol.transaction.domain.TransactionStatus;
import com.yavuzozmen.reconcontrol.transaction.domain.TransactionType;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MarkTransactionSettlementPendingUseCaseTest {

    @Test
    void shouldMoveBookedTransactionToSettlementPending() {
        InMemoryTransactionRepository repository = new InMemoryTransactionRepository(
            sampleTransaction(TransactionStatus.BOOKED)
        );
        MarkTransactionSettlementPendingUseCase useCase =
            new MarkTransactionSettlementPendingUseCase(repository);

        InternalTransaction result = useCase.handle(repository.transaction.id());

        assertThat(result.status()).isEqualTo(TransactionStatus.SETTLEMENT_PENDING);
    }

    private static InternalTransaction sampleTransaction(TransactionStatus status) {
        return InternalTransaction.rehydrate(
            UUID.fromString("77777777-7777-7777-7777-777777777777"),
            "TRX-SETTLE-001",
            UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
            TransactionType.CREDIT,
            new Money(new java.math.BigDecimal("20.0000"), CurrencyCode.CHF),
            LocalDate.parse("2026-03-31"),
            OffsetDateTime.parse("2026-03-31T11:00:00+02:00"),
            status
        );
    }

    private static final class InMemoryTransactionRepository implements InternalTransactionRepository {

        private InternalTransaction transaction;

        private InMemoryTransactionRepository(InternalTransaction transaction) {
            this.transaction = transaction;
        }

        @Override
        public InternalTransaction save(InternalTransaction transaction) {
            this.transaction = transaction;
            return transaction;
        }

        @Override
        public Optional<InternalTransaction> findById(UUID transactionId) {
            if (transaction == null || !transaction.id().equals(transactionId)) {
                return Optional.empty();
            }
            return Optional.of(transaction);
        }

        @Override
        public List<InternalTransaction> findAll() {
            return transaction == null ? List.of() : List.of(transaction);
        }

        @Override
        public List<InternalTransaction> findByAccountId(UUID accountId) {
            if (transaction == null || !transaction.accountId().equals(accountId)) {
                return List.of();
            }
            return List.of(transaction);
        }
    }
}
