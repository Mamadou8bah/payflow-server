import type { AgentQrPayload } from "../types";

export function buildAgentQrPayload(input: {
  operation: "DEPOSIT" | "WITHDRAWAL";
  reference: string;
  walletId: number;
  walletName: string;
  amount: string;
  currency: string;
  merchant: string;
}): AgentQrPayload {
  return {
    ...input,
    createdAt: new Date().toISOString(),
  };
}

export function encodeAgentQrPayload(payload: AgentQrPayload): string {
  return JSON.stringify({
    app: "payflow",
    v: 1,
    operation: payload.operation,
    reference: payload.reference,
    walletId: payload.walletId,
    walletName: payload.walletName,
    amount: payload.amount,
    currency: payload.currency,
    merchant: payload.merchant,
    createdAt: payload.createdAt,
  });
}
