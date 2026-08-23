"use client";

import { useEffect, useMemo, useState } from "react";
import { clearAuthSession, getAuthSession, type AppSession } from "../../lib/auth-session";
import { clearDemoSession, DEMO_AUTH_ENABLED, getDemoSession } from "../../lib/demo-auth";
import { buildAgentQrPayload, type AgentQrPayload } from "../../lib/agent-qr";
import {
  emptyCustomerDashboard,
  formatMoney,
  getGreeting,
  mockCustomerDashboard,
  type CustomerDashboardData,
  type CustomerTransaction,
} from "../../lib/mock/customer-dashboard";
import { walletApi } from "../../lib/wallet-api";
import { AgentQrPreview } from "../dashboard/agent-qr-preview";
import { PayflowLoader } from "../components/payflow-loader";
import { formatTimestamp, inputClass } from "../dashboard/dashboard-shell";

type Tab = "home" | "send" | "topup" | "withdraw" | "activity" | "profile";
type Message = { type: "success" | "error"; text: string } | null;

function BellIcon() {
  return (
    <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="1.8" aria-hidden="true">
      <path d="M12 3.5a5.5 5.5 0 0 0-5.5 5.5v2.1l-.9 2.7a1 1 0 0 0 .95 1.3h11a1 1 0 0 0 .95-1.3l-.9-2.7V9a5.5 5.5 0 0 0-5.5-5.5Z" />
      <path d="M10 19.5a2 2 0 0 0 4 0" />
    </svg>
  );
}

function SendIcon() {
  return (
    <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="1.8" aria-hidden="true">
      <path d="m4 12 16-7-7 16-2-7-7-2Z" />
    </svg>
  );
}

function PlusIcon() {
  return (
    <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
      <path d="M12 5v14M5 12h14" />
    </svg>
  );
}

function ArrowDownIcon() {
  return (
    <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="1.8" aria-hidden="true">
      <path d="M12 5v14M5 14l7 7 7-7" />
    </svg>
  );
}

function HomeIcon() {
  return (
    <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="1.8" aria-hidden="true">
      <path d="M4 10.5 12 4l8 6.5V20a1 1 0 0 1-1 1h-5v-6H10v6H5a1 1 0 0 1-1-1v-9.5Z" />
    </svg>
  );
}

function HistoryIcon() {
  return (
    <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="1.8" aria-hidden="true">
      <circle cx="12" cy="12" r="8" />
      <path d="M12 8v4l3 2" />
    </svg>
  );
}

function UserIcon() {
  return (
    <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="1.8" aria-hidden="true">
      <circle cx="12" cy="8" r="3.5" />
      <path d="M5 20.5a7 7 0 0 1 14 0" />
    </svg>
  );
}

function transactionIcon(type: CustomerTransaction["type"]) {
  const colors: Record<CustomerTransaction["type"], string> = {
    TRANSFER_IN: "bg-emerald-100 text-emerald-700",
    TRANSFER_OUT: "bg-blue-100 text-[#123c91]",
    DEPOSIT: "bg-orange-100 text-orange-700",
    WITHDRAWAL: "bg-rose-100 text-rose-700",
  };
  const labels: Record<CustomerTransaction["type"], string> = {
    TRANSFER_IN: "↓",
    TRANSFER_OUT: "↑",
    DEPOSIT: "+",
    WITHDRAWAL: "−",
  };
  return (
    <div className={`grid h-11 w-11 shrink-0 place-items-center rounded-2xl text-sm font-black ${colors[type]}`}>
      {labels[type]}
    </div>
  );
}

function TransactionRow({ txn }: { txn: CustomerTransaction }) {
  const isCredit = txn.type === "TRANSFER_IN" || txn.type === "DEPOSIT";
  const sign = isCredit ? "+" : "−";

  return (
    <li className="flex items-center gap-3 rounded-[1.25rem] bg-white px-4 py-3.5 shadow-sm">
      {transactionIcon(txn.type)}
      <div className="min-w-0 flex-1">
        <p className="truncate text-sm font-bold text-slate-900">{txn.counterparty}</p>
        <p className="text-xs font-medium text-slate-500">{formatTimestamp(txn.time)}</p>
      </div>
      <div className="text-right">
        <p className={`text-sm font-black ${isCredit ? "text-emerald-700" : "text-slate-900"}`}>
          {sign}{formatMoney(txn.amount, txn.currency)}
        </p>
        <p className="text-[10px] font-semibold text-slate-400">{txn.method}</p>
      </div>
    </li>
  );
}

function WalletCard({
  session,
  wallet,
  onTopUp,
}: {
  session: DemoSession;
  wallet: CustomerDashboardData["wallet"];
  onTopUp: () => void;
}) {
  const shortId = String(wallet.id).slice(-4);

  return (
    <div className="relative">
      <div className="flex items-center justify-between rounded-[1.75rem] bg-orange-200 px-5 py-4">
        <p className="text-sm font-bold text-orange-950">Top up wallet</p>
        <button
          type="button"
          onClick={onTopUp}
          className="grid h-10 w-10 place-items-center rounded-full bg-white text-slate-900 shadow-md transition-transform hover:scale-105"
          aria-label="Top up wallet"
        >
          <PlusIcon />
        </button>
      </div>

      <div className="-mt-6 mx-1 rounded-[1.75rem] bg-gradient-to-br from-[#123c91] via-[#1649a8] to-[#1e5bb8] p-6 text-white shadow-xl">
        <div className="flex items-start justify-between">
          <img
            src="https://res.cloudinary.com/dflsnes44/image/upload/v1780228196/payflow_no_bg_f0l7on.png"
            alt=""
            className="h-8 w-8 rounded-lg bg-white/10 object-contain p-1"
          />
          <span className="text-sm font-semibold text-white/70">··· {shortId}</span>
        </div>

        <p className="mt-8 text-3xl font-black tracking-tight">
          {formatMoney(wallet.balance, wallet.currency)}
        </p>

        <div className="mt-6 flex items-end justify-between text-sm">
          <div>
            <p className="text-[10px] font-bold uppercase tracking-wide text-white/60">Account holder</p>
            <p className="font-bold">{session.name}</p>
          </div>
          <div className="text-right">
            <p className="text-[10px] font-bold uppercase tracking-wide text-white/60">Wallet</p>
            <p className="font-semibold text-white/90">{wallet.name}</p>
          </div>
        </div>
      </div>
    </div>
  );
}

function QuickActions({
  onSend,
  onTopUp,
  onWithdraw,
  onActivity,
}: {
  onSend: () => void;
  onTopUp: () => void;
  onWithdraw: () => void;
  onActivity: () => void;
}) {
  return (
    <div className="grid grid-cols-[1fr_1fr_auto] gap-3">
      <button
        type="button"
        onClick={onSend}
        className="flex items-center justify-center gap-2 rounded-[1.25rem] bg-[#123c91] px-4 py-3.5 text-sm font-black text-white shadow-md transition-colors hover:bg-[#0d2f76]"
      >
        <SendIcon />
        Send
      </button>
      <button
        type="button"
        onClick={onTopUp}
        className="flex items-center justify-center gap-2 rounded-[1.25rem] bg-orange-400 px-4 py-3.5 text-sm font-black text-orange-950 shadow-md transition-colors hover:bg-orange-500"
      >
        <PlusIcon />
        Top up
      </button>
      <button
        type="button"
        onClick={onActivity}
        className="grid h-[52px] w-[52px] place-items-center rounded-[1.25rem] bg-white text-slate-800 shadow-md"
        aria-label="More actions"
      >
        <div className="grid grid-cols-2 gap-1">
          {[0, 1, 2, 3].map((i) => (
            <span key={i} className="h-1.5 w-1.5 rounded-full bg-slate-700" />
          ))}
        </div>
      </button>
      <button
        type="button"
        onClick={onWithdraw}
        className="col-span-3 flex items-center justify-center gap-2 rounded-[1.25rem] border-2 border-white bg-white/60 px-4 py-3 text-sm font-bold text-[#123c91] shadow-sm backdrop-blur-sm"
      >
        <ArrowDownIcon />
        Withdraw cash
      </button>
    </div>
  );
}

function BottomNav({ tab, onTabChange }: { tab: Tab; onTabChange: (tab: Tab) => void }) {
  const items: { id: Tab; label: string; icon: React.ReactNode }[] = [
    { id: "home", label: "Home", icon: <HomeIcon /> },
    { id: "send", label: "Send", icon: <SendIcon /> },
    { id: "activity", label: "Activity", icon: <HistoryIcon /> },
    { id: "profile", label: "Profile", icon: <UserIcon /> },
  ];

  return (
    <nav className="fixed inset-x-0 bottom-0 z-50 border-t border-blue-100 bg-white/95 px-4 pb-[max(0.75rem,env(safe-area-inset-bottom))] pt-2 backdrop-blur-md">
      <div className="mx-auto flex max-w-lg items-center justify-around">
        {items.map((item) => {
          const active = tab === item.id;
          return (
            <button
              key={item.id}
              type="button"
              onClick={() => onTabChange(item.id)}
              className={`flex flex-col items-center gap-1 rounded-2xl px-4 py-2 transition-colors ${
                active ? "text-[#123c91]" : "text-slate-400"
              }`}
            >
              <span className={active ? "text-[#123c91]" : ""}>{item.icon}</span>
              <span className="text-[10px] font-bold">{item.label}</span>
            </button>
          );
        })}
      </div>
    </nav>
  );
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <label className="block">
      <span className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-500">{label}</span>
      {children}
    </label>
  );
}

function readCustomerSession(): AppSession | null {
  if (typeof window === "undefined") return null;
  return getAuthSession() ?? (DEMO_AUTH_ENABLED ? (() => {
    const demo = getDemoSession();
    return demo ? { ...demo, source: "demo" as const } : null;
  })() : null);
}

export function CustomerAppView() {
  const [session, setSession] = useState<AppSession | null>(readCustomerSession);
  const [data, setData] = useState(
    DEMO_AUTH_ENABLED && getDemoSession() ? mockCustomerDashboard : emptyCustomerDashboard,
  );
  const [tab, setTab] = useState<Tab>("home");
  const [message, setMessage] = useState<Message>(null);

  const [sendForm, setSendForm] = useState({ destination: "", amount: "", note: "" });
  const [topUpForm, setTopUpForm] = useState({ amount: "", method: "mobile_money", phone: "" });
  const [withdrawForm, setWithdrawForm] = useState({ amount: "", method: "agent", phone: "" });
  const [depositQr, setDepositQr] = useState<AgentQrPayload | null>(null);
  const [withdrawalQr, setWithdrawalQr] = useState<AgentQrPayload | null>(null);

  const isApiSession = session?.source === "api" && Boolean(session.accessToken);

  const refreshLiveData = async () => {
    if (!isApiSession) return;
    try {
      const wallets = await walletApi.listWallets();
      const primary = wallets[0];
      if (!primary) {
        setData(emptyCustomerDashboard);
        return;
      }
      const [balance, transactions] = await Promise.all([
        walletApi.getBalance(primary.id),
        walletApi.listTransactions(),
      ]);
      setData({
        phone: session?.email ?? "",
        wallet: {
          id: primary.id,
          name: primary.name,
          currency: primary.currency,
          balance: Number(balance.balance ?? 0),
          status: primary.status as CustomerDashboardData["wallet"]["status"],
          dailyLimit: 50000,
          dailyUsed: 0,
        },
        transactions: transactions.map((t) => ({
          id: String(t.id),
          type: (t.type.includes("TRANSFER") && t.sourceWalletId
            ? "TRANSFER_OUT"
            : t.type.includes("TRANSFER")
              ? "TRANSFER_IN"
              : t.type.includes("DEPOSIT")
                ? "DEPOSIT"
                : "WITHDRAWAL") as CustomerTransaction["type"],
          counterparty: t.description || t.reference,
          amount: Number(t.amount),
          currency: t.currency,
          status: t.status as CustomerTransaction["status"],
          method: "Payflow",
          time: t.createdAt,
        })),
        pendingCount: transactions.filter((t) => t.status === "PENDING").length,
      });
    } catch {
      // Keep current data; user can retry actions.
    }
  };

  useEffect(() => {
    setSession(readCustomerSession());
  }, []);

  useEffect(() => {
    void refreshLiveData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isApiSession]);

  const recentTransactions = useMemo(() => data.transactions.slice(0, 5), [data.transactions]);

  function notify(type: "success" | "error", text: string) {
    setMessage({ type, text });
    setTimeout(() => setMessage(null), 4000);
  }

  async function handleSend(e: React.FormEvent) {
    e.preventDefault();
    const amount = Number(sendForm.amount);
    if (!amount || amount <= 0) return notify("error", "Enter a valid amount");
    if (amount > data.wallet.balance) return notify("error", "Insufficient balance");

    if (isApiSession) {
      try {
        const destId = Number(sendForm.destination);
        if (!Number.isFinite(destId)) return notify("error", "Enter a valid destination wallet ID");
        await walletApi.createTransfer({
          sourceWalletId: data.wallet.id,
          destinationWalletId: destId,
          amount,
          description: sendForm.note || `Transfer to wallet ${destId}`,
          reference: `xfer_${Date.now()}`,
        });
        await refreshLiveData();
        notify("success", `Sent ${formatMoney(amount, data.wallet.currency)} successfully`);
        setSendForm({ destination: "", amount: "", note: "" });
        setTab("home");
      } catch (err) {
        notify("error", err instanceof Error ? err.message : "Transfer failed");
      }
      return;
    }

    if (!DEMO_AUTH_ENABLED) return notify("error", "Please sign in to continue");

    const id = `txn_${9900 + data.transactions.length}`;
    setData((prev) => ({
      ...prev,
      wallet: { ...prev.wallet, balance: prev.wallet.balance - amount, dailyUsed: prev.wallet.dailyUsed + amount },
      transactions: [
        {
          id,
          type: "TRANSFER_OUT",
          counterparty: sendForm.destination || "Payflow user",
          amount,
          currency: prev.wallet.currency,
          status: "COMPLETED",
          method: "Payflow wallet",
          time: new Date().toISOString(),
        },
        ...prev.transactions,
      ],
    }));
    notify("success", `Sent ${formatMoney(amount, data.wallet.currency)} successfully`);
    setSendForm({ destination: "", amount: "", note: "" });
    setTab("home");
  }

  async function handleTopUp(e: React.FormEvent) {
    e.preventDefault();
    const amount = Number(topUpForm.amount);
    if (!amount || amount <= 0) return notify("error", "Enter a valid amount");

    if (isApiSession) {
      try {
        const deposit = await walletApi.createDeposit({
          walletId: data.wallet.id,
          amount,
          currency: data.wallet.currency,
          paymentMethod: topUpForm.method === "agent" ? "AGENT" : "MOBILE_MONEY",
          phoneNumber: topUpForm.phone || undefined,
          description: topUpForm.method === "agent" ? "Agent cash-in" : "Mobile money top-up",
          idempotencyKey: `dep_${Date.now()}`,
        });
        if (topUpForm.method === "agent") {
          setDepositQr(
            buildAgentQrPayload({
              operation: "DEPOSIT",
              reference: deposit.reference,
              walletId: data.wallet.id,
              walletName: data.wallet.name,
              amount: topUpForm.amount,
              currency: data.wallet.currency,
              merchant: session?.name ?? "Customer",
            }),
          );
        }
        await refreshLiveData();
        notify("success", topUpForm.method === "agent" ? `Deposit ${deposit.reference} ready — show QR to an agent` : "Payment started");
        setTopUpForm((prev) => ({ ...prev, amount: "" }));
      } catch (err) {
        notify("error", err instanceof Error ? err.message : "Deposit failed");
      }
      return;
    }

    if (!DEMO_AUTH_ENABLED) return notify("error", "Please sign in to continue");

    if (topUpForm.method === "agent") {
      const id = 1300 + data.transactions.length;
      const reference = `dep_${id}`;
      const qr = buildAgentQrPayload({
        operation: "DEPOSIT",
        reference,
        walletId: data.wallet.id,
        walletName: data.wallet.name,
        amount: topUpForm.amount,
        currency: data.wallet.currency,
        merchant: session?.name ?? "Customer",
      });
      setDepositQr(qr);
      setData((prev) => ({
        ...prev,
        pendingCount: prev.pendingCount + 1,
        transactions: [
          {
            id: `txn_${id}`,
            type: "DEPOSIT",
            counterparty: "Agent cash-in",
            amount,
            currency: prev.wallet.currency,
            status: "AWAITING_AGENT",
            method: "Agent",
            time: qr.createdAt,
          },
          ...prev.transactions,
        ],
      }));
      notify("success", `Deposit ${reference} ready — show QR to an agent`);
    } else {
      setData((prev) => ({
        ...prev,
        wallet: { ...prev.wallet, balance: prev.wallet.balance + amount },
        transactions: [
          {
            id: `txn_${9900 + prev.transactions.length}`,
            type: "DEPOSIT",
            counterparty: "ModemPay",
            amount,
            currency: prev.wallet.currency,
            status: "PENDING",
            method: "Mobile money",
            time: new Date().toISOString(),
          },
          ...prev.transactions,
        ],
      }));
      notify("success", "Payment started — complete it on your phone");
    }
    setTopUpForm((prev) => ({ ...prev, amount: "" }));
  }

  async function handleWithdraw(e: React.FormEvent) {
    e.preventDefault();
    const amount = Number(withdrawForm.amount);
    if (!amount || amount <= 0) return notify("error", "Enter a valid amount");
    if (amount > data.wallet.balance) return notify("error", "Insufficient balance");

    if (isApiSession) {
      try {
        const withdrawal = await walletApi.createWithdrawal({
          walletId: data.wallet.id,
          amount,
          currency: data.wallet.currency,
          paymentMethod: "AGENT",
          description: "Agent cash-out",
          idempotencyKey: `wd_${Date.now()}`,
        });
        setWithdrawalQr(
          buildAgentQrPayload({
            operation: "WITHDRAWAL",
            reference: withdrawal.reference,
            walletId: data.wallet.id,
            walletName: data.wallet.name,
            amount: withdrawForm.amount,
            currency: data.wallet.currency,
            merchant: session?.name ?? "Customer",
          }),
        );
        await refreshLiveData();
        notify("success", `Withdrawal ${withdrawal.reference} ready — show QR to an agent`);
        setWithdrawForm((prev) => ({ ...prev, amount: "" }));
      } catch (err) {
        notify("error", err instanceof Error ? err.message : "Withdrawal failed");
      }
      return;
    }

    if (!DEMO_AUTH_ENABLED) return notify("error", "Please sign in to continue");


    const id = 900 + data.transactions.length;
    const reference = `wd_${id}`;
    const qr = buildAgentQrPayload({
      operation: "WITHDRAWAL",
      reference,
      walletId: data.wallet.id,
      walletName: data.wallet.name,
      amount: withdrawForm.amount,
      currency: data.wallet.currency,
      merchant: session?.name ?? "Customer",
    });
    setWithdrawalQr(qr);
    setData((prev) => ({
      ...prev,
      pendingCount: prev.pendingCount + 1,
      transactions: [
        {
          id: `txn_${id}`,
          type: "WITHDRAWAL",
          counterparty: "Agent cash-out",
          amount,
          currency: prev.wallet.currency,
          status: "AWAITING_AGENT",
          method: "Cash out",
          time: qr.createdAt,
        },
        ...prev.transactions,
      ],
    }));
    notify("success", `Withdrawal ${reference} ready — show QR to an agent`);
    setWithdrawForm((prev) => ({ ...prev, amount: "" }));
    setTab("home");
  }

  function handleLogout() {
    clearAuthSession();
    clearDemoSession();
    window.location.href = "/login";
  }

  if (!session) {
    return <PayflowLoader message="Opening your wallet" submessage="Fetching balance and activity…" />;
  }

  const initials = session.name
    .split(" ")
    .map((part) => part[0])
    .join("")
    .slice(0, 2)
    .toUpperCase();

  return (
    <main className="min-h-screen bg-[#eaf0ff] pb-28 text-slate-900">
      <div className="mx-auto max-w-lg px-4 pt-5">
        {message ? (
          <div
            className={`mb-4 rounded-2xl px-4 py-3 text-sm font-semibold ${
              message.type === "success" ? "bg-emerald-200 text-emerald-950" : "bg-rose-200 text-rose-950"
            }`}
          >
            {message.text}
          </div>
        ) : null}

        {tab === "home" && (
          <div className="space-y-5">
            <header className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="grid h-12 w-12 place-items-center rounded-full bg-[#123c91] text-sm font-black text-white">
                  {initials}
                </div>
                <div>
                  <p className="text-xs font-semibold text-slate-500">{getGreeting()}</p>
                  <p className="text-lg font-black text-slate-900">{session.name}</p>
                </div>
              </div>
              <button
                type="button"
                className="relative grid h-11 w-11 place-items-center rounded-full bg-white text-[#123c91] shadow-md"
                aria-label={`${data.pendingCount} pending notifications`}
              >
                <BellIcon />
                {data.pendingCount > 0 ? (
                  <span className="absolute -right-0.5 -top-0.5 grid h-5 min-w-5 place-items-center rounded-full bg-rose-500 px-1 text-[10px] font-black text-white">
                    {data.pendingCount}
                  </span>
                ) : null}
              </button>
            </header>

            <WalletCard session={session} wallet={data.wallet} onTopUp={() => setTab("topup")} />

            <QuickActions
              onSend={() => setTab("send")}
              onTopUp={() => setTab("topup")}
              onWithdraw={() => setTab("withdraw")}
              onActivity={() => setTab("activity")}
            />

            <section>
              <div className="mb-3 flex items-center justify-between">
                <h2 className="text-base font-black text-slate-900">Recent activity</h2>
                <button type="button" onClick={() => setTab("activity")} className="text-xs font-bold text-[#123c91]">
                  See all
                </button>
              </div>
              <ul className="space-y-2.5">
                {recentTransactions.map((txn) => (
                  <TransactionRow key={txn.id} txn={txn} />
                ))}
              </ul>
            </section>
          </div>
        )}

        {tab === "send" && (
          <div className="space-y-5">
            <header>
              <h1 className="text-xl font-black text-slate-900">Send money</h1>
              <p className="mt-1 text-sm text-slate-600">
                Free wallet-to-wallet transfers. Balance: {formatMoney(data.wallet.balance, data.wallet.currency)}
              </p>
            </header>

            <form onSubmit={handleSend} className="space-y-4 rounded-[1.75rem] bg-white p-5 shadow-md">
              <Field label="Recipient phone or wallet ID">
                <input
                  className={inputClass}
                  placeholder="+220 7XX XXXX or wallet ID"
                  value={sendForm.destination}
                  onChange={(e) => setSendForm((prev) => ({ ...prev, destination: e.target.value }))}
                  required
                />
              </Field>
              <Field label="Amount (GMD)">
                <input
                  className={inputClass}
                  type="number"
                  min="0.01"
                  step="0.01"
                  placeholder="0.00"
                  value={sendForm.amount}
                  onChange={(e) => setSendForm((prev) => ({ ...prev, amount: e.target.value }))}
                  required
                />
              </Field>
              <Field label="Note (optional)">
                <input
                  className={inputClass}
                  placeholder="What's this for?"
                  value={sendForm.note}
                  onChange={(e) => setSendForm((prev) => ({ ...prev, note: e.target.value }))}
                />
              </Field>
              <button
                type="submit"
                className="h-12 w-full rounded-full bg-[#123c91] text-sm font-black text-white shadow-md hover:bg-[#0d2f76]"
              >
                Send now
              </button>
            </form>
          </div>
        )}

        {tab === "topup" && (
          <div className="space-y-5">
            <header>
              <h1 className="text-xl font-black text-slate-900">Top up wallet</h1>
              <p className="mt-1 text-sm text-slate-600">Add money via mobile money or at a Payflow agent.</p>
            </header>

            <form onSubmit={handleTopUp} className="space-y-4 rounded-[1.75rem] bg-white p-5 shadow-md">
              <Field label="Amount (GMD)">
                <input
                  className={inputClass}
                  type="number"
                  min="1"
                  step="0.01"
                  placeholder="0.00"
                  value={topUpForm.amount}
                  onChange={(e) => setTopUpForm((prev) => ({ ...prev, amount: e.target.value }))}
                  required
                />
              </Field>
              <Field label="Payment method">
                <select
                  className={`${inputClass} appearance-none`}
                  value={topUpForm.method}
                  onChange={(e) => setTopUpForm((prev) => ({ ...prev, method: e.target.value }))}
                >
                  <option value="mobile_money">Mobile money (ModemPay)</option>
                  <option value="agent">Cash at agent</option>
                </select>
              </Field>
              {topUpForm.method === "mobile_money" ? (
                <Field label="Phone number">
                  <input
                    className={inputClass}
                    value={topUpForm.phone}
                    onChange={(e) => setTopUpForm((prev) => ({ ...prev, phone: e.target.value }))}
                    required
                  />
                </Field>
              ) : null}
              <button
                type="submit"
                className="h-12 w-full rounded-full bg-orange-500 text-sm font-black text-white shadow-md hover:bg-orange-600"
              >
                {topUpForm.method === "agent" ? "Generate agent QR" : "Pay with mobile money"}
              </button>
            </form>

            {depositQr ? (
              <AgentQrPreview
                payload={depositQr}
                title="Deposit QR code"
                hint="Show this to a Payflow agent to complete your top-up."
              />
            ) : null}
          </div>
        )}

        {tab === "withdraw" && (
          <div className="space-y-5">
            <header>
              <h1 className="text-xl font-black text-slate-900">Withdraw cash</h1>
              <p className="mt-1 text-sm text-slate-600">
                Available: {formatMoney(data.wallet.balance, data.wallet.currency)}
              </p>
            </header>

            <form onSubmit={handleWithdraw} className="space-y-4 rounded-[1.75rem] bg-white p-5 shadow-md">
              <Field label="Amount (GMD)">
                <input
                  className={inputClass}
                  type="number"
                  min="1"
                  step="0.01"
                  placeholder="0.00"
                  value={withdrawForm.amount}
                  onChange={(e) => setWithdrawForm((prev) => ({ ...prev, amount: e.target.value }))}
                  required
                />
              </Field>
              <Field label="Payout method">
                <select
                  className={`${inputClass} appearance-none`}
                  value={withdrawForm.method}
                  onChange={(e) => setWithdrawForm((prev) => ({ ...prev, method: e.target.value }))}
                >
                  <option value="agent">Cash at agent</option>
                  <option value="mobile_money">Mobile money payout</option>
                </select>
              </Field>
              <button
                type="submit"
                className="h-12 w-full rounded-full bg-[#123c91] text-sm font-black text-white shadow-md hover:bg-[#0d2f76]"
              >
                Request withdrawal
              </button>
            </form>

            {withdrawalQr ? (
              <AgentQrPreview
                payload={withdrawalQr}
                title="Withdrawal QR code"
                hint="An agent scans this to hand you cash from your wallet."
              />
            ) : null}
          </div>
        )}

        {tab === "activity" && (
          <div className="space-y-5">
            <header>
              <h1 className="text-xl font-black text-slate-900">All transactions</h1>
              <p className="mt-1 text-sm text-slate-600">Your recent wallet activity</p>
            </header>
            <ul className="space-y-2.5">
              {data.transactions.map((txn) => (
                <TransactionRow key={txn.id} txn={txn} />
              ))}
            </ul>
          </div>
        )}

        {tab === "profile" && (
          <div className="space-y-5">
            <header className="text-center">
              <div className="mx-auto grid h-20 w-20 place-items-center rounded-full bg-[#123c91] text-2xl font-black text-white">
                {initials}
              </div>
              <h1 className="mt-4 text-xl font-black text-slate-900">{session.name}</h1>
              <p className="text-sm text-slate-600">{data.phone}</p>
              <span className="mt-2 inline-block rounded-full bg-blue-100 px-3 py-1 text-xs font-bold text-[#123c91]">
                Customer · Demo
              </span>
            </header>

            <div className="rounded-[1.75rem] bg-white p-5 shadow-md">
              <h2 className="text-sm font-black text-slate-900">Wallet limits</h2>
              <dl className="mt-4 space-y-3 text-sm">
                <div className="flex justify-between">
                  <dt className="text-slate-500">Daily limit</dt>
                  <dd className="font-bold">{formatMoney(data.wallet.dailyLimit, data.wallet.currency)}</dd>
                </div>
                <div className="flex justify-between">
                  <dt className="text-slate-500">Used today</dt>
                  <dd className="font-bold">{formatMoney(data.wallet.dailyUsed, data.wallet.currency)}</dd>
                </div>
                <div className="flex justify-between">
                  <dt className="text-slate-500">Wallet status</dt>
                  <dd className="font-bold text-emerald-700">{data.wallet.status}</dd>
                </div>
              </dl>
            </div>

            <div className="space-y-2">
              <button
                type="button"
                onClick={() => setTab("topup")}
                className="flex w-full items-center justify-between rounded-2xl bg-white px-4 py-3.5 text-sm font-bold shadow-sm"
              >
                Top up wallet
                <span className="text-slate-400">→</span>
              </button>
              <button
                type="button"
                onClick={() => setTab("withdraw")}
                className="flex w-full items-center justify-between rounded-2xl bg-white px-4 py-3.5 text-sm font-bold shadow-sm"
              >
                Withdraw cash
                <span className="text-slate-400">→</span>
              </button>
              <button
                type="button"
                onClick={handleLogout}
                className="flex w-full items-center justify-center rounded-2xl bg-rose-100 px-4 py-3.5 text-sm font-bold text-rose-800"
              >
                Log out
              </button>
            </div>
          </div>
        )}
      </div>

      <BottomNav tab={tab} onTabChange={setTab} />
    </main>
  );
}
