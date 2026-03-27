package com.mamadou.payflow.user.enums;

public enum Permission {

    /* =========================
       USER / PROFILE
       ========================= */
    USER_CREATE,
    USER_READ,
    USER_UPDATE,
    USER_DISABLE,

    /* =========================
       WALLET / ACCOUNT
       ========================= */
    WALLET_CREATE,
    WALLET_READ,
    WALLET_FREEZE,
    WALLET_UNFREEZE,
    WALLET_CLOSE,
    WALLET_SET_LIMIT,
    WALLET_VIEW_BALANCE,

    /* =========================
       TRANSACTIONS
       ========================= */
    TRANSACTION_CREATE,
    TRANSACTION_READ,
    TRANSACTION_REVERSE,
    TRANSACTION_CANCEL,
    TRANSACTION_EXPORT,

    /* =========================
       TRANSFERS
       ========================= */
    TRANSFER_INITIATE,
    TRANSFER_APPROVE,
    TRANSFER_REJECT,
    TRANSFER_RETRY,

    /* =========================
       DEPOSITS / WITHDRAWALS
       ========================= */
    DEPOSIT_INITIATE,
    WITHDRAWAL_INITIATE,
    WITHDRAWAL_APPROVE,
    WITHDRAWAL_REJECT,

    /* =========================
       LEDGER / FINANCIAL CORE
       ========================= */
    LEDGER_VIEW,
    LEDGER_ADJUST,
    LEDGER_REBUILD_BALANCE,

    /* =========================
       RISK / FRAUD
       ========================= */
    RISK_VIEW_FLAGS,
    RISK_RESOLVE_FLAG,
    RISK_OVERRIDE_LIMIT,

    /* =========================
       RECONCILIATION
       ========================= */
    RECONCILIATION_RUN,
    RECONCILIATION_VIEW_REPORT,
    RECONCILIATION_RESOLVE_MISMATCH,

    /* =========================
       WEBHOOK / PAYMENT EVENTS
       ========================= */
    WEBHOOK_RECEIVE,
    WEBHOOK_REPROCESS,
    WEBHOOK_VIEW_EVENTS,

    /* =========================
       ADMIN OPERATIONS
       ========================= */
    ADMIN_FORCE_REVERSAL,
    ADMIN_FREEZE_USER,
    ADMIN_UNLOCK_USER,
    ADMIN_SYSTEM_CONFIG,

    /* =========================
       AUDIT / COMPLIANCE
       ========================= */
    AUDIT_VIEW,
    AUDIT_EXPORT,

    /* =========================
       API / INTEGRATIONS
       ========================= */
    API_KEY_CREATE,
    API_KEY_REVOKE,
    API_KEY_VIEW,

    /* =========================
       REPORTING / SETTLEMENT
       ========================= */
    SETTLEMENT_RUN,
    SETTLEMENT_VIEW,
    REPORT_GENERATE

}