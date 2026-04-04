package com.yavuzozmen.reconcontrol.infra.outbox;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.outbox.publisher")
public record OutboxPublisherProperties(long delayMs, int batchSize) {}
