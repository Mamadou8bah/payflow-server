import { API_BASE } from "./api-base";
import { authenticatedFetch, readAuthenticatedJson } from "./authenticated-fetch";

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

export type PaymentLinkResponse = {
  id: number;
  reference: string;
  amount: number;
  currency: string;
  description: string;
  status: string;
  paymentUrl?: string;
  expiresAt: string;
};

async function apiRequest<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await authenticatedFetch(path, init);
  return readAuthenticatedJson<T>(response);
}

export const walletApi = {
  listWallets: () => apiRequest<WalletResponse[]>("/api/v1/wallets"),

  getBalance: (walletId: number) => apiRequest<WalletBalance>(`/api/v1/wallets/${walletId}/balance`),

  createWallet: (name: string, currency: string) =>
    apiRequest<WalletResponse>("/api/v1/wallets", {
      method: "POST",
      body: JSON.stringify({ name, currency }),
    }),

  listTransactions: (params?: { limit?: number }) => {
    const query = params?.limit ? `?limit=${params.limit}` : "";
    return apiRequest<TransactionResponse[]>(`/api/v1/transactions${query}`);
  },

  createTransfer: (payload: {
    destinationWalletId: number;
    sourceWalletId?: number;
    amount: number;
    description?: string;
    reference?: string;
  }) =>
    apiRequest<TransferResponse>("/api/v1/transfers", {
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
    apiRequest<DepositResponse>("/api/v1/deposits", {
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
    apiRequest<WithdrawalResponse>("/api/v1/withdrawals", {
      method: "POST",
      body: JSON.stringify(payload),
    }),

  listPaymentLinks: () => apiRequest<PaymentLinkResponse[]>("/api/v1/payment-links"),

  createPaymentLink: (payload: {
    walletId: number;
    amount: number;
    currency: string;
    description: string;
    paymentUrl?: string;
    expiresDays?: number;
  }) =>
    apiRequest<PaymentLinkResponse>("/api/v1/payment-links", {
      method: "POST",
      body: JSON.stringify(payload),
    }),
};

export { API_BASE };
