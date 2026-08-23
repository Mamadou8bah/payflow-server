import { getAuthSession } from "./auth-session";
import { getMerchantSession } from "./merchant-api";
import { mockMerchantDashboard } from "./mock/merchant-dashboard";
import { walletApi } from "./wallet-api";

export type MerchantDashboardData = typeof mockMerchantDashboard;

export function hasLiveMerchantApi(): boolean {
  const auth = getAuthSession();
  if (auth?.accessToken && auth.role === "merchant") return true;
  return Boolean(getMerchantSession()?.accessToken);
}

export function resolveMerchantStoreName(): string {
  const merchant = getMerchantSession();
  if (merchant?.businessName) return merchant.businessName;
  const auth = getAuthSession();
  if (auth?.name) return auth.name;
  return "Your business";
}

function formatMoney(currency: string, amount: number): string {
  return `${currency} ${Number(amount).toLocaleString()}`;
}

export async function loadMerchantDashboardFromApi(): Promise<MerchantDashboardData> {
  const storeName = resolveMerchantStoreName();
  const [wallets, transactions, links] = await Promise.all([
    walletApi.listWallets(),
    walletApi.listTransactions({ limit: 50 }),
    walletApi.listPaymentLinks(),
  ]);
  const balances = await Promise.all(wallets.map((w) => walletApi.getBalance(w.id)));

  const walletRows = wallets.map((w, i) => ({
    id: w.id,
    name: w.name,
    currency: w.currency,
    balance: formatMoney(w.currency, balances[i]?.balance ?? 0),
    status: w.status,
    dailyLimit: "—",
    dailyUsed: "—",
  }));

  const deposits = transactions
    .filter((t) => t.type.toUpperCase().includes("DEPOSIT"))
    .map((t) => ({
      id: t.id,
      walletId: t.destinationWalletId ?? t.sourceWalletId ?? wallets[0]?.id ?? 0,
      amount: formatMoney(t.currency, t.amount),
      status: t.status,
      reference: t.reference,
      time: t.createdAt,
    }));

  const withdrawals = transactions
    .filter((t) => t.type.toUpperCase().includes("WITHDRAW"))
    .map((t) => ({
      id: t.id,
      walletId: t.sourceWalletId ?? wallets[0]?.id ?? 0,
      amount: formatMoney(t.currency, t.amount),
      status: t.status,
      reference: t.reference,
      time: t.createdAt,
    }));

  const transfers = transactions
    .filter((t) => t.type.toUpperCase().includes("TRANSFER"))
    .map((t) => ({
      id: t.id,
      from: t.sourceWalletId ?? 0,
      to: t.destinationWalletId ?? 0,
      amount: formatMoney(t.currency, t.amount),
      status: t.status,
      reference: t.reference,
      time: t.createdAt,
    }));

  const activeLinks = links.filter((l) => l.status === "ACTIVE").length;
  const totalBalance = balances.reduce((sum, b) => sum + Number(b.balance), 0);
  const currency = wallets[0]?.currency ?? "GMD";

  const recentPayments = transactions.slice(0, 8).map((t) => ({
    id: t.reference,
    customer: t.description || "Customer",
    amount: formatMoney(t.currency, t.amount),
    status: t.status.toLowerCase(),
    time: t.createdAt,
  }));

  const paymentLinks = links.map((l) => ({
    id: l.reference,
    title: l.description || l.reference,
    amount: formatMoney(l.currency, l.amount),
    status: l.status.toLowerCase(),
    uses: 0,
  }));

  return {
    storeName,
    stats: [
      { label: "Total balance", value: formatMoney(currency, totalBalance), hint: `${wallets.length} wallets` },
      { label: "Pending operations", value: String(deposits.filter((d) => d.status !== "COMPLETED").length + withdrawals.filter((w) => w.status !== "COMPLETED").length), hint: "Awaiting completion" },
      { label: "Active payment links", value: String(activeLinks), hint: `${links.length} total` },
      { label: "Recent transactions", value: String(transactions.length), hint: "Loaded from API" },
    ],
    wallets: walletRows,
    deposits,
    withdrawals,
    transfers,
    subscriptions: [],
    recentPayments,
    paymentLinks,
    webhooks: [],
    charts: mockMerchantDashboard.charts,
  };
}
