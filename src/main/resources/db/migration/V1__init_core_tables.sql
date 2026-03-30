CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    account_number VARCHAR(64) NOT NULL UNIQUE,
    customer_id VARCHAR(64) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    balance_amount NUMERIC(19, 4) NOT NULL,
    status VARCHAR(16) NOT NULL
);

CREATE INDEX idx_accounts_customer_id ON accounts (customer_id);

CREATE TABLE internal_transactions (
    id UUID PRIMARY KEY,
    reference_no VARCHAR(64) NOT NULL UNIQUE,
    account_id UUID NOT NULL,
    transaction_type VARCHAR(16) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    value_date DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    status VARCHAR(32) NOT NULL,
    CONSTRAINT fk_internal_transactions_account
        FOREIGN KEY (account_id) REFERENCES accounts (id)
);

CREATE INDEX idx_internal_transactions_account_id
    ON internal_transactions (account_id);

CREATE INDEX idx_internal_transactions_status
    ON internal_transactions (status);
