package com.mamadou.payflow.risk.service;

import com.mamadou.payflow.risk.dto.RiskRuleResult;
import com.mamadou.payflow.risk.rules.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service to manage and execute risk evaluation rules.
 * Handles registration, execution, and aggregation of risk rules.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RiskRuleService {

    private final DailyThresholdRule dailyThresholdRule;
    private final RapidTransferRule rapidTransferRule;
    private final NewWalletHighValueRule newWalletHighValueRule;

    private final List<RiskRule> rules;

    public List<RiskRule> getAllRules() {
        return List.of(dailyThresholdRule, rapidTransferRule, newWalletHighValueRule);
    }

    public Optional<RiskRule> getRuleByName(String name) {
        return getAllRules().stream()
            .filter(rule -> rule.getName().equals(name))
            .findFirst();
    }

    public int getActiveRuleCount() {
        return getAllRules().size();
    }

    public Map<String, Object> getRuleStats() {
        return Map.of(
            "totalRules", getAllRules().size(),
            "rules", getAllRules().stream()
                .map(RiskRule::getName)
                .collect(Collectors.toList())
        );
    }
}
