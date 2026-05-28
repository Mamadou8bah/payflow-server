package com.mamadou.payflow.risk.dto;

import com.mamadou.payflow.risk.enums.RiskRuleType;

import java.math.BigDecimal;

public record RiskRuleResult(
    RiskRuleType ruleType,
    boolean triggered,
    BigDecimal riskScore,
    String reason
) {}
