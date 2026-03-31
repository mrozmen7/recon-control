package com.yavuzozmen.reconcontrol.infra.ratelimit;

public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException(String actor, int limit, long windowSeconds) {
        super(
            "rate limit exceeded for actor '" + actor + "' with limit "
                + limit + " requests per " + windowSeconds + " seconds"
        );
    }
}
