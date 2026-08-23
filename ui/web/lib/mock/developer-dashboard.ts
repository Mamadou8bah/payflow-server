export const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:5000";

export const mockDeveloperDashboard = {
  projectName: "My integration",
  stats: [
    { label: "Payments (30d)", value: "—", hint: "Sign in to load live data" },
    { label: "Success rate", value: "—", hint: "Sign in to load live data" },
    { label: "Active API keys", value: "0", hint: "No keys yet" },
  ],
  apiKeys: [] as {
    id: string;
    name: string;
    prefix: string;
    created: string;
    lastUsed: string;
    status: string;
  }[],
  payments: [] as {
    id: string;
    reference: string;
    amount: string;
    status: string;
    customer: string;
    time: string;
  }[],
  webhookUrl: "",
  webhookEvents: [] as { id: string; type: string; status: string; time: string }[],
  integrationSnippet: `curl -X POST ${API_BASE}/api/v1/payment-links \\
  -H "X-Api-Key: pf_live_your_key.secret" \\
  -H "Content-Type: application/json" \\
  -d '{
    "walletId": 1,
    "amount": 1500,
    "currency": "GMD",
    "description": "Order #1042"
  }'`,
};
