package com.mamadou.payflow.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Redis Cache Configuration
 * Enables caching for frequently accessed data like wallets, transactions, risk flags
 * 
 * Redis configuration is handled via application.yaml:
 * - spring.redis.host
 * - spring.redis.port
 * - spring.cache.type: redis
 * - spring.cache.redis.time-to-live
 */
@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {
    
    public CacheConfig() {
        log.info("Redis Cache Configuration enabled");
    }
}
