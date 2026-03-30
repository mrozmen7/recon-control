package com.yavuzozmen.reconcontrol.common.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Immutable monetary value object.
 *
 * <p>Banking code must not use floating-point types for money.</p>
 */
public record Money(BigDecimal amount, CurrencyCode currency) {

    private static final int SCALE = 4;

    public Money {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(currency, "currency must not be null");

        amount = amount.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static Money of(String amount, CurrencyCode currency) {
        return new Money(new BigDecimal(amount), currency);
    }

    public static Money zero(CurrencyCode currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        validateSameCurrency(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    public boolean isNegative() {
        return amount.signum() < 0;
    }

    public boolean isPositive() {
        return amount.signum() > 0;
    }

    public boolean isZero() {
        return amount.signum() == 0;
    }

    public boolean isGreaterThan(Money other) {
        validateSameCurrency(other);
        return amount.compareTo(other.amount) > 0;
    }

    private void validateSameCurrency(Money other) {
        Objects.requireNonNull(other, "other money must not be null");

        if (currency != other.currency) {
            throw new IllegalArgumentException("money currencies must match");
        }
    }
}
