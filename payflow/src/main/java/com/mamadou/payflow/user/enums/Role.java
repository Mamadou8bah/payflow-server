package com.mamadou.payflow.user.enums;

import java.util.Set;

import static com.mamadou.payflow.user.enums.Permission.*;

public enum Role {
    USER(
            Set.of(
                    DEPOSIT_INITIATE,
                    WITHDRAWAL_INITIATE,
                    WALLET_VIEW_BALANCE,
                    WALLET_READ,
                    TRANSACTION_CREATE,
                    TRANSACTION_READ,
                    TRANSFER_RETRY,
                    TRANSACTION_EXPORT,
                    USER_READ,
                    USER_UPDATE

            )
    ),
    ADMIN(
            Set.of(
                    DEPOSIT_INITIATE,
                    WITHDRAWAL_INITIATE,
                    WALLET_VIEW_BALANCE,
                    WALLET_READ,
                    WALLET_CREATE,
                    TRANSACTION_CREATE,
                    TRANSACTION_READ,
                    TRANSFER_RETRY,
                    TRANSACTION_EXPORT,
                    TRANSACTION_REVERSE,
                    ADMIN_FREEZE_USER,
                    ADMIN_SYSTEM_CONFIG,
                    ADMIN_UNLOCK_USER,
                    ADMIN_FORCE_REVERSAL,
                    API_KEY_CREATE,
                    API_KEY_REVOKE,
                    API_KEY_VIEW,
                    WITHDRAWAL_REJECT,
                    AUDIT_EXPORT,
                    AUDIT_VIEW,
                    WEBHOOK_RECEIVE,
                    WEBHOOK_REPROCESS,
                    WEBHOOK_VIEW_EVENTS,
                    USER_CREATE,
                    USER_DISABLE,
                    USER_READ,
                    USER_UPDATE,
                    RECONCILIATION_RUN,
                    RECONCILIATION_VIEW_REPORT,
                    RECONCILIATION_RESOLVE_MISMATCH,
                    RISK_VIEW_FLAGS,
                    RISK_RESOLVE_FLAG,
                    RISK_OVERRIDE_LIMIT,
                    LEDGER_VIEW,
                    LEDGER_ADJUST,
                    LEDGER_REBUILD_BALANCE,
                    TRANSFER_APPROVE
            )
    ),
    MERCHANT(
            Set.of(
                    API_KEY_CREATE,
                    API_KEY_REVOKE,
                    API_KEY_VIEW,
                    WITHDRAWAL_INITIATE,
                    DEPOSIT_INITIATE,
                    TRANSACTION_CREATE,
                    TRANSFER_INITIATE,
                    TRANSACTION_READ
            )
    );

    private final Set<Permission> permissions;
    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }
    public Set<Permission> getPermissions()
    {
        return permissions;
    }
}
