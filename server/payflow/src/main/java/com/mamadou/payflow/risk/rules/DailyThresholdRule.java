package com.mamadou.payflow.risk.rules;

import com.mamadou.payflow.risk.dto.RiskRuleResult;
import com.mamadou.payflow.risk.enums.RiskRuleType;
import com.mamadou.payflow.risk.repository.RiskFlagRepository;
import com.mamadou.payflow.transaction.repository.TransactionRepository;
import com.mamadou.payflow.wallet.entity.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Rule to detect transactions exceeding daily thresholds per wallet.
 * Flags high-value transactions or patterns that cross configured limits.
 */
@Component
@RequiredArgsConstructor
public class DailyThresholdRule implements RiskRule {

    private final TransactionRepository transactionRepository;
    private final RiskFlagRepository riskFlagRepository;

    @Value("${payflow.risk.daily-threshold:50000}")
    private BigDecimal dailyThresholdAmount;

    @Value("${payflow.risk.daily-transaction-count:20}")
    private int dailyTransactionCountThreshold;

    @Override
    public RiskRuleResult evaluate(Long walletId, Wallet wallet, BigDecimal transactionAmount, Long transactionId) {
        try {
            LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

            BigDecimal dailyVolume = transactionRepository.sumTransactionAmountByWalletAndDateRange(
                walletId, startOfDay, endOfDay
            ).orElse(BigDecimal.ZERO);

            BigDecimal totalWithCurrent = dailyVolume.add(transactionAmount);

            long dailyTransactionCount = transactionRepository.countByWalletAndDateRange(
                walletId, startOfDay, endOfDay
            );

            if (totalWithCurrent.compareTo(dailyThresholdAmount) > 0) {
                BigDecimal exceedAmount = totalWithCurrent.subtract(dailyThresholdAmount);
                double riskScore = Math.min(8.0, 2.0 + (exceedAmount.doubleValue() / 10000.0));
                return new RiskRuleResult(
                    RiskRuleType.DAILY_THRESHOLD,
                    true,
                    BigDecimal.valueOf(riskScore),
                    String.format("Daily transaction volume (%.2f) exceeds threshold (%.2f)",
                        totalWithCurrent, dailyThresholdAmount)
                );
            }

            if (dailyTransactionCount >= dailyTransactionCountThreshold) {
                return new RiskRuleResult(
                    RiskRuleType.DAILY_THRESHOLD,
                    true,
                    BigDecimal.valueOf(6.0),
                    String.format("Unusual transaction frequency: %d transactions today", dailyTransactionCount + 1)
                );
            }

            return new RiskRuleResult(
                RiskRuleType.DAILY_THRESHOLD,
                false,
                BigDecimal.ZERO,
                "Daily threshold check passed"
            );
        } catch (Exception e) {
            // Return non-blocking error for rule evaluation failures
            return new RiskRuleResult(
                RiskRuleType.DAILY_THRESHOLD,
                false,
                BigDecimal.ZERO,
                "Error evaluating daily threshold: " + e.getMessage()
            );
        }
    }

    @Override
    public String getName() {
        return "DailyThresholdRule";
    }
}
