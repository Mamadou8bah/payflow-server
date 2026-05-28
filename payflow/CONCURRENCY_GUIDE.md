# Payflow Concurrency Implementation Guide

## Overview
This document outlines the concurrency improvements implemented in the Payflow Spring Boot application to handle high-throughput payment operations efficiently.

## Architecture Components

### 1. AsyncConfig (common/config/AsyncConfig.java)
Central configuration for all async thread pools with dedicated executors for different operation types:

- **riskEvaluationExecutor**: Core=4, Max=8
  - Parallel risk rule evaluation (CPU-bound)
  - Each transaction evaluates multiple independent rules concurrently
  
- **reconciliationExecutor**: Core=8, Max=16
  - Batch wallet reconciliation processing
  - Parallel wallet-to-ledger consistency checks
  
- **webhookExecutor**: Core=6, Max=12
  - Webhook validation and processing
  - Parallel signature validation and event processing
  
- **transferExecutor**: Core=10, Max=20
  - Transfer execution operations
  - Parallel ledger recording and wallet updates
  
- **taskExecutor**: Core=10, Max=20 (default)
  - General async operations (notifications, etc.)
  - Configured with graceful shutdown

## Service Improvements

### 2. RiskEngineService (risk/service/RiskEngineService.java)
**Improvement**: Parallel risk rule evaluation

**Before**: Sequential loop evaluating each rule (10+ rules = 10+ sequential database queries)
```java
for (RiskRule rule : riskRuleService.getAllRules()) {
    RiskRuleResult result = rule.evaluate(...);
}
```

**After**: Parallel evaluation with CompletableFuture
```java
evaluateRulesInParallel() {
    futures = rules.stream()
        .map(rule -> CompletableFuture.supplyAsync(() -> rule.evaluate(...), riskEvaluationExecutor))
        .collect(toList());
    return futures.stream().map(CompletableFuture::join).collect(toList());
}
```

**Benefits**:
- Reduces rule evaluation time from 10N to ~N (where N = number of rules)
- Better CPU utilization
- Fallback to sequential evaluation on error

### 3. WalletReconciliationService (reconciliation/service/WalletReconciliationService.java)
**Improvement**: Batch parallel wallet processing

**Before**: Sequential wallet-by-wallet processing
```java
for (Wallet wallet : wallets) {
    checkWalletConsistency(wallet);
}
```

**After**: Batch parallel processing
```java
processBatches(wallets, batch -> {
    futures = batch.stream()
        .map(wallet -> CompletableFuture.supplyAsync(() -> reconcile(wallet), reconciliationExecutor))
        .collect(toList());
    futures.stream().map(CompletableFuture::join).forEach(process);
});
```

**Benefits**:
- Processes multiple wallets simultaneously (batch size: 100)
- Reduced reconciliation time from hours to minutes for large datasets
- Controlled concurrency prevents database connection exhaustion

### 4. WebhookService (webhook/service/WebhookService.java)
**Improvement**: Async webhook validation and processing

**Before**: Synchronous signature validation blocking webhook processing
```java
if (!webhookValidationService.hasValidSignature(payload, headers)) {
    // Handle failure
}
depositProcessingService.process(event);
```

**After**: Async validation with CompletableFuture
```java
CompletableFuture.supplyAsync(() -> 
    webhookValidationService.hasValidSignature(payload, headers), webhookExecutor)
    .thenApply(isValid -> processIfValid(isValid))
    .join();
```

**New Methods**:
- `handleModemPayDepositAsync()`: Fire-and-forget webhook processing
- Parallel validation + processing reduces latency

**Benefits**:
- Non-blocking webhook validation
- Can process multiple webhooks in parallel
- Optional async version for high-throughput scenarios

### 5. TransferExecutionService (transfer/service/TransferExecutionService.java)
**Improvement**: Parallel execution of independent operations

**Before**: Sequential ledger recording and wallet limit tracking
```java
LedgerTraceResponse trace = ledgerService.recordFinancialTransaction(...);
walletLimitService.trackDebitUsage(...);
```

**After**: Parallel independent operations
```java
LedgerTraceResponse trace = recordLedgerTransactionAsync(...).join();
trackWalletLimitAsync(sourceWallet, amount); // Fire-and-forget
```

**New Methods**:
- `recordLedgerTransactionAsync()`: Non-blocking ledger recording
- `trackWalletLimitAsync()`: Fire-and-forget wallet limit tracking

**Benefits**:
- Ledger recording and wallet updates happen in parallel
- Reduced transfer execution time
- Non-critical wallet tracking doesn't block main transaction

### 6. LedgerPostingService (ledger/service/LedgerPostingService.java)
**Improvement**: Optimized account lookups with caching

**Before**: Separate database query for each account lookup
```java
for (Entry entry : request.getPostings()) {
    LedgerAccount account = ledgerAccountService.getAccountEntity(entry.getAccountCode());
}
```

**After**: Local cache within batch transaction
```java
LedgerAccount account = accountCache.computeIfAbsent(accountCode, code ->
    ledgerAccountService.getAccountEntity(code)
);
```

**Benefits**:
- Eliminates duplicate database queries within a batch
- Reduces database load significantly
- Thread-safe ConcurrentHashMap implementation

### 7. AuditLogService (audit/service/AuditLogService.java)
**Improvement**: Async and batch audit logging

**New Methods**:
- `createLogAsync()`: Non-blocking audit logging (fire-and-forget)
- `createLogsBatch()`: Batch save multiple audit logs
- `createLogsBatchAsync()`: Batch async logging

**Benefits**:
- Audit logging doesn't block main operations
- Reduced database transactions for multiple log entries
- Better performance for high-volume audit trails

## Utility Classes

### 8. ConcurrentBatchProcessor (common/util/ConcurrentBatchProcessor.java)
Reusable utilities for concurrent batch operations:

- `processBatchesAsync()`: Process items with transformation in parallel batches
- `processBatchesAsyncVoid()`: Process items with side effects in parallel batches
- `processWithConcurrencyLimit()`: Control max concurrent operations
- `partition()`: Divide items into chunks
- `retryWithBackoff()`: Retry operations with exponential backoff

## Configuration & Thread Pool Sizing

### Thread Pool Sizing Strategy

**Risk Evaluation (4-8 threads)**:
- CPU-bound operations (rule evaluation)
- Smaller pool since mainly CPU time
- 4 core threads handle typical load
- 8 max threads for spike handling

**Reconciliation (8-16 threads)**:
- I/O-bound batch processing
- Larger pool for database throughput
- 8 core threads for baseline
- 16 max for peak reconciliation periods

**Webhook (6-12 threads)**:
- Mixed I/O and validation
- 6 core threads handle typical webhook load
- 12 max for burst webhook events

**Transfer (10-20 threads)**:
- High-throughput transfer processing
- Largest pool for throughput
- 10 core threads baseline
- 20 max for peak transfer volumes

**Default/General (10-20 threads)**:
- Notifications and other async tasks
- Queue capacity: 1000 tasks
- Graceful shutdown: 60 seconds

## Concurrency Patterns Used

### 1. CompletableFuture for Async Operations
```java
CompletableFuture.supplyAsync(() -> operation(), executor)
    .thenApply(result -> nextOperation(result))
    .join();
```

### 2. Parallel Batch Processing
```java
for (int i = 0; i < items.size(); i += batchSize) {
    List<CompletableFuture<R>> futures = batch.stream()
        .map(item -> CompletableFuture.supplyAsync(() -> process(item), executor))
        .collect(toList());
    futures.stream().map(CompletableFuture::join).forEach(handleResult);
}
```

### 3. Local Thread-Safe Caching
```java
ConcurrentMap<K, V> cache = new ConcurrentHashMap<>();
V value = cache.computeIfAbsent(key, k -> fetchFromDB(k));
```

### 4. Fire-and-Forget Async
```java
CompletableFuture.runAsync(() -> {
    try {
        nonCriticalOperation();
    } catch (Exception e) {
        log.error("Error", e);
    }
}, executor);
```

### 5. @Async Annotation for Simple Async
```java
@Async
public void sendNotificationAsync(NotificationRequest request) {
    // Executed in task executor thread pool
}
```

## Performance Impact

### Expected Improvements

| Operation | Before | After | Improvement |
|-----------|--------|-------|------------|
| Risk evaluation (10 rules) | ~500ms | ~100ms | 5x faster |
| Wallet reconciliation (1000 wallets) | ~60s | ~5s | 12x faster |
| Transfer execution | ~200ms | ~120ms | 1.67x faster |
| Webhook processing (burst 100) | Sequential | Parallel | ~8x throughput |
| Batch audit logging (100 logs) | 100 txns | 1 txn | 100x faster |

### Resource Usage

- **CPU**: Better utilization through parallelization
- **Memory**: Minimal increase (thread pools + task queues)
- **Database**: Reduced connection pool pressure (batching)
- **Throughput**: 5-12x improvement on bulk operations

## Best Practices

### 1. Use Appropriate Executor
```java
// Risk operations → riskEvaluationExecutor
// Reconciliation → reconciliationExecutor
// Webhooks → webhookExecutor
// Transfers → transferExecutor
```

### 2. Handle Exceptions in Async
```java
CompletableFuture.supplyAsync(operation, executor)
    .exceptionally(ex -> {
        log.error("Error", ex);
        return fallbackValue();
    });
```

### 3. Don't Block Unnecessarily
```java
// Good - non-critical operation doesn't block
CompletableFuture.runAsync(() -> nonCritical(), executor);

// If result needed - use join()
R result = CompletableFuture.supplyAsync(operation, executor).join();
```

### 4. Batch Related Operations
```java
// Instead of multiple single saves
auditLogService.createLogsBatch(auditLogs);
ledgerPostingRepository.saveAll(postings);
```

### 5. Monitor Thread Pools
```java
log.info("Risk evaluation thread pool initialized: core=4, max=8");
// Monitor queue size and rejection rate in production
```

## Monitoring & Troubleshooting

### Key Metrics to Monitor

1. **Thread Pool Metrics**:
   - Active threads count
   - Queue size
   - Task rejection rate
   - Execution time per task

2. **Performance Metrics**:
   - Risk evaluation time (target: < 200ms)
   - Reconciliation time (target: < 30s per 1000 wallets)
   - Transfer execution time (target: < 500ms)
   - Webhook processing latency (target: < 100ms)

3. **Resource Metrics**:
   - Database connection pool usage
   - Memory usage (heap)
   - Thread count

### Common Issues & Solutions

**Issue**: Thread pool rejections
- **Cause**: Too many queued tasks
- **Solution**: Increase core pool size or reduce batch size

**Issue**: High memory usage
- **Cause**: Large queue capacity with many pending tasks
- **Solution**: Reduce queue capacity or process smaller batches

**Issue**: Slow database queries with parallel threads
- **Cause**: Connection pool exhaustion
- **Solution**: Increase database connection pool size

**Issue**: Deadlocks in nested transactions
- **Cause**: Multiple threads acquiring locks in different order
- **Solution**: Use ordered locking or reduce transaction scope

## Future Enhancements

1. **Reactive Programming**: Convert to Spring WebFlux for true async/non-blocking
2. **Scheduling**: Async scheduled reconciliation during off-peak hours
3. **Circuit Breaker**: Add resilience4j for external service calls
4. **Message Queue**: Use RabbitMQ/Kafka for webhook and notification processing
5. **Virtual Threads**: Java 21+ virtual threads for higher concurrency
6. **Distributed Tracing**: Correlate logs across async threads with OpenTelemetry

## Rollback Plan

If concurrency implementation causes issues:

1. Disable specific executors in AsyncConfig
2. Revert service methods to sequential versions
3. Disable @Async methods (returns to sequential)
4. Monitor and gradually re-enable after fixes

## Testing Concurrency

### Unit Tests
```java
@Test
void testParallelRuleEvaluation() {
    RiskEvaluationResult result = riskEngineService.evaluateTransactionRisk(...);
    assertEquals(RiskLevel.HIGH, result.getRiskLevel());
}
```

### Load Tests
```
Load Testing: 1000 concurrent transfers
Expected: ~50% reduction in response time
Monitor: Thread pool utilization, database connections
```

### Stress Tests
```
Monitor behavior under:
- 10x normal load
- Thread pool exhaustion scenarios
- Database connection pool exhaustion
```
