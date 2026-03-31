package com.yavuzozmen.reconcontrol.infra.ratelimit;

import java.time.Duration;

public interface RateLimitStore {

    long incrementAndGet(String key, Duration window);
}
