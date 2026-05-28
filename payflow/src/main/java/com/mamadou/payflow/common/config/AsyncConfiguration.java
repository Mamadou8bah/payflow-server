package com.mamadou.payflow.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuration for async task execution.
 * Configures thread pools for different types of async operations.
 */
@Configuration
@Slf4j
public class AsyncConfiguration {

    /**
     * Thread pool executor for risk evaluation tasks.
     * Risk rule evaluation can be parallelized independently.
     */
    @Bean(name = "riskEvaluationExecutor")
    public TaskExecutor riskEvaluationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("risk-eval-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        log.info("Initialized riskEvaluationExecutor with corePoolSize=8, maxPoolSize=16");
        return executor;
    }

    /**
     * Thread pool executor for webhook processing tasks.
     * Webhooks should be processed asynchronously to avoid blocking.
     */
    @Bean(name = "webhookProcessingExecutor")
    public TaskExecutor webhookProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("webhook-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        log.info("Initialized webhookProcessingExecutor with corePoolSize=10, maxPoolSize=20");
        return executor;
    }

    /**
     * Thread pool executor for reconciliation tasks.
     * Batch reconciliation operations can be parallelized.
     */
    @Bean(name = "reconciliationExecutor")
    public TaskExecutor reconciliationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(12);
        executor.setMaxPoolSize(24);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("reconciliation-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(120);
        executor.initialize();
        log.info("Initialized reconciliationExecutor with corePoolSize=12, maxPoolSize=24");
        return executor;
    }

    /**
     * Default async task executor (used by @Async without a bean name).
     * Used for general async operations like notifications.
     */
    @Bean(name = "taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        log.info("Initialized default taskExecutor with corePoolSize=10, maxPoolSize=20");
        return executor;
    }
}
