package com.mamadou.payflow.common.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting filter using Bucket4j token bucket algorithm.
 * Implements per-IP rate limiting for authentication endpoints with distributed-ready design.
 * 
 * Configuration:
 * - 10 requests per minute per IP for login/register endpoints
 * - 20 requests per minute per IP for refresh endpoint
 */
@Component
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    @Value("${payflow.ratelimit.login.requests:10}")
    private int loginRequestsPerMinute;

    @Value("${payflow.ratelimit.refresh.requests:20}")
    private int refreshRequestsPerMinute;

    // Thread-safe map of Bucket4j buckets per IP address
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        
        // Only apply rate limiting to auth endpoints
        if (isRateLimitedEndpoint(requestPath)) {
            String clientIp = getClientIp(request);
            long rateLimit = getRateLimitForEndpoint(requestPath);
            
            Bucket bucket = bucketCache.computeIfAbsent(clientIp, ip -> createBucket(rateLimit));
            
            if (!bucket.tryConsume(1)) {
                // Rate limit exceeded
                log.warn("Rate limit exceeded for IP: {} on endpoint: {}", clientIp, requestPath);
                
                response.setStatus(429); // TOO_MANY_REQUESTS
                response.setContentType("application/json");
                response.getWriter().write(
                    "{\"success\":false,\"message\":\"Rate limit exceeded. Try again in 1 minute.\"}"
                );
                return;
            }
            
            log.debug("Rate limit check passed for IP: {} on endpoint: {}", clientIp, requestPath);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRateLimitedEndpoint(String path) {
        return path.contains("/api/auth/login") || 
               path.contains("/api/auth/register") || 
               path.contains("/api/auth/refresh");
    }

    private long getRateLimitForEndpoint(String requestPath) {
        return requestPath.contains("/refresh") ? refreshRequestsPerMinute : loginRequestsPerMinute;
    }

    private Bucket createBucket(long requestsPerMinute) {
        Bandwidth limit = Bandwidth.classic(requestsPerMinute, Refill.intervally(requestsPerMinute, Duration.ofMinutes(1)));
        return Bucket4j.builder()
            .addLimit(limit)
            .build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
