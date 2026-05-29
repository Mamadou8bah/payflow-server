package com.mamadou.payflow.common.ratelimit;

import com.mamadou.payflow.auth.service.CurrentUserService;
import com.mamadou.payflow.common.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * HTTP interceptor for enforcing rate limits on annotated endpoints
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitingService rateLimitingService;
    private final CurrentUserService currentUserService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        
        RateLimit rateLimitAnnotation = method.getAnnotation(RateLimit.class);
        if (rateLimitAnnotation == null) {
            return true;
        }

        String operation = rateLimitAnnotation.value();
        if (operation.isEmpty()) {
            operation = method.getName();
        }

        // Check per-user rate limit
        if (rateLimitAnnotation.perUser()) {
            try {
                Long userId = currentUserService.getCurrentUserId();
                if (userId != null && !rateLimitingService.isUserAllowed(userId, operation)) {
                    return rejectRequest(response, rateLimitAnnotation.message());
                }
            } catch (Exception e) {
                log.debug("Could not extract user ID for rate limiting: {}", e.getMessage());
            }
        }

        // Check per-API-key rate limit
        if (rateLimitAnnotation.perApiKey()) {
            String apiKey = extractApiKey(request);
            if (apiKey != null && !rateLimitingService.isApiKeyAllowed(apiKey, operation)) {
                return rejectRequest(response, rateLimitAnnotation.message());
            }
        }

        return true;
    }

    private String extractApiKey(HttpServletRequest request) {
        // Check Authorization header: "Bearer <api-key>"
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        // Check X-API-Key header
        String apiKeyHeader = request.getHeader("X-API-Key");
        if (apiKeyHeader != null) {
            return apiKeyHeader;
        }
        
        return null;
    }

    private boolean rejectRequest(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Retry-After", "60");

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("RATE_LIMIT_EXCEEDED")
                .message(message)
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        return false;
    }
}
