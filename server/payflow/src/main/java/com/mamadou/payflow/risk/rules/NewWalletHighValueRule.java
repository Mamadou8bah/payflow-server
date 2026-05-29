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
 * Rule to detect high-value transactions from newly created wallets.
 * New wallets performing large transfers are potentially compromised or fraudulent.
 */
@Component
@RequiredArgsConstructor
public class NewWalletHighValueRule implements RiskRule {

    private final TransactionRepository transactionRepository;

    @Value("${payflow.risk.new-wallet-age-days:7}")
    private int newWalletAgeDays;

    @Value("${payflow.risk.new-wallet-high-value-threshold:10000}")
    private BigDecimal newWalletHighValueThreshold;

    @Override
    public RiskRuleResult evaluate(Long walletId, Wallet wallet, BigDecimal transactionAmount, Long transactionId) {
        try {
            LocalDateTime walletCreationThreshold = LocalDateTime.now().minusDays(newWalletAgeDays);
            if (wallet.getCreatedAt() == null || wallet.getCreatedAt().isAfter(walletCreationThreshold)) {
                if (transactionAmount.compareTo(newWalletHighValueThreshold) > 0) {
                    double riskScore = Math.min(8.5, 4.0 + (transactionAmount.doubleValue() / 5000.0));
                    return new RiskRuleResult(
                        RiskRuleType.NEW_WALLET_HIGH_VALUE,
                        true,
                        BigDecimal.valueOf(riskScore),
                        String.format("High-value transaction (%.2f) from newly created wallet", transactionAmount)
                    );
                }

                long totalTransactionCount = transactionRepository.countByWallet(walletId);
                if (totalTransactionCount == 0 && transactionAmount.compareTo(BigDecimal.valueOf(5000)) > 0) {
                    return new RiskRuleResult(
                        RiskRuleType.NEW_WALLET_HIGH_VALUE,
                        true,
                        BigDecimal.valueOf(6.0),
                        "First transaction from new wallet with significant amount"
                    );
                }
            }

            return new RiskRuleResult(
                RiskRuleType.NEW_WALLET_HIGH_VALUE,
                false,
                BigDecimal.ZERO,
                "New wallet high-value check passed"
            );
        } catch (Exception e) {
            return new RiskRuleResult(
                RiskRuleType.NEW_WALLET_HIGH_VALUE,
                false,
                BigDecimal.ZERO,
                "Error evaluating new wallet rule: " + e.getMessage()
            );
        }
    }

    @Override
    public String getName() {
        return "NewWalletHighValueRule";
    }
}
