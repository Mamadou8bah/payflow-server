package com.mamadou.payflow.common.config;

import com.mamadou.payflow.common.metrics.PayFlowMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Initializes PayFlow metrics on application startup
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsInitializer {

    private final PayFlowMetricsService metricsService;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeMetrics() {
        log.info("Initializing PayFlow metrics...");
        metricsService.initialize();
        log.info("PayFlow metrics initialized successfully");
    }
}
