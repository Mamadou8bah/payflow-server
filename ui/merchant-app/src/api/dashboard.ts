import { authFetch, readAuthJson } from "./sessionAuth";

export type PaymentLinkSummary = {
  id: number;
  reference: string;
  amount: number;
  currency: string;
  status: string;
};

export async function fetchMerchantDashboard(): Promise<{
  totalBalance: number;
  activeLinks: number;
  currency: string;
}> {
  const [wallets, links] = await Promise.all([
    authFetch("/api/v1/wallets").then((r) => readAuthJson<Array<{ id: number; name: string; currency: string; status: string }>>(r)),
    authFetch("/api/v1/payment-links").then((r) => readAuthJson<PaymentLinkSummary[]>(r)),
  ]);

  const balances = await Promise.all(
    wallets.map((w) =>
      authFetch(`/api/v1/wallets/${w.id}/balance`).then((r) => readAuthJson<{ balance: number }>(r)),
    ),
  );

  const totalBalance = balances.reduce((sum, b) => sum + Number(b.balance), 0);
  const activeLinks = links.filter((l) => l.status === "ACTIVE").length;
  const currency = wallets[0]?.currency ?? "GMD";

  return { totalBalance, activeLinks, currency };
}
