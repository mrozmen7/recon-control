package com.yavuzozmen.reconcontrol.infra.ratelimit;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rate-limit.transaction-create")
public class TransactionRateLimitProperties {

    private int limit = 5;
    private long windowSeconds = 60;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public long getWindowSeconds() {
        return windowSeconds;
    }

    public void setWindowSeconds(long windowSeconds) {
        this.windowSeconds = windowSeconds;
    }

    public Duration window() {
        return Duration.ofSeconds(windowSeconds);
    }
}
