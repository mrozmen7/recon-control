package com.yavuzozmen.reconcontrol.infra.idempotency;

import com.yavuzozmen.reconcontrol.transaction.application.IdempotencyRecord;
import com.yavuzozmen.reconcontrol.transaction.application.IdempotencyStatus;
import com.yavuzozmen.reconcontrol.transaction.application.port.out.TransactionIdempotencyStore;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class RedisTransactionIdempotencyStore implements TransactionIdempotencyStore {

    private static final String PROCESSING = "PROCESSING";
    private static final String COMPLETED_PREFIX = "COMPLETED:";

    private final StringRedisTemplate redisTemplate;

    public RedisTransactionIdempotencyStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<IdempotencyRecord> find(String key) {
        String value = redisTemplate.opsForValue().get(redisKey(key));
        if (value == null) {
            return Optional.empty();
        }

        if (PROCESSING.equals(value)) {
            return Optional.of(new IdempotencyRecord(IdempotencyStatus.PROCESSING, null));
        }

        if (value.startsWith(COMPLETED_PREFIX)) {
            return Optional.of(
                new IdempotencyRecord(
                    IdempotencyStatus.COMPLETED,
                    UUID.fromString(value.substring(COMPLETED_PREFIX.length()))
                )
            );
        }

        throw new IllegalStateException("unknown idempotency record value: " + value);
    }

    @Override
    public boolean markProcessing(String key, Duration ttl) {
        Boolean created = redisTemplate.opsForValue()
            .setIfAbsent(redisKey(key), PROCESSING, ttl);
        return Boolean.TRUE.equals(created);
    }

    @Override
    public void markCompleted(String key, UUID transactionId, Duration ttl) {
        redisTemplate.opsForValue()
            .set(redisKey(key), COMPLETED_PREFIX + transactionId, ttl);
    }

    @Override
    public void clear(String key) {
        redisTemplate.delete(redisKey(key));
    }

    private String redisKey(String key) {
        return "recon-control:idempotency:transaction-create:" + key;
    }
}
