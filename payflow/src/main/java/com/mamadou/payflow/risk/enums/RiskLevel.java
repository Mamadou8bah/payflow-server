package com.mamadou.payflow.risk.enums;

public enum RiskLevel {
    LOW(0.0),
    MEDIUM(5.0),
    HIGH(7.0),
    CRITICAL(9.0);

    private final double threshold;

    RiskLevel(double threshold) {
        this.threshold = threshold;
    }

    public double getThreshold() {
        return threshold;
    }

    public static RiskLevel fromScore(double score) {
        if (score >= 9.0) return CRITICAL;
        if (score >= 7.0) return HIGH;
        if (score >= 5.0) return MEDIUM;
        return LOW;
    }
}
