export type AgentOperation = "DEPOSIT" | "WITHDRAWAL";

export type AgentQrPayload = {
  type: "payflow/agent";
  operation: AgentOperation;
  reference: string;
  walletId: number;
  walletName: string;
  amount: string;
  currency: string;
  merchant: string;
  createdAt: string;
};

export function buildAgentQrPayload(input: Omit<AgentQrPayload, "type" | "createdAt"> & { createdAt?: string }): AgentQrPayload {
  return {
    type: "payflow/agent",
    createdAt: input.createdAt ?? new Date().toISOString(),
    operation: input.operation,
    reference: input.reference,
    walletId: input.walletId,
    walletName: input.walletName,
    amount: input.amount,
    currency: input.currency,
    merchant: input.merchant,
  };
}

export function encodeAgentQrPayload(payload: AgentQrPayload): string {
  return JSON.stringify(payload);
}
