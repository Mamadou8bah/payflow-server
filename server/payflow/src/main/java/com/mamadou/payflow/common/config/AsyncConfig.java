package com.mamadou.payflow.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Async Configuration for concurrent operations.
 * Defines separate thread pools for different types of async operations:
 * - Risk evaluation: Parallel risk rule evaluation
 * - Reconciliation: Batch wallet reconciliation processing
 * - Webhook: Webhook validation and processing
 * - Transfer: Transfer execution operations
 * - General: Default async operations
 */
@Slf4j
@Configuration
public class AsyncConfig {

    /**
     * Task executor for risk rule evaluation (CPU-bound, parallelizable)
     * Each transaction can evaluate multiple risk rules in parallel
     */
    @Bean("riskEvaluationExecutor")
    public TaskExecutor riskEvaluationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("risk-eval-");
        executor.initialize();
        log.info("Risk evaluation thread pool initialized: core=4, max=8");
        return executor;
    }

    /**
     * Task executor for reconciliation operations (I/O-bound, batch processing)
     * Multiple wallets can be reconciled in parallel
     */
    @Bean("reconciliationExecutor")
    public TaskExecutor reconciliationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("reconciliation-");
        executor.initialize();
        log.info("Reconciliation thread pool initialized: core=8, max=16");
        return executor;
    }

    /**
     * Task executor for webhook operations (I/O-bound, validation and processing)
     * Multiple webhooks can be validated and processed in parallel
     */
    @Bean("webhookExecutor")
    public TaskExecutor webhookExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(6);
        executor.setMaxPoolSize(12);
        executor.setQueueCapacity(800);
        executor.setThreadNamePrefix("webhook-");
        executor.initialize();
        log.info("Webhook thread pool initialized: core=6, max=12");
        return executor;
    }

    /**
     * Task executor for transfer execution operations (I/O-bound, multiple service calls)
     * Ledger recording, wallet updates, and notifications can be parallelized
     */
    @Bean("transferExecutor")
    public TaskExecutor transferExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("transfer-");
        executor.initialize();
        log.info("Transfer thread pool initialized: core=10, max=20");
        return executor;
    }

    /**
     * Default task executor for general async operations (notifications, etc.)
     */
    @Bean("taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        log.info("Default async thread pool initialized: core=10, max=20");
        return executor;
    }
}
