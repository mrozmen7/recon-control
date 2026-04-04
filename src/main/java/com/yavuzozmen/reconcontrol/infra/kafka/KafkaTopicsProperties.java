package com.yavuzozmen.reconcontrol.infra.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.topics")
public record KafkaTopicsProperties(
    String transactionEvents,
    String transactionEventsDlt,
    String fraudAlertEvents
) {}
