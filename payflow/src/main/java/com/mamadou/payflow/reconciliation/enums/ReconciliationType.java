package com.mamadou.payflow.reconciliation.enums;

public enum ReconciliationType {
    WALLET_LEDGER("Wallet vs Ledger"),
    WEBHOOK_DEPOSIT("Webhook vs Deposit"),
    TRANSACTION_FLOW("Transaction Flow"),
    BALANCE_CHECK("Balance Verification");

    private final String description;

    ReconciliationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
