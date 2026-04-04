package com.yavuzozmen.reconcontrol.transaction.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

class GetInternalTransactionUseCaseTest {

    @Test
    void shouldReturnTransactionWhenItExists() {
        InternalTransaction transaction = sampleTransaction(TransactionStatus.BOOKED);
        GetInternalTransactionUseCase useCase = new GetInternalTransactionUseCase(
            new InMemoryTransactionRepository(transaction)
        );

        InternalTransaction result = useCase.handle(transaction.id());

        assertThat(result.id()).isEqualTo(transaction.id());
        assertThat(result.status()).isEqualTo(TransactionStatus.BOOKED);
    }

    @Test
    void shouldThrowWhenTransactionIsMissing() {
        GetInternalTransactionUseCase useCase = new GetInternalTransactionUseCase(
            new InMemoryTransactionRepository(null)
        );

        assertThatThrownBy(() -> useCase.handle(UUID.randomUUID()))
            .isInstanceOf(TransactionNotFoundException.class);
    }

    private static InternalTransaction sampleTransaction(TransactionStatus status) {
        return InternalTransaction.rehydrate(
            UUID.fromString("66666666-6666-6666-6666-666666666666"),
            "TRX-GET-001",
            UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
            TransactionType.CREDIT,
            new Money(new java.math.BigDecimal("20.0000"), CurrencyCode.CHF),
            LocalDate.parse("2026-03-31"),
            OffsetDateTime.parse("2026-03-31T10:00:00+02:00"),
            status
        );
    }

    private record InMemoryTransactionRepository(InternalTransaction transaction)
        implements InternalTransactionRepository {

        @Override
        public InternalTransaction save(InternalTransaction transaction) {
            throw new UnsupportedOperationException("save is not needed in this test");
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
