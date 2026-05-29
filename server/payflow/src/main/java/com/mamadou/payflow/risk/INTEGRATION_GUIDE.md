# Risk Module Integration Guide

## Overview

The PayFlow Risk Module provides comprehensive transaction risk assessment and monitoring capabilities. It evaluates transactions against configurable rules to detect suspicious patterns and flag high-risk activity.

## Architecture

### Components

1. **RiskEngine** - Orchestrates risk evaluation for transactions
2. **RiskRules** - Pluggable rules for detecting specific risk patterns
3. **RiskFlags** - Persistent tracking of flagged transactions
4. **RiskController** - REST API for risk operations

### Rule System

The risk module uses a pluggable architecture where each rule implements `RiskRule` interface:

```java
public interface RiskRule {
    RiskRuleResult evaluate(UUID walletId, Wallet wallet, BigDecimal transactionAmount, UUID transactionId);
    String getName();
}
```

Current rules:
- **DailyThresholdRule** - Detects high daily volumes and transaction frequency
- **RapidTransferRule** - Detects rapid sequential transfers
- **NewWalletHighValueRule** - Flags high-value transfers from newly created wallets

### Risk Scoring

Risk scores are aggregated from triggered rules:
- **CRITICAL** (≥9.0) - Blocks transaction by default
- **HIGH** (7.0-8.9) - Flags for review
- **MEDIUM** (5.0-6.9) - Monitored
- **LOW** (<5.0) - Allowed

## Integration Points

### 1. Transaction Service Integration

Evaluate risk before creating transactions:

```java
@Autowired
private RiskEngineService riskEngineService;

public void transferFunds(TransferRequest request) {
    // Evaluate risk
    RiskEvaluationResult riskResult = riskEngineService
        .evaluateTransactionRisk(
            request.getSourceWalletId(),
            request.getAmount(),
            UUID.randomUUID()
        );

    // Block if critical
    if (riskResult.shouldBlock()) {
        throw new TransactionBlockedException("Transaction blocked due to risk: " + riskResult.summary());
    }

    // Continue with transaction processing...
}
```

### 2. Notification Integration

Send alerts for high-risk flags:

```java
@Component
@Transactional
public class RiskNotificationListener {
    
    @Autowired
    private NotificationService notificationService;
    
    @PostPersist
    public void onRiskFlagCreated(RiskFlag flag) {
        if (flag.getRiskLevel() == RiskLevel.CRITICAL) {
            notificationService.notifyAdmin(
                "Critical risk flag created",
                flag.getId().toString()
            );
        }
    }
}
```

### 3. Audit Service Integration

Log risk evaluations for compliance:

```java
@Autowired
private AuditService auditService;

// In RiskEngineService.evaluateTransactionRisk():
auditService.logAction(
    walletId,
    "RISK_EVALUATION",
    "Risk evaluated: " + riskResult.summary()
);
```

## API Endpoints

### Evaluate Transaction Risk
```http
POST /api/v1/risk/evaluate
?walletId=UUID&amount=1000&transactionId=UUID
```

Response:
```json
{
  "success": true,
  "data": {
    "transactionId": "UUID",
    "walletId": "UUID",
    "riskLevel": "HIGH",
    "riskScore": 7.5,
    "triggeredRules": [
      {
        "ruleType": "RAPID_TRANSFER",
        "triggered": true,
        "riskScore": 7.5,
        "reason": "5 transfers in 300 seconds"
      }
    ],
    "shouldBlock": false,
    "summary": "Risk Level: HIGH | Triggered Rules: Rapid Sequential Transfer | ACTION: ALLOW"
  }
}
```

### Get Unresolved Flags
```http
GET /api/v1/risk/wallets/{walletId}/flags/unresolved
```

### Get Risk Summary
```http
GET /api/v1/risk/wallets/{walletId}/summary
```

### Resolve Risk Flag
```http
PUT /api/v1/risk/flags/{flagId}/resolve
?resolutionAction=APPROVED
```

## Configuration

Add to `application.yaml`:

```yaml
payflow:
  risk:
    # Risk scoring thresholds
    critical-threshold: 9.0
    high-threshold: 7.0
    medium-threshold: 5.0
    
    # Blocking configuration
    block-critical: true
    block-high: false
    
    # Daily threshold rule
    daily-threshold: 50000
    daily-transaction-count: 20
    
    # Rapid transfer rule (seconds)
    rapid-transfer-window-seconds: 300
    rapid-transfer-count: 5
    
    # New wallet rule (days)
    new-wallet-age-days: 7
    new-wallet-high-value-threshold: 10000
```

## Adding Custom Rules

1. Implement `RiskRule` interface:

```java
@Component
@RequiredArgsConstructor
public class MyCustomRule implements RiskRule {

    @Override
    public RiskRuleResult evaluate(UUID walletId, Wallet wallet, 
                                   BigDecimal amount, UUID transactionId) {
        // Your rule logic
        boolean triggered = /* check condition */;
        
        return new RiskRuleResult(
            RiskRuleType.CUSTOM_RULE,
            triggered,
            BigDecimal.valueOf(riskScore),
            "Your reason"
        );
    }

    @Override
    public String getName() {
        return "MyCustomRule";
    }
}
```

2. Add to `RiskRuleService`:

```java
@Component
@RequiredArgsConstructor
public class RiskRuleService {
    private final MyCustomRule myCustomRule;
    
    public List<RiskRule> getAllRules() {
        return List.of(
            dailyThresholdRule,
            rapidTransferRule,
            newWalletHighValueRule,
            myCustomRule  // Add your rule here
        );
    }
}
```

## Machine Learning Integration (Future)

The architecture supports ML model integration:

1. Create a `MLScoringRule` implementing `RiskRule`
2. Call your ML model in `evaluate()` method
3. Register in `RiskRuleService`
4. Update risk thresholds based on model output

## Testing

Run the risk module tests:

```bash
mvn test -Dtest=Risk*Test
```

## Monitoring

### Key Metrics
- Unresolved risk flags per wallet
- Critical flags by timestamp
- Rule trigger frequency
- Block rate by risk level

### Admin Dashboard Endpoints
- `GET /api/v1/risk/rules/stats` - Rule statistics
- `GET /api/v1/risk/config` - Engine configuration
- `GET /api/v1/risk/wallets/{walletId}/critical` - Critical flag check

## Security Considerations

1. **Rate Limiting** - Risk evaluation is fast (~10ms)
2. **Transactional Safety** - All risk flags are persisted atomically
3. **Audit Trail** - All risk operations are logged
4. **Access Control** - Risk operations require proper authorization
5. **Data Isolation** - Each wallet's risk data is isolated
