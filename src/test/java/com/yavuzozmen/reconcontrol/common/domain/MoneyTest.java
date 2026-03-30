package com.yavuzozmen.reconcontrol.common.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    @DisplayName("should normalize scale to four decimals")
    void shouldNormalizeScaleToFourDecimals() {
        Money money = Money.of("10.5", CurrencyCode.CHF);

        assertThat(money.amount()).isEqualByComparingTo(new BigDecimal("10.5000"));
    }

    @Test
    @DisplayName("should add amounts when currencies match")
    void shouldAddAmountsWhenCurrenciesMatch() {
        Money left = Money.of("10.2500", CurrencyCode.CHF);
        Money right = Money.of("2.7500", CurrencyCode.CHF);

        Money result = left.add(right);

        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("13.0000"));
        assertThat(result.currency()).isEqualTo(CurrencyCode.CHF);
    }

    @Test
    @DisplayName("should subtract amounts when currencies match")
    void shouldSubtractAmountsWhenCurrenciesMatch() {
        Money left = Money.of("10.0000", CurrencyCode.EUR);
        Money right = Money.of("3.5000", CurrencyCode.EUR);

        Money result = left.subtract(right);

        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("6.5000"));
    }

    @Test
    @DisplayName("should reject arithmetic for different currencies")
    void shouldRejectArithmeticForDifferentCurrencies() {
        Money chf = Money.of("10.0000", CurrencyCode.CHF);
        Money usd = Money.of("5.0000", CurrencyCode.USD);

        assertThatThrownBy(() -> chf.add(usd))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("money currencies must match");
    }

    @Test
    @DisplayName("should detect positive zero and negative amounts")
    void shouldDetectPositiveZeroAndNegativeAmounts() {
        Money positive = Money.of("10.0000", CurrencyCode.GBP);
        Money zero = Money.zero(CurrencyCode.GBP);
        Money negative = Money.of("-1.2500", CurrencyCode.GBP);

        assertThat(positive.isPositive()).isTrue();
        assertThat(zero.isZero()).isTrue();
        assertThat(negative.isNegative()).isTrue();
    }
}
