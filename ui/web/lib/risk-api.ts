import { authenticatedFetch, readAuthenticatedJson } from "./authenticated-fetch";

type ApiEnvelope<T> = {
  success: boolean;
  message: string;
  data: T;
};

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await authenticatedFetch(path, init);
  const payload = await readAuthenticatedJson<ApiEnvelope<T>>(response);
  if (!payload.success) {
    throw new Error(payload.message || "Request failed");
  }
  return payload.data;
}

export type RiskEvaluationResult = {
  transactionId: number;
  walletId: number;
  riskLevel: string;
  riskScore: number;
  triggeredRules: Array<{
    ruleType: string;
    triggered: boolean;
    riskScore: number;
    message: string;
  }>;
  shouldBlock: boolean;
  summary: string;
};

export type RiskFlagResponse = {
  id: number;
  walletId: number;
  transactionId: number;
  riskLevel: string;
  triggeringRule: string;
  riskScore: number;
  reason: string;
  resolved: boolean;
  resolutionAction: string | null;
  flaggedAt: string;
  resolvedAt: string | null;
};

export type FraudDecision = {
  transaction_id: string;
  decision: string;
  score: number;
  reasons: string[];
  latency_ms: number;
  rule_triggered: string | null;
};

export const riskApi = {
  evaluate: (walletId: number, amount: number, transactionId: number) => {
    const params = new URLSearchParams({
      walletId: String(walletId),
      amount: String(amount),
      transactionId: String(transactionId),
    });
    return request<RiskEvaluationResult>(`/api/v1/risk/evaluate?${params}`, { method: "POST" });
  },

  getSummary: (walletId: number) =>
    request<Record<string, unknown>>(`/api/v1/risk/wallets/${walletId}/summary`),

  checkCritical: (walletId: number) =>
    request<Record<string, unknown>>(`/api/v1/risk/wallets/${walletId}/critical`),

  resolveFlag: (flagId: number, resolutionAction: string) => {
    const params = new URLSearchParams({ resolutionAction });
    return request<RiskFlagResponse>(`/api/v1/risk/flags/${flagId}/resolve?${params}`, {
      method: "PUT",
    });
  },

  getRuleStats: () => request<Record<string, number>>("/api/v1/risk/rules/stats"),

  getEngineConfig: () => request<Record<string, unknown>>("/api/v1/risk/config"),

  getUnresolvedFlags: (walletId: number) =>
    request<RiskFlagResponse[]>(`/api/v1/risk/wallets/${walletId}/flags/unresolved`),

  scoreFraud: (walletId: number, amount: number, transactionId?: string) => {
    const params = new URLSearchParams({
      walletId: String(walletId),
      amount: String(amount),
    });
    if (transactionId) params.set("transactionId", transactionId);
    return request<FraudDecision>(`/api/v1/fraud/score?${params}`, { method: "POST" });
  },

  fraudHealth: () =>
    request<{ enabled: boolean; healthy: boolean; baseUrl: string }>("/api/v1/fraud/health"),

  fraudConfig: () => request<Record<string, unknown>>("/api/v1/fraud/config"),
};
