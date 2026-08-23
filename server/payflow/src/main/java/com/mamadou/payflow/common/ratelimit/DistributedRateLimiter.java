package com.mamadou.payflow.common.ratelimit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Redis-backed token bucket approximation for distributed rate limiting across replicas.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedRateLimiter {

    private static final String KEY_PREFIX = "payflow:ratelimit:";

    private final StringRedisTemplate redisTemplate;

    public boolean tryConsume(String bucketKey, long limit, Duration window) {
        String redisKey = KEY_PREFIX + bucketKey;
        try {
            Long count = redisTemplate.opsForValue().increment(redisKey);
            if (count == null) {
                return true;
            }
            if (count == 1L) {
                redisTemplate.expire(redisKey, window);
            }
            boolean allowed = count <= limit;
            if (!allowed) {
                log.warn("Rate limit exceeded for key={} count={} limit={}", bucketKey, count, limit);
            }
            return allowed;
        } catch (Exception ex) {
            log.error("Redis rate limit check failed for key={}: {}", bucketKey, ex.getMessage());
            // Fail open on Redis errors so auth is not fully blocked by cache outages.
            return true;
        }
    }
}
