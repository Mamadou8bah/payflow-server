import { API_BASE } from "./config";
import { authFetch, readAuthJson } from "./sessionAuth";

type ApiEnvelope<T> = {
  success: boolean;
  message: string;
  data: T;
};

export type AuthSession = {
  accessToken: string;
  refreshToken: string;
  username: string;
  role: string;
  firstName?: string;
  lastName?: string;
};

export type WalletResponse = {
  id: number;
  name: string;
  currency: string;
  status: string;
  ledgerAccountCode: string;
  ownerId: number;
};

export type WalletBalance = {
  id: number;
  walletStatus: string;
  ledgerAccountCode: string;
  balance: number;
};

export type TransactionResponse = {
  id: number;
  reference: string;
  type: string;
  status: string;
  amount: number;
  currency: string;
  description: string;
  createdAt: string;
  sourceWalletId?: number;
  destinationWalletId?: number;
};

export type TransferResponse = {
  transaction: TransactionResponse;
  idempotentReplay: boolean;
};

export type DepositResponse = {
  id: number;
  reference: string;
  amount: number;
  currency: string;
  status: string;
  walletId: number;
};

export type WithdrawalResponse = {
  id: number;
  reference: string;
  amount: number;
  currency: string;
  status: string;
  walletId: number;
};

async function envelopeRequest<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    ...init,
    headers: { "Content-Type": "application/json", ...(init?.headers ?? {}) },
  });
  const text = await response.text();
  let payload: ApiEnvelope<T>;
  try {
    payload = JSON.parse(text) as ApiEnvelope<T>;
  } catch {
    throw new Error(text || "Request failed");
  }
  if (!response.ok || !payload.success) {
    throw new Error(payload.message || `Request failed (${response.status})`);
  }
  return payload.data;
}

async function authRequest<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await authFetch(path, init);
  return readAuthJson<T>(response);
}

export const api = {
  login: (email: string, password: string) =>
    envelopeRequest<AuthSession & { twoFactorRequired?: boolean }>("/api/auth/login", {
      method: "POST",
      body: JSON.stringify({ username: email, password }),
    }),

  listWallets: () => authRequest<WalletResponse[]>("/api/v1/wallets"),

  getBalance: (walletId: number) => authRequest<WalletBalance>(`/api/v1/wallets/${walletId}/balance`),

  listTransactions: () => authRequest<TransactionResponse[]>("/api/v1/transactions"),

  createTransfer: (payload: {
    destinationWalletId: number;
    sourceWalletId?: number;
    amount: number;
    description?: string;
    reference?: string;
  }) =>
    authRequest<TransferResponse>("/api/v1/transfers", {
      method: "POST",
      headers: payload.reference ? { "Idempotency-Key": payload.reference } : {},
      body: JSON.stringify(payload),
    }),

  createDeposit: (payload: {
    walletId: number;
    amount: number;
    currency: string;
    paymentMethod?: string;
    phoneNumber?: string;
    description?: string;
    idempotencyKey?: string;
  }) =>
    authRequest<DepositResponse>("/api/v1/deposits", {
      method: "POST",
      body: JSON.stringify(payload),
    }),

  createWithdrawal: (payload: {
    walletId: number;
    amount: number;
    currency: string;
    paymentMethod?: string;
    phoneNumber?: string;
    description?: string;
    idempotencyKey?: string;
  }) =>
    authRequest<WithdrawalResponse>("/api/v1/withdrawals", {
      method: "POST",
      body: JSON.stringify(payload),
    }),
};
