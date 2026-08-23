export type CustomerTransaction = {
  id: string;
  type: "TRANSFER_IN" | "TRANSFER_OUT" | "DEPOSIT" | "WITHDRAWAL";
  counterparty: string;
  amount: number;
  currency: string;
  status: "COMPLETED" | "PENDING" | "FAILED" | "AWAITING_AGENT";
  method: string;
  time: string;
};

export type CustomerWallet = {
  id: number;
  name: string;
  currency: string;
  balance: number;
  status: "ACTIVE" | "SUSPENDED" | "CLOSED";
  dailyLimit: number;
  dailyUsed: number;
};

export type CustomerDashboardData = {
  phone: string;
  wallet: CustomerWallet;
  transactions: CustomerTransaction[];
  pendingCount: number;
};

export const emptyCustomerDashboard: CustomerDashboardData = {
  phone: "",
  wallet: {
    id: 0,
    name: "No wallet",
    currency: "GMD",
    balance: 0,
    status: "ACTIVE",
    dailyLimit: 0,
    dailyUsed: 0,
  },
  pendingCount: 0,
  transactions: [],
};

/** @deprecated Demo-only seed data — not used in production builds */
export const mockCustomerDashboard: CustomerDashboardData = {
  phone: "+220 712 3456",
  wallet: {
    id: 6544,
    name: "Personal wallet",
    currency: "GMD",
    balance: 52806.99,
    status: "ACTIVE",
    dailyLimit: 100000,
    dailyUsed: 12400,
  },
  pendingCount: 2,
  transactions: [
    {
      id: "txn_8821",
      type: "TRANSFER_IN",
      counterparty: "Lamin Sowe",
      amount: 4500,
      currency: "GMD",
      status: "COMPLETED",
      method: "Payflow wallet",
      time: "2026-06-27T10:40:00Z",
    },
    {
      id: "txn_8820",
      type: "TRANSFER_OUT",
      counterparty: "Acme Merchant",
      amount: 12000,
      currency: "GMD",
      status: "COMPLETED",
      method: "Payflow wallet",
      time: "2026-06-27T10:12:00Z",
    },
    {
      id: "txn_8819",
      type: "DEPOSIT",
      counterparty: "ModemPay",
      amount: 8000,
      currency: "GMD",
      status: "COMPLETED",
      method: "Mobile money",
      time: "2026-06-26T16:30:00Z",
    },
    {
      id: "txn_8818",
      type: "WITHDRAWAL",
      counterparty: "Agent — Banjul",
      amount: 5000,
      currency: "GMD",
      status: "AWAITING_AGENT",
      method: "Cash out",
      time: "2026-06-26T14:15:00Z",
    },
    {
      id: "txn_8817",
      type: "TRANSFER_OUT",
      counterparty: "Awa Camara",
      amount: 2250,
      currency: "GMD",
      status: "COMPLETED",
      method: "Payflow wallet",
      time: "2026-06-26T09:58:00Z",
    },
    {
      id: "txn_8816",
      type: "DEPOSIT",
      counterparty: "Agent — Serrekunda",
      amount: 15000,
      currency: "GMD",
      status: "COMPLETED",
      method: "Agent cash-in",
      time: "2026-06-25T11:20:00Z",
    },
  ],
};

export function formatMoney(amount: number, currency: string): string {
  return `${currency} ${amount.toLocaleString("en-GB", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}

export function getGreeting(): string {
  const hour = new Date().getHours();
  if (hour < 12) return "Good morning";
  if (hour < 17) return "Good afternoon";
  return "Good evening";
}
