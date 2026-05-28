package com.mamadou.payflow.reconciliation.enums;

public enum ReconciliationStatus {
    PENDING("Waiting to run"),
    IN_PROGRESS("Currently running"),
    COMPLETED("Completed successfully"),
    COMPLETED_WITH_MISMATCHES("Completed with issues found"),
    FAILED("Execution failed"),
    RESOLVED("All mismatches resolved");

    private final String description;

    ReconciliationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
