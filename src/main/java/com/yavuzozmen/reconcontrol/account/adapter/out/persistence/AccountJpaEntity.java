package com.yavuzozmen.reconcontrol.account.adapter.out.persistence;

import com.yavuzozmen.reconcontrol.account.domain.AccountStatus;
import com.yavuzozmen.reconcontrol.common.domain.CurrencyCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class AccountJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "account_number", nullable = false, unique = true, length = 64)
    private String accountNumber;

    @Column(name = "customer_id", nullable = false, length = 64)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code", nullable = false, length = 3)
    private CurrencyCode currency;

    @Column(name = "balance_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal balanceAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private AccountStatus status;

    protected AccountJpaEntity() {
    }

    AccountJpaEntity(
        UUID id,
        String accountNumber,
        String customerId,
        CurrencyCode currency,
        BigDecimal balanceAmount,
        AccountStatus status
    ) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.currency = currency;
        this.balanceAmount = balanceAmount;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public AccountStatus getStatus() {
        return status;
    }
}
