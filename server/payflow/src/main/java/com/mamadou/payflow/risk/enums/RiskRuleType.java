package com.mamadou.payflow.risk.enums;

public enum RiskRuleType {
    DAILY_THRESHOLD("Daily Threshold Exceeded"),
    RAPID_TRANSFER("Rapid Sequential Transfer"),
    NEW_WALLET_HIGH_VALUE("New Wallet High Value Transfer"),
    SUSPICIOUS_PATTERN("Suspicious Transaction Pattern"),
    FRAUD_ML_SCORE("ML Fraud Detection Score");

    private final String description;

    RiskRuleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
