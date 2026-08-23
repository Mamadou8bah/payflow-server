import type { AgentOperation, AgentSession, AgentStats } from "../types";

export const mockAgentSession: AgentSession = {
  name: "Modou Camara",
  agentCode: "AGT-2041",
  location: "Banjul Central",
  phone: "+220 700 1122",
  email: "modou.camara@payflow.local",
};

export const mockAgentStats: AgentStats = {
  todayDeposits: 12,
  todayWithdrawals: 8,
  todayVolume: 186400,
  pendingCount: 3,
  completedToday: 20,
};

export const mockPendingOperations: AgentOperation[] = [
  {
    operation: "DEPOSIT",
    reference: "dep_1301",
    walletId: 6544,
    walletName: "Main wallet",
    amount: 5000,
    currency: "GMD",
    status: "AWAITING_AGENT",
    userId: 101,
    merchantName: "Fatou Jallow",
    createdAt: "2026-06-27T11:05:00Z",
    location: "Banjul",
  },
  {
    operation: "WITHDRAWAL",
    reference: "wd_901",
    walletId: 6544,
    walletName: "Main wallet",
    amount: 2500,
    currency: "GMD",
    status: "AWAITING_AGENT",
    userId: 101,
    merchantName: "Fatou Jallow",
    createdAt: "2026-06-27T10:48:00Z",
    location: "Serrekunda",
  },
  {
    operation: "DEPOSIT",
    reference: "dep_1302",
    walletId: 6545,
    walletName: "Savings wallet",
    amount: 12000,
    currency: "GMD",
    status: "AWAITING_AGENT",
    userId: 102,
    merchantName: "Lamin Sowe",
    createdAt: "2026-06-27T10:20:00Z",
    location: "Brikama",
  },
];

export const mockCompletedOperations: AgentOperation[] = [
  {
    operation: "DEPOSIT",
    reference: "dep_1298",
    walletId: 6544,
    walletName: "Main wallet",
    amount: 8000,
    currency: "GMD",
    status: "COMPLETED",
    userId: 103,
    merchantName: "Awa Ceesay",
    createdAt: "2026-06-27T09:15:00Z",
  },
  {
    operation: "WITHDRAWAL",
    reference: "wd_898",
    walletId: 6546,
    walletName: "USD wallet",
    amount: 150,
    currency: "USD",
    status: "COMPLETED",
    userId: 104,
    merchantName: "Omar Bah",
    createdAt: "2026-06-27T08:40:00Z",
  },
  {
    operation: "DEPOSIT",
    reference: "dep_1295",
    walletId: 6544,
    walletName: "Main wallet",
    amount: 3500,
    currency: "GMD",
    status: "COMPLETED",
    userId: 105,
    merchantName: "Mariama Touray",
    createdAt: "2026-06-27T08:10:00Z",
  },
];
