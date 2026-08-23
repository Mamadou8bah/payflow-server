package com.mamadou.payflow.fraud.exception;

import com.mamadou.payflow.transaction.exception.TransactionException;

public class FraudBlockedException extends TransactionException {

    private final double fraudScore;
    private final String decision;

    public FraudBlockedException(String message, double fraudScore, String decision) {
        super(message);
        this.fraudScore = fraudScore;
        this.decision = decision;
    }

    public double getFraudScore() {
        return fraudScore;
    }

    public String getDecision() {
        return decision;
    }
}
