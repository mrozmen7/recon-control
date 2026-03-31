package com.yavuzozmen.reconcontrol.infra.idempotency;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.idempotency.transaction-create")
public class TransactionIdempotencyProperties {

    private long ttlHours = 24;

    public long getTtlHours() {
        return ttlHours;
    }

    public void setTtlHours(long ttlHours) {
        this.ttlHours = ttlHours;
    }

    public Duration ttl() {
        return Duration.ofHours(ttlHours);
    }
}
