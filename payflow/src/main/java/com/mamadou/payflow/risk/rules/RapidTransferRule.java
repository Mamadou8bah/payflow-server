package com.mamadou.payflow.risk.rules;

import com.mamadou.payflow.risk.dto.RiskRuleResult;
import com.mamadou.payflow.risk.enums.RiskRuleType;
import com.mamadou.payflow.transaction.repository.TransactionRepository;
import com.mamadou.payflow.wallet.entity.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Rule to detect rapid sequential transfers.
 * Flags wallets performing multiple transfers in quick succession, which may indicate
 * compromised accounts or suspicious activity.
 */
@Component
@RequiredArgsConstructor
public class RapidTransferRule implements RiskRule {

    private final TransactionRepository transactionRepository;

    @Value("${payflow.risk.rapid-transfer-window-seconds:300}")
    private long rapidTransferWindowSeconds;

    @Value("${payflow.risk.rapid-transfer-count:5}")
    private int rapidTransferCountThreshold;

    @Override
    public RiskRuleResult evaluate(Long walletId, Wallet wallet, BigDecimal transactionAmount, Long transactionId) {
        try {
            LocalDateTime windowStart = LocalDateTime.now().minusSeconds(rapidTransferWindowSeconds);
            LocalDateTime now = LocalDateTime.now();

            // Count transfers in the rapid transfer window
            long recentTransferCount = transactionRepository.countByWalletAndDateRange(
                walletId, windowStart, now
            );

            if (recentTransferCount >= rapidTransferCountThreshold) {
                double riskScore = Math.min(9.0, 5.0 + (recentTransferCount * 0.5));
                return new RiskRuleResult(
                    RiskRuleType.RAPID_TRANSFER,
                    true,
                    BigDecimal.valueOf(riskScore),
                    String.format("Rapid sequential transfers detected: %d transfers in %d seconds",
                        recentTransferCount, rapidTransferWindowSeconds)
                );
            }

            return new RiskRuleResult(
                RiskRuleType.RAPID_TRANSFER,
                false,
                BigDecimal.ZERO,
                "Rapid transfer check passed"
            );
        } catch (Exception e) {
            return new RiskRuleResult(
                RiskRuleType.RAPID_TRANSFER,
                false,
                BigDecimal.ZERO,
                "Error evaluating rapid transfer: " + e.getMessage()
            );
        }
    }

    @Override
    public String getName() {
        return "RapidTransferRule";
    }
}
