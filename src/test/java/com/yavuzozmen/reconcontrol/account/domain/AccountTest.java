package com.yavuzozmen.reconcontrol.account.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yavuzozmen.reconcontrol.common.domain.CurrencyCode;
import com.yavuzozmen.reconcontrol.common.domain.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AccountTest {

    @Test
    @DisplayName("should open account as active with zero balance")
    void shouldOpenAccountAsActiveWithZeroBalance() {
        Account account = Account.open("CH-001", "customer-1", CurrencyCode.CHF);

        assertThat(account.status()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account.balance()).isEqualTo(Money.zero(CurrencyCode.CHF));
    }

    @Test
    @DisplayName("should increase balance when deposit is valid")
    void shouldIncreaseBalanceWhenDepositIsValid() {
        Account account = Account.open("CH-001", "customer-1", CurrencyCode.CHF);

        account.deposit(Money.of("100.0000", CurrencyCode.CHF));

        assertThat(account.balance().amount()).isEqualByComparingTo("100.0000");
    }

    @Test
    @DisplayName("should decrease balance when withdraw is valid")
    void shouldDecreaseBalanceWhenWithdrawIsValid() {
        Account account = Account.open("CH-001", "customer-1", CurrencyCode.CHF);
        account.deposit(Money.of("100.0000", CurrencyCode.CHF));

        account.withdraw(Money.of("40.0000", CurrencyCode.CHF));

        assertThat(account.balance().amount()).isEqualByComparingTo("60.0000");
    }

    @Test
    @DisplayName("should reject withdraw when funds are insufficient")
    void shouldRejectWithdrawWhenFundsAreInsufficient() {
        Account account = Account.open("CH-001", "customer-1", CurrencyCode.CHF);

        assertThatThrownBy(() -> account.withdraw(Money.of("10.0000", CurrencyCode.CHF)))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("insufficient funds");
    }

    @Test
    @DisplayName("should reject operations when account is not active")
    void shouldRejectOperationsWhenAccountIsNotActive() {
        Account account = Account.open("CH-001", "customer-1", CurrencyCode.CHF);
        account.freeze();

        assertThatThrownBy(() -> account.deposit(Money.of("10.0000", CurrencyCode.CHF)))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("account must be active");
    }

    @Test
    @DisplayName("should reject money with a different currency")
    void shouldRejectMoneyWithDifferentCurrency() {
        Account account = Account.open("CH-001", "customer-1", CurrencyCode.CHF);

        assertThatThrownBy(() -> account.deposit(Money.of("10.0000", CurrencyCode.USD)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("money currency must match account currency");
    }

    @Test
    @DisplayName("should close account only when balance is zero")
    void shouldCloseAccountOnlyWhenBalanceIsZero() {
        Account account = Account.open("CH-001", "customer-1", CurrencyCode.CHF);
        account.deposit(Money.of("5.0000", CurrencyCode.CHF));

        assertThatThrownBy(account::close)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("account balance must be zero before closing");

        account.withdraw(Money.of("5.0000", CurrencyCode.CHF));
        account.close();

        assertThat(account.status()).isEqualTo(AccountStatus.CLOSED);
    }
}
