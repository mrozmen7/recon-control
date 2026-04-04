package com.yavuzozmen.reconcontrol.infra.ratelimit;

import java.util.Objects;

public class TransactionCreateRateLimiter {

    private final RateLimitStore rateLimitStore;
    private final TransactionRateLimitProperties properties;

    public TransactionCreateRateLimiter(
        RateLimitStore rateLimitStore,
        TransactionRateLimitProperties properties
    ) {
        this.rateLimitStore = Objects.requireNonNull(
            rateLimitStore,
            "rateLimitStore must not be null"
        );
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
    }

    public void checkLimit(String actor) {
        String normalizedActor = actor == null || actor.isBlank() ? "anonymous" : actor;
        long currentCount = rateLimitStore.incrementAndGet(
            "recon-control:ratelimit:transaction-create:" + normalizedActor,
            properties.window()
        );

        if (currentCount > properties.getLimit()) {
            throw new RateLimitExceededException(
                normalizedActor,
                properties.getLimit(),
                properties.getWindowSeconds()
            );
        }
    }
}
