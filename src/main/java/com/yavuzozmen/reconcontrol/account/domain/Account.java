package com.yavuzozmen.reconcontrol.account.domain;

import com.yavuzozmen.reconcontrol.common.domain.CurrencyCode;
import com.yavuzozmen.reconcontrol.common.domain.Money;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity that represents a bank account.
 *
 * <p>This class contains core account business rules and no persistence annotations.</p>
 */
public final class Account {

    private final UUID id;
    private final long version;
    private final String accountNumber;
    private final String customerId;
    private final CurrencyCode currency;
    private Money balance;
    private AccountStatus status;

    private Account(
        UUID id,
        long version,
        String accountNumber,
        String customerId,
        CurrencyCode currency,
        Money balance,
        AccountStatus status
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        if (version < 0) {
            throw new IllegalArgumentException("version must not be negative");
        }
        this.version = version;
        this.accountNumber = validateText(accountNumber, "accountNumber");
        this.customerId = validateText(customerId, "customerId");
        this.currency = Objects.requireNonNull(currency, "currency must not be null");
        this.balance = Objects.requireNonNull(balance, "balance must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");

        if (balance.currency() != currency) {
            throw new IllegalArgumentException("account currency and balance currency must match");
        }
    }

    public static Account open(String accountNumber, String customerId, CurrencyCode currency) {
        return new Account(
            UUID.randomUUID(),
            0L,
            accountNumber,
            customerId,
            currency,
            Money.zero(currency),
            AccountStatus.ACTIVE
        );
    }

    public static Account rehydrate(
        UUID id,
        long version,
        String accountNumber,
        String customerId,
        CurrencyCode currency,
        Money balance,
        AccountStatus status
    ) {
        return new Account(id, version, accountNumber, customerId, currency, balance, status);
    }

    public UUID id() {
        return id;
    }

    public long version() {
        return version;
    }

    public String accountNumber() {
        return accountNumber;
    }

    public String customerId() {
        return customerId;
    }

    public CurrencyCode currency() {
        return currency;
    }

    public Money balance() {
        return balance;
    }

    public AccountStatus status() {
        return status;
    }

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    public void deposit(Money amount) {
        ensureActive();
        validateMoney(amount);

        if (!amount.isPositive()) {
            throw new IllegalArgumentException("deposit amount must be positive");
        }

        balance = balance.add(amount);
    }

    public void withdraw(Money amount) {
        ensureActive();
        validateMoney(amount);

        if (!amount.isPositive()) {
            throw new IllegalArgumentException("withdraw amount must be positive");
        }

        if (amount.isGreaterThan(balance)) {
            throw new IllegalStateException("insufficient funds");
        }

        balance = balance.subtract(amount);
    }

    public void freeze() {
        if (status == AccountStatus.CLOSED) {
            throw new IllegalStateException("closed account cannot be frozen");
        }

        status = AccountStatus.FROZEN;
    }

    public void activate() {
        if (status == AccountStatus.CLOSED) {
            throw new IllegalStateException("closed account cannot be activated");
        }

        status = AccountStatus.ACTIVE;
    }

    public void close() {
        if (!balance.isZero()) {
            throw new IllegalStateException("account balance must be zero before closing");
        }

        status = AccountStatus.CLOSED;
    }

    private void ensureActive() {
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("account must be active");
        }
    }

    private void validateMoney(Money amount) {
        Objects.requireNonNull(amount, "amount must not be null");

        if (amount.currency() != currency) {
            throw new IllegalArgumentException("money currency must match account currency");
        }
    }

    private static String validateText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }

        return trimmed;
    }
}
