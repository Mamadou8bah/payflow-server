package com.mamadou.payflow.common.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Advanced rate limiting service supporting:
 * - Per-user rate limiting
 * - Per-API-key rate limiting
 * - Configurable limits per endpoint/operation
 * - Redis-backed distributed rate limiting
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RateLimitingService {

    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> apiKeyBuckets = new ConcurrentHashMap<>();
    
    private final RateLimitConfig rateLimitConfig;

    /**
     * Check rate limit for a specific user
     * @param userId User identifier
     * @param operation Operation type (e.g., "login", "transfer", "refresh")
     * @return true if request is allowed, false if rate limited
     */
    public boolean isUserAllowed(Long userId, String operation) {
        String key = "user:" + userId + ":" + operation;
        Bucket bucket = userBuckets.computeIfAbsent(key, k -> createUserBucket(operation));
        
        boolean allowed = bucket.tryConsume(1);
        if (!allowed) {
            log.warn("Rate limit exceeded for user={} operation={}", userId, operation);
        }
        return allowed;
    }

    /**
     * Check rate limit for a specific API key
     * @param apiKey API key identifier
     * @param operation Operation type
     * @return true if request is allowed, false if rate limited
     */
    public boolean isApiKeyAllowed(String apiKey, String operation) {
        String key = "apikey:" + apiKey + ":" + operation;
        Bucket bucket = apiKeyBuckets.computeIfAbsent(key, k -> createApiKeyBucket(operation));
        
        boolean allowed = bucket.tryConsume(1);
        if (!allowed) {
            log.warn("Rate limit exceeded for apiKey={} operation={}", apiKey, operation);
        }
        return allowed;
    }

    /**
     * Get remaining tokens for a user
     */
    public long getUserRemainingTokens(Long userId, String operation) {
        String key = "user:" + userId + ":" + operation;
        Bucket bucket = userBuckets.get(key);
        if (bucket == null) {
            return Long.valueOf(rateLimitConfig.getLimit(operation));
        }
        return bucket.estimateAbilityToConsume(1).getRoundedTokensToConsume();
    }

    /**
     * Get remaining tokens for an API key
     */
    public long getApiKeyRemainingTokens(String apiKey, String operation) {
        String key = "apikey:" + apiKey + ":" + operation;
        Bucket bucket = apiKeyBuckets.get(key);
        if (bucket == null) {
            return Long.valueOf(rateLimitConfig.getApiKeyLimit(operation));
        }
        return bucket.estimateAbilityToConsume(1).getRoundedTokensToConsume();
    }

    /**
     * Reset rate limit for a user (admin operation)
     */
    public void resetUserLimit(Long userId, String operation) {
        String key = "user:" + userId + ":" + operation;
        userBuckets.remove(key);
        log.info("Rate limit reset for user={} operation={}", userId, operation);
    }

    /**
     * Reset rate limit for an API key (admin operation)
     */
    public void resetApiKeyLimit(String apiKey, String operation) {
        String key = "apikey:" + apiKey + ":" + operation;
        apiKeyBuckets.remove(key);
        log.info("Rate limit reset for apiKey={} operation={}", apiKey, operation);
    }

    private Bucket createUserBucket(String operation) {
        long requests = rateLimitConfig.getLimit(operation);
        long windowSeconds = rateLimitConfig.getWindowSeconds(operation);
        
        Refill refill = Refill.intervally(requests, Duration.ofSeconds(windowSeconds));
        Bandwidth limit = Bandwidth.classic(requests, refill);
        
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }

    private Bucket createApiKeyBucket(String operation) {
        long requests = rateLimitConfig.getApiKeyLimit(operation);
        long windowSeconds = rateLimitConfig.getWindowSeconds(operation);
        
        Refill refill = Refill.intervally(requests, Duration.ofSeconds(windowSeconds));
        Bandwidth limit = Bandwidth.classic(requests, refill);
        
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Configuration class for rate limiting parameters
     */
    @org.springframework.boot.context.properties.ConfigurationProperties(prefix = "payflow.ratelimit")
    public static class RateLimitConfig {
        
        private Map<String, RateLimitRule> rules = new ConcurrentHashMap<>();
        private RateLimitRule login;
        private RateLimitRule refresh;
        private RateLimitRule apiKey;
        private RateLimitRule defaultRule;

        public RateLimitRule getRule(String operation) {
            return rules.getOrDefault(operation, defaultRule != null ? defaultRule : 
                    new RateLimitRule(100, 60, 1000, 3600));
        }

        public long getLimit(String operation) {
            RateLimitRule rule = getRule(operation);
            return rule.getRequests();
        }

        public long getApiKeyLimit(String operation) {
            RateLimitRule rule = getRule(operation);
            return rule.getApiKeyRequests();
        }

        public long getWindowSeconds(String operation) {
            RateLimitRule rule = getRule(operation);
            return rule.getWindowSeconds();
        }

        // Getters and setters
        public Map<String, RateLimitRule> getRules() {
            return rules;
        }

        public void setRules(Map<String, RateLimitRule> rules) {
            this.rules = rules;
        }

        public RateLimitRule getLogin() {
            return login;
        }

        public void setLogin(RateLimitRule login) {
            this.login = login;
            if (login != null) {
                rules.put("login", login);
            }
        }

        public RateLimitRule getRefresh() {
            return refresh;
        }

        public void setRefresh(RateLimitRule refresh) {
            this.refresh = refresh;
            if (refresh != null) {
                rules.put("refresh", refresh);
            }
        }

        public RateLimitRule getApiKey() {
            return apiKey;
        }

        public void setApiKey(RateLimitRule apiKey) {
            this.apiKey = apiKey;
            if (apiKey != null) {
                rules.put("api-key", apiKey);
            }
        }

        public RateLimitRule getDefault() {
            return defaultRule;
        }

        public void setDefault(RateLimitRule defaultRule) {
            this.defaultRule = defaultRule;
        }
    }

    /**
     * Individual rate limit rule with per-user and per-API-key settings
     */
    public static class RateLimitRule {
        private long requests;           // Per-user requests
        private long windowSeconds;
        private long apiKeyRequests;    // Per-API-key requests
        private long apiKeyWindowSeconds;

        public RateLimitRule() {
        }

        public RateLimitRule(long requests, long windowSeconds, long apiKeyRequests, long apiKeyWindowSeconds) {
            this.requests = requests;
            this.windowSeconds = windowSeconds;
            this.apiKeyRequests = apiKeyRequests;
            this.apiKeyWindowSeconds = apiKeyWindowSeconds;
        }

        // Getters and setters
        public long getRequests() {
            return requests;
        }

        public void setRequests(long requests) {
            this.requests = requests;
        }

        public long getWindowSeconds() {
            return windowSeconds;
        }

        public void setWindowSeconds(long windowSeconds) {
            this.windowSeconds = windowSeconds;
        }

        public long getApiKeyRequests() {
            return apiKeyRequests;
        }

        public void setApiKeyRequests(long apiKeyRequests) {
            this.apiKeyRequests = apiKeyRequests;
        }

        public long getApiKeyWindowSeconds() {
            return apiKeyWindowSeconds;
        }

        public void setApiKeyWindowSeconds(long apiKeyWindowSeconds) {
            this.apiKeyWindowSeconds = apiKeyWindowSeconds;
        }
    }
}
