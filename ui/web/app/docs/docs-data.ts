export const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:5000";

export type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE";

export type NavItem = { id: string; label: string };
export type NavGroup = { title: string; items: NavItem[] };

export const navGroups: NavGroup[] = [
  {
    title: "Guide",
    items: [
      { id: "introduction", label: "Introduction" },
      { id: "api-keys", label: "API keys" },
      { id: "quickstart", label: "Quickstart" },
      { id: "webhooks", label: "Webhooks" },
      { id: "errors", label: "Errors" },
    ],
  },
];

export type EndpointDoc = {
  method: HttpMethod;
  path: string;
  summary: string;
};

export const paymentEndpoints: EndpointDoc[] = [
  { method: "POST", path: "/api/v1/payment-links", summary: "Create a checkout link for your customer" },
  { method: "GET", path: "/api/v1/payment-links", summary: "List payment links you created" },
  { method: "GET", path: "/api/v1/payment-links/{id}", summary: "Get a single payment link" },
];

export const apiKeyEndpoints: EndpointDoc[] = [
  { method: "POST", path: "/api/auth/api-keys", summary: "Create an API key (dashboard session required)" },
  { method: "DELETE", path: "/api/auth/api-keys/{id}", summary: "Revoke an API key" },
];

export const codeSamples = {
  createKey: `curl -X POST ${BASE_URL}/api/auth/api-keys \\
  -H "Authorization: Bearer <dashboard_session_token>" \\
  -H "Content-Type: application/json" \\
  -d '{ "name": "Production backend" }'`,
  createPayment: `curl -X POST ${BASE_URL}/api/v1/payment-links \\
  -H "X-Api-Key: pf_live_<public>.<secret>" \\
  -H "Content-Type: application/json" \\
  -d '{
    "walletId": 1,
    "amount": 1500,
    "currency": "GMD",
    "description": "Order #1042"
  }'`,
  webhookPayload: `{
  "id": "evt_4410",
  "type": "payment.paid",
  "data": {
    "paymentId": "pay_8821",
    "reference": "plink_a91f",
    "amount": 1500,
    "currency": "GMD",
    "status": "paid"
  }
}`,
};

export const quickstartSteps = [
  {
    step: "1",
    title: "Create an API key",
    body: "Sign in to the developer dashboard and generate a key. Copy the full pf_live_… value — it is only shown once.",
  },
  {
    step: "2",
    title: "Create a payment from your server",
    body: "POST to payment-links with your API key. Redirect the customer to the checkout URL in the response.",
  },
  {
    step: "3",
    title: "Handle webhooks",
    body: "When payment.paid arrives at your endpoint, fulfil the order or unlock access in your app.",
  },
];

export const errorCodes = [
  { code: "401", title: "Unauthorized", detail: "Missing or invalid X-Api-Key header." },
  { code: "403", title: "Forbidden", detail: "Key is revoked or does not have access to this resource." },
  { code: "404", title: "Not found", detail: "Payment link or wallet does not exist." },
  { code: "422", title: "Validation error", detail: "Check amount, currency, and walletId in the request body." },
];
