package com.yavuzozmen.reconcontrol.transaction.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yavuzozmen.reconcontrol.common.domain.CurrencyCode;
import com.yavuzozmen.reconcontrol.common.domain.Money;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InternalTransactionTest {

    @Test
    @DisplayName("should create transaction in received status")
    void shouldCreateTransactionInReceivedStatus() {
        InternalTransaction transaction = InternalTransaction.create(
            "TX-001",
            UUID.randomUUID(),
            TransactionType.DEBIT,
            Money.of("150.0000", CurrencyCode.CHF),
            LocalDate.now()
        );

        assertThat(transaction.status()).isEqualTo(TransactionStatus.RECEIVED);
    }

    @Test
    @DisplayName("should reject non-positive transaction amount")
    void shouldRejectNonPositiveTransactionAmount() {
        assertThatThrownBy(() -> InternalTransaction.create(
            "TX-001",
            UUID.randomUUID(),
            TransactionType.CREDIT,
            Money.zero(CurrencyCode.EUR),
            LocalDate.now()
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("transaction amount must be positive");
    }

    @Test
    @DisplayName("should move from received to booked")
    void shouldMoveFromReceivedToBooked() {
        InternalTransaction transaction = newTransaction();

        transaction.markBooked();

        assertThat(transaction.status()).isEqualTo(TransactionStatus.BOOKED);
    }

    @Test
    @DisplayName("should move from booked to settlement pending and then settled")
    void shouldMoveFromBookedToSettlementPendingAndThenSettled() {
        InternalTransaction transaction = newTransaction();
        transaction.markBooked();

        transaction.markSettlementPending();
        transaction.markSettled();

        assertThat(transaction.status()).isEqualTo(TransactionStatus.SETTLED);
    }

    @Test
    @DisplayName("should reject invalid state transition")
    void shouldRejectInvalidStateTransition() {
        InternalTransaction transaction = newTransaction();

        assertThatThrownBy(transaction::markSettled)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("only settlement pending transaction can be settled");
    }

    @Test
    @DisplayName("should reject received transaction")
    void shouldRejectReceivedTransaction() {
        InternalTransaction transaction = newTransaction();

        transaction.reject();

        assertThat(transaction.status()).isEqualTo(TransactionStatus.REJECTED);
    }

    private InternalTransaction newTransaction() {
        return InternalTransaction.create(
            "TX-001",
            UUID.randomUUID(),
            TransactionType.DEBIT,
            Money.of("150.0000", CurrencyCode.CHF),
            LocalDate.now()
        );
    }
}
