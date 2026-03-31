create table fraud_cases (
    id uuid primary key,
    source_event_id uuid not null,
    transaction_id uuid not null,
    account_id uuid not null,
    reference_no varchar(120) not null,
    rule_code varchar(120) not null,
    severity varchar(30) not null,
    status varchar(30) not null,
    reason varchar(500) not null,
    created_at timestamptz not null,
    reviewed_at timestamptz,
    constraint uk_fraud_cases_event_rule unique (source_event_id, rule_code)
);

create index idx_fraud_cases_transaction_id
    on fraud_cases (transaction_id);

create index idx_fraud_cases_created_at
    on fraud_cases (created_at desc);
