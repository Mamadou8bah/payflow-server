package com.mamadou.payflow.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Utility class for concurrent batch processing operations
 * Provides common patterns for processing large datasets in parallel batches
 */
@Slf4j
public class ConcurrentBatchProcessor {

    /**
     * Process items in parallel batches
     * 
     * @param items the items to process
     * @param batchSize the size of each batch
     * @param processor the processor function for each item
     * @param executor the task executor for parallel processing
     * @param <T> the type of items
     * @param <R> the type of results
     * @return list of processed results
     */
    public static <T, R> List<R> processBatchesAsync(
            List<T> items,
            int batchSize,
            java.util.function.Function<T, R> processor,
            TaskExecutor executor) {
        
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }

        // Use synchronizedList for thread safety during concurrent batch processing
        List<R> results = Collections.synchronizedList(new ArrayList<>());
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // Submit each batch for processing
        for (int i = 0; i < items.size(); i += batchSize) {
            int end = Math.min(i + batchSize, items.size());
            List<T> batch = items.subList(i, end);

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                batch.stream()
                    .map(processor)
                    .forEach(results::add);
            }, executor);
            futures.add(future);
        }

        // Wait for all batches to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return new ArrayList<>(results);
    }

    /**
     * Process items in parallel batches with side effects
     * 
     * @param items the items to process
     * @param batchSize the size of each batch
     * @param processor the processor function (with side effects)
     * @param executor the task executor for parallel processing
     * @param <T> the type of items
     */
    public static <T> void processBatchesAsyncVoid(
            List<T> items,
            int batchSize,
            Consumer<T> processor,
            TaskExecutor executor) {
        
        if (items == null || items.isEmpty()) {
            return;
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // Submit each batch for processing
        for (int i = 0; i < items.size(); i += batchSize) {
            int end = Math.min(i + batchSize, items.size());
            List<T> batch = items.subList(i, end);

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                batch.forEach(processor);
            }, executor);
            futures.add(future);
        }

        // Wait for all batches to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    /**
     * Process items in parallel with a maximum concurrency level
     * Useful for limiting concurrent DB connections or API calls
     * 
     * @param items the items to process
     * @param maxConcurrency the maximum number of concurrent operations
     * @param processor the processor function for each item
     * @param executor the task executor for parallel processing
     * @param <T> the type of items
     * @param <R> the type of results
     * @return list of processed results
     */
    public static <T, R> List<R> processWithConcurrencyLimit(
            List<T> items,
            int maxConcurrency,
            java.util.function.Function<T, R> processor,
            TaskExecutor executor) {
        
        return processBatchesAsync(items, maxConcurrency, processor, executor);
    }

    /**
     * Partition items into chunks for parallel processing
     * 
     * @param items the items to partition
     * @param chunkSize the size of each chunk
     * @param <T> the type of items
     * @return list of partitioned chunks
     */
    public static <T> List<List<T>> partition(List<T> items, int chunkSize) {
        List<List<T>> partitions = new ArrayList<>();
        
        for (int i = 0; i < items.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, items.size());
            partitions.add(items.subList(i, end));
        }
        
        return partitions;
    }

    /**
     * Retry operation with exponential backoff
     * 
     * @param operation the operation to retry
     * @param maxAttempts the maximum number of attempts
     * @param initialDelayMs the initial delay in milliseconds
     * @param <R> the type of result
     * @return the result of the operation
     */
    public static <R> R retryWithBackoff(
            java.util.function.Supplier<R> operation,
            int maxAttempts,
            long initialDelayMs) {
        
        long delay = initialDelayMs;
        RuntimeException lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return operation.get();
            } catch (RuntimeException e) {
                lastException = e;
                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep(delay);
                        delay *= 2; // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
            }
        }

        throw lastException;
    }
}
