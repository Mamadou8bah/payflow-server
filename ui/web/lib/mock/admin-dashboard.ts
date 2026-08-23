export type WalletStatus = "ACTIVE" | "SUSPENDED" | "CLOSED";

export type AdminWallet = {
  id: number;
  owner: string;
  email: string;
  status: WalletStatus;
  balance: string;
  currency: string;
  kycLevel: string;
};

export type AdminTransaction = {
  id: number;
  reference: string;
  amount: string;
  status: "PENDING" | "COMPLETED" | "FAILED" | "REVERSED";
  sourceWalletId: number;
  destinationWalletId: number;
  createdAt: string;
};

export type AdminWebhook = {
  id: number;
  eventType: string;
  status: string;
  provider: string;
  receivedAt: string;
  payloadRef: string;
};

export type AdminRiskFlag = {
  id: number;
  walletId: number;
  level: "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";
  rule: string;
  description: string;
  resolved: boolean;
  transactionId?: number;
  createdAt: string;
};

export type ReconciliationReport = {
  id: number;
  reconciliationType: "WALLET_LEDGER" | "WEBHOOK_DEPOSIT";
  status: "COMPLETED" | "RUNNING" | "FAILED";
  startedAt: string;
  completedAt?: string;
  totalRecordsChecked: number;
  mismatchesFound: number;
  mismatchesResolved: number;
  summary: string;
  triggeredBy: string;
  automated: boolean;
};

export type AuditLogEntry = {
  id: string;
  actionType: string;
  entityType: string;
  entityId: number;
  actor: string;
  changeDescription: string;
  success: boolean;
  timestamp: string;
};

export type ReconciliationMismatch = {
  id: number;
  reportId: number;
  mismatchType: string;
  entityId: number;
  entityType: string;
  expectedValue: string;
  actualValue: string;
  variance: string;
  description: string;
  resolved: boolean;
  resolutionAction?: string;
  createdAt: string;
};

export type AdminDashboardData = {
  totalWallets: number;
  activeWallets: number;
  frozenWallets: number;
  totalTransactions: number;
  failedTransactions: number;
  pendingTransactions: number;
  totalRiskFlags: number;
  criticalRiskFlags: number;
  unresolvedReconciliationMismatches: number;
  failedOperations: number;
  operationCounts: {
    freezes: number;
    reversals: number;
    reprocesses: number;
    reconciliations: number;
  };
  systemHealth: {
    status: "HEALTHY" | "DEGRADED" | "DOWN";
    uptime: string;
    lastCheck: string;
  };
  volumeToday: { label: string; amount: string; change: string }[];
  wallets: AdminWallet[];
  transactions: AdminTransaction[];
  webhooks: AdminWebhook[];
  riskFlags: AdminRiskFlag[];
  reconciliationReports: ReconciliationReport[];
  auditLogs: AuditLogEntry[];
  riskRuleStats: Record<string, number>;
  riskEngineConfig: Record<string, string | number | boolean>;
  reconciliationMismatches: ReconciliationMismatch[];
  reconciliationStatus: {
    unresolvedMismatchCount: number;
    reportsWithMismatches: number;
  };
  charts: {
    volumeByRange: Record<"7d" | "30d" | "90d", { label: string; value: number }[]>;
    transactionStatus: { label: string; value: number; color: string }[];
    walletStatus: { label: string; value: number; color: string }[];
    operationsWeekly: { label: string; value: number; color?: string }[];
    kpiSparklines: Record<string, number[]>;
  };
};

export const initialAdminDashboard: AdminDashboardData = {
  totalWallets: 0,
  activeWallets: 0,
  frozenWallets: 0,
  totalTransactions: 0,
  failedTransactions: 0,
  pendingTransactions: 0,
  totalRiskFlags: 0,
  criticalRiskFlags: 0,
  unresolvedReconciliationMismatches: 0,
  failedOperations: 0,
  operationCounts: { freezes: 0, reversals: 0, reprocesses: 0, reconciliations: 0 },
  systemHealth: { status: "HEALTHY", uptime: "—", lastCheck: new Date(0).toISOString() },
  volumeToday: [],
  wallets: [],
  transactions: [],
  webhooks: [],
  riskFlags: [],
  reconciliationReports: [],
  auditLogs: [],
  riskRuleStats: {},
  riskEngineConfig: {},
  reconciliationMismatches: [],
  reconciliationStatus: {
    unresolvedMismatchCount: 0,
    reportsWithMismatches: 0,
  },
  charts: {
    volumeByRange: {
      "7d": [],
      "30d": [],
      "90d": [],
    },
    transactionStatus: [],
    walletStatus: [],
    operationsWeekly: [],
    kpiSparklines: {
      volume: [],
      wallets: [],
      risk: [],
    },
  },
};

export function getAdminDashboard(): AdminDashboardData {
  return initialAdminDashboard;
}
