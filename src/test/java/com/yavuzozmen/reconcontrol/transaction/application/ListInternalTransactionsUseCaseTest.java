package com.yavuzozmen.reconcontrol.transaction.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.yavuzozmen.reconcontrol.common.domain.CurrencyCode;
import com.yavuzozmen.reconcontrol.common.domain.Money;
import com.yavuzozmen.reconcontrol.transaction.application.port.out.InternalTransactionRepository;
import com.yavuzozmen.reconcontrol.transaction.domain.InternalTransaction;
import com.yavuzozmen.reconcontrol.transaction.domain.TransactionStatus;
import com.yavuzozmen.reconcontrol.transaction.domain.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ListInternalTransactionsUseCaseTest {

    @Test
    void shouldReturnAllTransactionsWhenAccountIdIsNull() {
        InMemoryTransactionRepository repository = new InMemoryTransactionRepository(sampleTransactions());
        ListInternalTransactionsUseCase useCase = new ListInternalTransactionsUseCase(repository);

        List<InternalTransaction> result = useCase.handle(null);

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldReturnFilteredTransactionsWhenAccountIdProvided() {
        List<InternalTransaction> transactions = sampleTransactions();
        UUID accountId = transactions.get(0).accountId();
        InMemoryTransactionRepository repository = new InMemoryTransactionRepository(transactions);
        ListInternalTransactionsUseCase useCase = new ListInternalTransactionsUseCase(repository);

        List<InternalTransaction> result = useCase.handle(accountId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).referenceNo()).isEqualTo("TRX-LIST-001");
    }

    private static List<InternalTransaction> sampleTransactions() {
        return List.of(
            InternalTransaction.rehydrate(
                UUID.randomUUID(),
                "TRX-LIST-001",
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                TransactionType.DEBIT,
                new Money(new BigDecimal("10.0000"), CurrencyCode.CHF),
                LocalDate.parse("2026-03-30"),
                OffsetDateTime.parse("2026-03-30T10:00:00+02:00"),
                TransactionStatus.RECEIVED
            ),
            InternalTransaction.rehydrate(
                UUID.randomUUID(),
                "TRX-LIST-002",
                UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                TransactionType.CREDIT,
                new Money(new BigDecimal("20.0000"), CurrencyCode.EUR),
                LocalDate.parse("2026-03-30"),
                OffsetDateTime.parse("2026-03-30T11:00:00+02:00"),
                TransactionStatus.BOOKED
            )
        );
    }

    private record InMemoryTransactionRepository(List<InternalTransaction> transactions)
        implements InternalTransactionRepository {

        @Override
        public InternalTransaction save(InternalTransaction transaction) {
            throw new UnsupportedOperationException("save is not needed in this test");
        }

        @Override
        public List<InternalTransaction> findAll() {
            return transactions;
        }

        @Override
        public List<InternalTransaction> findByAccountId(UUID accountId) {
            return transactions.stream()
                .filter(transaction -> transaction.accountId().equals(accountId))
                .toList();
        }
    }
}
