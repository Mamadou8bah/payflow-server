package com.mamadou.payflow.common.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Redis Health Indicator
 * Monitors Redis connection and reports health status
 * Access via: GET /actuator/health/redis
 * 
 * Note: Health indicator functionality is disabled in this version.
 * Actuator health checks are not required for concurrency implementation.
 */
@Slf4j
@Component
public class RedisHealthIndicator {

    private final RedisConnectionFactory connectionFactory;

    public RedisHealthIndicator(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        log.info("Redis Health Indicator initialized");
    }

    /**
     * Check Redis connection health (manual check)
     */
    public boolean isRedisHealthy() {
        try {
            var connection = connectionFactory.getConnection();
            if (connection != null) {
                connection.ping();
                connection.close();
                log.debug("Redis health check passed");
                return true;
            } else {
                log.warn("Redis connection is null");
                return false;
            }
        } catch (Exception e) {
            log.error("Redis health check failed", e);
            return false;
        }
    }
}
