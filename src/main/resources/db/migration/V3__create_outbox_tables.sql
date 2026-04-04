create table outbox_events (
    id uuid primary key,
    aggregate_type varchar(100) not null,
    aggregate_id uuid not null,
    topic varchar(255) not null,
    message_key varchar(255) not null,
    event_type varchar(150) not null,
    payload text not null,
    status varchar(30) not null,
    retry_count integer not null default 0,
    last_error text,
    created_at timestamptz not null,
    published_at timestamptz
);

create index idx_outbox_events_status_created_at
    on outbox_events (status, created_at);

create table processed_kafka_messages (
    id uuid primary key,
    consumer_name varchar(150) not null,
    message_id uuid not null,
    processed_at timestamptz not null,
    constraint uk_processed_kafka_messages_consumer_message
        unique (consumer_name, message_id)
);

create index idx_processed_kafka_messages_consumer
    on processed_kafka_messages (consumer_name);
