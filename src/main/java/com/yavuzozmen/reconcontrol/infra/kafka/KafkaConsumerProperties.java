package com.yavuzozmen.reconcontrol.infra.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.consumer")
public record KafkaConsumerProperties(String fraudGroupId) {}
