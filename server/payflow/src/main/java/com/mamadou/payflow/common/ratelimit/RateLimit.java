package com.mamadou.payflow.common.ratelimit;

import java.lang.annotation.*;

/**
 * Annotation to apply rate limiting to controller methods
 * Can limit based on user, API key, or operation type
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    
    /**
     * Operation type for rate limiting configuration lookup
     * Default: method name
     */
    String value() default "";
    
    /**
     * Apply per-user rate limiting
     */
    boolean perUser() default true;
    
    /**
     * Apply per-API-key rate limiting
     */
    boolean perApiKey() default false;
    
    /**
     * Custom rate limit key (e.g., "user_id", "api_key")
     */
    String key() default "user";
    
    /**
     * Error message when rate limit exceeded
     */
    String message() default "Rate limit exceeded. Please try again later.";
}
