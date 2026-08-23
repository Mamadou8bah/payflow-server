"use client";

import { useMemo, useState, useEffect } from "react";
import { DashboardShell, formatTimestamp, inputClass, PanelCard, PrimaryButton } from "./dashboard-shell";
import { DashboardHeroCard, DonutChart, LineAreaChart, Sparkline } from "./dashboard-charts";
import { DashboardToolbar, KpiCard } from "./dashboard-toolbar";
import { DataTable } from "./data-table";
import { downloadCsv, type DateRangeKey } from "./dashboard-utils";
import { merchantTabs } from "./dashboard-tabs";
import { AgentQrPreview, PrimaryWalletBanner } from "./agent-qr-preview";
import { buildAgentQrPayload, type AgentQrPayload } from "../../lib/agent-qr";
import { mockMerchantDashboard } from "../../lib/mock/merchant-dashboard";
import { getMerchantSession } from "../../lib/merchant-api";
import { hasLiveMerchantApi, loadMerchantDashboardFromApi } from "../../lib/merchant-dashboard-loader";
import { walletApi } from "../../lib/wallet-api";
type Tab = "overview" | "wallets" | "money" | "payments" | "links" | "subscriptions" | "webhooks";
type Message = { type: "success" | "error"; text: string } | null;

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <label className="block">
      <span className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-500">{label}</span>
      {children}
    </label>
  );
}

export function MerchantDashboardView() {
  const [data, setData] = useState(mockMerchantDashboard);
  const [tab, setTab] = useState<Tab>("overview");
  const [message, setMessage] = useState<Message>(null);

  const [walletForm, setWalletForm] = useState({ name: "", currency: "GMD" });
  const [depositForm, setDepositForm] = useState({ amount: "" });
  const [withdrawalForm, setWithdrawalForm] = useState({ amount: "" });
  const [depositQr, setDepositQr] = useState<AgentQrPayload | null>(null);
  const [withdrawalQr, setWithdrawalQr] = useState<AgentQrPayload | null>(null);
  const [transferForm, setTransferForm] = useState({ to: "", amount: "" });
  const [linkForm, setLinkForm] = useState({ title: "", amount: "" });
  const [subscriptionForm, setSubscriptionForm] = useState({ plan: "", amount: "", customer: "" });
  const [chartRange, setChartRange] = useState<DateRangeKey>("7d");
  const [search, setSearch] = useState("");

  const primaryWallet = useMemo(
    () => data.wallets.find((w) => w.status === "ACTIVE") ?? data.wallets[0],
    [data.wallets],
  );

  function notify(type: "success" | "error", text: string) {
    setMessage({ type, text });
    setTimeout(() => setMessage(null), 4000);
  }

  function handleCreateWallet(e: React.FormEvent) {
    e.preventDefault();
    if (liveApi) {
      void walletApi.createWallet(walletForm.name, walletForm.currency)
        .then(() => refreshFromApi())
        .then(() => {
          notify("success", "Wallet created successfully");
          setWalletForm({ name: "", currency: "GMD" });
        })
        .catch((err) => notify("error", err instanceof Error ? err.message : "Wallet creation failed"));
      return;
    }
    const id = 7700 + data.wallets.length;
    setData((prev) => ({
      ...prev,
      wallets: [...prev.wallets, { id, name: walletForm.name, currency: walletForm.currency, balance: "GMD 0", status: "ACTIVE", dailyLimit: "GMD 100,000", dailyUsed: "GMD 0" }],
    }));
    notify("success", `Wallet ${id} created successfully`);
    setWalletForm({ name: "", currency: "GMD" });
  }

  function handleCreateDeposit(e: React.FormEvent) {
    e.preventDefault();
    if (!primaryWallet) return notify("error", "No active wallet found");
    if (liveApi) {
      void walletApi.createDeposit({
        walletId: primaryWallet.id,
        amount: Number(depositForm.amount),
        currency: primaryWallet.currency,
        paymentMethod: "AGENT",
        description: "Merchant deposit",
      })
        .then((deposit) => {
          const qr = buildAgentQrPayload({
            operation: "DEPOSIT",
            reference: deposit.reference,
            walletId: primaryWallet.id,
            walletName: primaryWallet.name,
            amount: depositForm.amount,
            currency: primaryWallet.currency,
            merchant: data.storeName,
          });
          setDepositQr(qr);
          notify("success", `Deposit ${deposit.reference} ready — show the QR code to an agent`);
          setDepositForm({ amount: "" });
          return refreshFromApi();
        })
        .catch((err) => notify("error", err instanceof Error ? err.message : "Deposit failed"));
      return;
    }
    const id = 1200 + data.deposits.length;
    const reference = `dep_${id}`;
    const qr = buildAgentQrPayload({
      operation: "DEPOSIT",
      reference,
      walletId: primaryWallet.id,
      walletName: primaryWallet.name,
      amount: depositForm.amount,
      currency: primaryWallet.currency,
      merchant: data.storeName,
    });
    setDepositQr(qr);
    setData((prev) => ({
      ...prev,
      deposits: [{
        id,
        walletId: primaryWallet.id,
        amount: `${primaryWallet.currency} ${depositForm.amount}`,
        status: "AWAITING_AGENT",
        reference,
        time: qr.createdAt,
      }, ...prev.deposits],
    }));
    notify("success", `Deposit ${reference} ready — show the QR code to an agent`);
    setDepositForm({ amount: "" });
  }

  function handleCreateWithdrawal(e: React.FormEvent) {
    e.preventDefault();
    if (!primaryWallet) return notify("error", "No active wallet found");
    if (liveApi) {
      void walletApi.createWithdrawal({
        walletId: primaryWallet.id,
        amount: Number(withdrawalForm.amount),
        currency: primaryWallet.currency,
        paymentMethod: "AGENT",
        description: "Merchant withdrawal",
      })
        .then((withdrawal) => {
          const qr = buildAgentQrPayload({
            operation: "WITHDRAWAL",
            reference: withdrawal.reference,
            walletId: primaryWallet.id,
            walletName: primaryWallet.name,
            amount: withdrawalForm.amount,
            currency: primaryWallet.currency,
            merchant: data.storeName,
          });
          setWithdrawalQr(qr);
          notify("success", `Withdrawal ${withdrawal.reference} ready — show the QR code to an agent`);
          setWithdrawalForm({ amount: "" });
          return refreshFromApi();
        })
        .catch((err) => notify("error", err instanceof Error ? err.message : "Withdrawal failed"));
      return;
    }
    const id = 800 + data.withdrawals.length;
    const reference = `wd_${id}`;
    const qr = buildAgentQrPayload({
      operation: "WITHDRAWAL",
      reference,
      walletId: primaryWallet.id,
      walletName: primaryWallet.name,
      amount: withdrawalForm.amount,
      currency: primaryWallet.currency,
      merchant: data.storeName,
    });
    setWithdrawalQr(qr);
    setData((prev) => ({
      ...prev,
      withdrawals: [{
        id,
        walletId: primaryWallet.id,
        amount: `${primaryWallet.currency} ${withdrawalForm.amount}`,
        status: "AWAITING_AGENT",
        reference,
        time: qr.createdAt,
      }, ...prev.withdrawals],
    }));
    notify("success", `Withdrawal ${reference} ready — show the QR code to an agent`);
    setWithdrawalForm({ amount: "" });
  }
  function handleTransfer(e: React.FormEvent) {
    e.preventDefault();
    if (!primaryWallet) return notify("error", "No active wallet found");
    if (liveApi) {
      void walletApi.createTransfer({
        sourceWalletId: primaryWallet.id,
        destinationWalletId: Number(transferForm.to),
        amount: Number(transferForm.amount),
        description: "Merchant transfer",
      })
        .then(() => {
          notify("success", "Transfer completed successfully");
          setTransferForm({ to: "", amount: "" });
          return refreshFromApi();
        })
        .catch((err) => notify("error", err instanceof Error ? err.message : "Transfer failed"));
      return;
    }
    const id = 99200 + data.transfers.length;
    setData((prev) => ({
      ...prev,
      transfers: [{ id, from: primaryWallet.id, to: Number(transferForm.to), amount: `${primaryWallet.currency} ${transferForm.amount}`, status: "COMPLETED", reference: `pf_${id}`, time: new Date().toISOString() }, ...prev.transfers],
    }));
    notify("success", `Transfer ${id} completed successfully`);
    setTransferForm({ to: "", amount: "" });
  }

  function handleCreateLink(e: React.FormEvent) {
    e.preventDefault();
    if (liveApi) {
      if (!primaryWallet) return notify("error", "No active wallet found");
      void walletApi.createPaymentLink({
        walletId: primaryWallet.id,
        amount: Number(linkForm.amount),
        currency: primaryWallet.currency,
        description: linkForm.title,
      })
        .then((link) => {
          notify("success", `Payment link ${link.reference} created`);
          setLinkForm({ title: "", amount: "" });
          return refreshFromApi();
        })
        .catch((err) => notify("error", err instanceof Error ? err.message : "Payment link creation failed"));
      return;
    }
    const id = `link_${100 + data.paymentLinks.length}`;
    setData((prev) => ({
      ...prev,
      paymentLinks: [{ id, title: linkForm.title, amount: `GMD ${linkForm.amount}`, status: "active", uses: 0 }, ...prev.paymentLinks],
    }));
    notify("success", `Payment link ${id} created`);
    setLinkForm({ title: "", amount: "" });
  }

  function handleCreateSubscription(e: React.FormEvent) {
    e.preventDefault();
    if (liveApi) {
      notify("error", "Subscriptions are not available via API yet");
      return;
    }
    const id = 300 + data.subscriptions.length;
    setData((prev) => ({
      ...prev,
      subscriptions: [{ id, plan: subscriptionForm.plan, amount: `GMD ${subscriptionForm.amount}`, status: "ACTIVE", customer: subscriptionForm.customer, nextBilling: "2026-07-27" }, ...prev.subscriptions],
    }));
    notify("success", `Subscription ${id} created`);
    setSubscriptionForm({ plan: "", amount: "", customer: "" });
  }

  const [isPendingReview, setIsPendingReview] = useState(false);

  const liveApi = hasLiveMerchantApi();

  async function refreshFromApi() {
    if (!liveApi) return;
    try {
      const loaded = await loadMerchantDashboardFromApi();
      setData(loaded);
      notify("success", "Dashboard refreshed from API");
    } catch (err) {
      notify("error", err instanceof Error ? err.message : "Failed to load dashboard");
    }
  }

  useEffect(() => {
    const session = getMerchantSession();
    setIsPendingReview(session?.userStatus === "PENDING_REVIEW");
    if (liveApi) {
      void refreshFromApi();
    }
  }, []);

  return (
    <DashboardShell tabs={merchantTabs} activeTab={tab} onTabChange={(id) => setTab(id as Tab)} message={message}>
      {isPendingReview ? (
        <div className="mb-5 rounded-2xl bg-amber-50 px-4 py-3 text-sm text-amber-950">
          <p className="font-bold">Application under review</p>
          <p className="mt-1">Collections are disabled until Payflow approves your Gambian merchant account. <a href="/merchants/pending" className="font-bold underline">View status</a></p>
        </div>
      ) : null}
      {tab === "overview" && (
        <div className="space-y-5">
          <DashboardToolbar
            search={search}
            onSearchChange={setSearch}
            searchPlaceholder="Search payments, links, customers…"
            dateRange={chartRange}
            onDateRangeChange={setChartRange}
            onDownload={() =>
              downloadCsv("payflow-merchant-overview", ["Metric", "Value"], data.stats.map((s) => [s.label, s.value]))
            }
            onRefresh={() => (liveApi ? void refreshFromApi() : notify("success", "Dashboard refreshed"))}
          />

          <div className="grid gap-5 xl:grid-cols-[1.15fr_0.85fr]">
            <div className="space-y-5 min-w-0">
              <DashboardHeroCard
                label="Today's collections"
                value={data.stats[0]?.value ?? "—"}
                hint={data.stats[0]?.hint}
                footerLeft={data.storeName}
                footerRight={data.stats[1]?.value + " pending"}
                progress={88}
                progressLabel={`Settlement progress · ${data.stats[1]?.hint}`}
              />

              <PanelCard title="Collections" subtitle={`GMD thousands · ${chartRange}`} className="min-w-0 overflow-hidden">
                <LineAreaChart data={data.charts.collectionsByRange[chartRange]} valuePrefix="GMD " valueSuffix="K" height={220} />
              </PanelCard>
            </div>

            <div className="space-y-5 min-w-0">
              <div className="grid gap-4 sm:grid-cols-2">
                {data.stats.slice(1).map((item, i) => (
                  <KpiCard
                    key={item.label}
                    label={item.label}
                    value={item.value}
                    change={item.hint}
                    changePositive={i === 0}
                    sparkline={i === 1 ? <Sparkline values={data.charts.kpiSparklines.links} color="#123c91" /> : undefined}
                  />
                ))}
              </div>
              <PanelCard title="Payment status" subtitle="Last 30 days">
                <DonutChart data={data.charts.paymentStatus} />
              </PanelCard>
            </div>
          </div>

          <div className="grid gap-5 xl:grid-cols-[1.15fr_0.85fr]">
            <PanelCard title="Create payment link" subtitle="Quick checkout for customers">
              <form className="space-y-3" onSubmit={handleCreateLink}>
                <Field label="Title"><input className={inputClass} value={linkForm.title} onChange={(e) => setLinkForm({ ...linkForm, title: e.target.value })} required /></Field>
                <Field label="Amount (GMD)"><input className={inputClass} value={linkForm.amount} onChange={(e) => setLinkForm({ ...linkForm, amount: e.target.value })} required /></Field>
                <PrimaryButton type="submit">New payment link</PrimaryButton>
              </form>
            </PanelCard>
            <PanelCard title="Recent payments" subtitle="Latest collections">
              <ul className="space-y-3">
                {data.recentPayments.slice(0, 4).map((p) => (
                  <li key={p.id} className="flex items-center justify-between rounded-2xl bg-slate-50 px-4 py-3">
                    <span>
                      <span className="block text-sm font-bold text-slate-900">{p.customer}</span>
                      <span className="text-xs text-slate-500">{p.id}</span>
                    </span>
                    <span className="text-sm font-bold text-[#123c91]">{p.amount}</span>
                  </li>
                ))}
              </ul>
            </PanelCard>
          </div>
        </div>
      )}

      {tab === "wallets" && (
        <div className="grid gap-5 xl:grid-cols-[1.15fr_0.85fr]">
          <DataTable
            title="Your wallets"
            subtitle="Your collection wallets"
            exportFilename="payflow-merchant-wallets"
            searchPlaceholder="Search wallets by name, ID, status…"
            rows={data.wallets as unknown as Record<string, unknown>[]}
            columns={[
              { key: "id", label: "ID", render: (r) => <span className="font-bold text-[#123c91]">#{String(r.id)}</span> },
              { key: "name", label: "Name" },
              { key: "balance", label: "Balance" },
              { key: "dailyLimit", label: "Daily limit", render: (r) => `${String(r.dailyUsed)} / ${String(r.dailyLimit)}`, exportValue: (r) => `${String(r.dailyUsed)} / ${String(r.dailyLimit)}` },
              { key: "status", label: "Status", render: (r) => <span className="capitalize">{String(r.status).toLowerCase()}</span> },
            ]}
          />
          <div className="space-y-5">
            <PanelCard title="Create wallet" subtitle="Add a GMD or USD wallet">
              <form className="space-y-3" onSubmit={handleCreateWallet}>
                <Field label="Name"><input className={inputClass} value={walletForm.name} onChange={(e) => setWalletForm({ ...walletForm, name: e.target.value })} required /></Field>
                <Field label="Currency">
                  <select className={inputClass} value={walletForm.currency} onChange={(e) => setWalletForm({ ...walletForm, currency: e.target.value })}>
                    <option value="GMD">GMD</option>
                    <option value="USD">USD</option>
                  </select>
                </Field>
                <PrimaryButton type="submit">Create wallet</PrimaryButton>
              </form>
            </PanelCard>
            <PanelCard title="Wallet tools" subtitle="Balance, limits, and account controls">
              <ul className="space-y-2 text-sm text-slate-600">
                <li>View balance and daily limits</li>
                <li>Adjust spending limits when needed</li>
                <li>Close a wallet you no longer use</li>
              </ul>
            </PanelCard>
          </div>
        </div>
      )}

      {tab === "money" && (
        <div className="space-y-5">
          {primaryWallet ? (
            <PanelCard title="Money operations" subtitle="Deposits and withdrawals use your primary wallet automatically">
              <PrimaryWalletBanner
                name={primaryWallet.name}
                id={primaryWallet.id}
                balance={primaryWallet.balance}
                currency={primaryWallet.currency}
              />
            </PanelCard>
          ) : null}

          <div className="grid gap-5 xl:grid-cols-2">
            <PanelCard title="Create deposit" subtitle="Agent-assisted · show QR at a Payflow agent">
              <form className="space-y-3" onSubmit={handleCreateDeposit}>
                <Field label="Amount">
                  <input
                    className={inputClass}
                    type="number"
                    min="1"
                    step="0.01"
                    value={depositForm.amount}
                    onChange={(e) => setDepositForm({ ...depositForm, amount: e.target.value })}
                    placeholder={primaryWallet ? `Amount in ${primaryWallet.currency}` : "Amount"}
                    required
                  />
                </Field>
                <PrimaryButton type="submit">Generate deposit QR</PrimaryButton>
              </form>
              {depositQr ? (
                <div className="mt-5">
                  <AgentQrPreview
                    payload={depositQr}
                    title="Deposit QR code"
                    hint="Take cash to a Payflow agent and show this code"
                  />
                </div>
              ) : null}
            </PanelCard>

            <PanelCard title="Create withdrawal" subtitle="Agent-assisted · collect cash at a Payflow agent">
              <form className="space-y-3" onSubmit={handleCreateWithdrawal}>
                <Field label="Amount">
                  <input
                    className={inputClass}
                    type="number"
                    min="1"
                    step="0.01"
                    value={withdrawalForm.amount}
                    onChange={(e) => setWithdrawalForm({ ...withdrawalForm, amount: e.target.value })}
                    placeholder={primaryWallet ? `Amount in ${primaryWallet.currency}` : "Amount"}
                    required
                  />
                </Field>
                <PrimaryButton type="submit">Generate withdrawal QR</PrimaryButton>
              </form>
              {withdrawalQr ? (
                <div className="mt-5">
                  <AgentQrPreview
                    payload={withdrawalQr}
                    title="Withdrawal QR code"
                    hint="Show this at a Payflow agent to receive your cash"
                  />
                </div>
              ) : null}
            </PanelCard>
          </div>

          <PanelCard title="Transfer funds" subtitle="Send from your primary wallet to another wallet">
            <form className="mx-auto grid max-w-2xl gap-3 sm:grid-cols-2" onSubmit={handleTransfer}>
              <Field label="To wallet"><input className={inputClass} value={transferForm.to} onChange={(e) => setTransferForm({ ...transferForm, to: e.target.value })} required /></Field>
              <Field label="Amount"><input className={inputClass} type="number" min="1" step="0.01" value={transferForm.amount} onChange={(e) => setTransferForm({ ...transferForm, amount: e.target.value })} placeholder={primaryWallet ? `Amount in ${primaryWallet.currency}` : "Amount"} required /></Field>
              <div className="sm:col-span-2">
                <PrimaryButton type="submit">Send transfer</PrimaryButton>
              </div>
            </form>
          </PanelCard>

          <div className="grid gap-5 lg:grid-cols-3">
            <DataTable
              title="Deposits"
              subtitle="Agent scan required to complete"
              exportFilename="payflow-merchant-deposits"
              dateKey="time"
              pageSize={5}
              rows={data.deposits as unknown as Record<string, unknown>[]}
              columns={[
                { key: "reference", label: "Reference" },
                { key: "amount", label: "Amount" },
                { key: "status", label: "Status", render: (r) => <span className="font-semibold text-amber-800">{String(r.status).replace(/_/g, " ")}</span> },
                { key: "time", label: "Time", render: (r) => formatTimestamp(String(r.time)), exportValue: (r) => formatTimestamp(String(r.time)) },
              ]}
            />
            <DataTable
              title="Withdrawals"
              subtitle="Agent scan required to complete"
              exportFilename="payflow-merchant-withdrawals"
              dateKey="time"
              pageSize={5}
              rows={data.withdrawals as unknown as Record<string, unknown>[]}
              columns={[
                { key: "reference", label: "Reference" },
                { key: "amount", label: "Amount" },
                { key: "status", label: "Status", render: (r) => <span className="font-semibold text-amber-800">{String(r.status).replace(/_/g, " ")}</span> },
                { key: "time", label: "Time", render: (r) => formatTimestamp(String(r.time)), exportValue: (r) => formatTimestamp(String(r.time)) },
              ]}
            />            <DataTable
              title="Transfers"
              subtitle="Between your wallets"
              exportFilename="payflow-merchant-transfers"
              dateKey="time"
              pageSize={5}
              rows={data.transfers as unknown as Record<string, unknown>[]}
              columns={[
                { key: "id", label: "ID", render: (r) => `#${String(r.id)}` },
                { key: "from", label: "Route", render: (r) => `${String(r.from)}→${String(r.to)}`, exportValue: (r) => `${String(r.from)}→${String(r.to)}` },
                { key: "amount", label: "Amount" },
                { key: "status", label: "Status" },
              ]}
            />
          </div>
        </div>
      )}

      {tab === "payments" && (
        <DataTable
          title="Payments"
          subtitle="Customer collections"
          exportFilename="payflow-merchant-payments"
          searchPlaceholder="Search by customer, payment ID, status…"
          dateKey="time"
          rows={data.recentPayments as unknown as Record<string, unknown>[]}
          columns={[
            { key: "id", label: "Payment" },
            { key: "customer", label: "Customer" },
            { key: "amount", label: "Amount" },
            { key: "status", label: "Status", render: (r) => <span className="capitalize">{String(r.status)}</span> },
            { key: "time", label: "Time", render: (r) => formatTimestamp(String(r.time)), exportValue: (r) => formatTimestamp(String(r.time)) },
          ]}
        />
      )}

      {tab === "links" && (
        <div className="grid gap-5 xl:grid-cols-[1.15fr_0.85fr]">
          <DataTable
            title="Payment links"
            subtitle="Active checkout links"
            exportFilename="payflow-merchant-payment-links"
            searchPlaceholder="Search links by title, ID, status…"
            rows={data.paymentLinks as unknown as Record<string, unknown>[]}
            columns={[
              { key: "id", label: "Link", render: (r) => <span className="font-semibold text-[#123c91]">{String(r.id)}</span> },
              { key: "title", label: "Title" },
              { key: "amount", label: "Amount" },
              { key: "uses", label: "Uses" },
              { key: "status", label: "Status", render: (r) => <span className="capitalize">{String(r.status)}</span> },
            ]}
          />
          <PanelCard title="Create payment link" subtitle="Quick checkout for customers">
            <form className="space-y-3" onSubmit={handleCreateLink}>
              <Field label="Title"><input className={inputClass} value={linkForm.title} onChange={(e) => setLinkForm({ ...linkForm, title: e.target.value })} required /></Field>
              <Field label="Amount (GMD)"><input className={inputClass} value={linkForm.amount} onChange={(e) => setLinkForm({ ...linkForm, amount: e.target.value })} required /></Field>
              <PrimaryButton type="submit">Create link</PrimaryButton>
            </form>
          </PanelCard>
        </div>
      )}

      {tab === "subscriptions" && (
        <div className="grid gap-5 xl:grid-cols-[1.15fr_0.85fr]">
          <DataTable
            title="Subscriptions"
            subtitle="Recurring customer billing"
            exportFilename="payflow-merchant-subscriptions"
            searchPlaceholder="Search by plan, customer, status…"
            rows={data.subscriptions as unknown as Record<string, unknown>[]}
            columns={[
              { key: "id", label: "ID", render: (r) => <span className="font-bold">#{String(r.id)}</span> },
              { key: "plan", label: "Plan" },
              { key: "customer", label: "Customer" },
              { key: "amount", label: "Amount" },
              { key: "nextBilling", label: "Next billing" },
              { key: "status", label: "Status", render: (r) => <span className="capitalize">{String(r.status).toLowerCase()}</span> },
            ]}
          />
          <PanelCard title="Create subscription" subtitle="Bill customers on a schedule">
            <form className="space-y-3" onSubmit={handleCreateSubscription}>
              <Field label="Plan name"><input className={inputClass} value={subscriptionForm.plan} onChange={(e) => setSubscriptionForm({ ...subscriptionForm, plan: e.target.value })} required /></Field>
              <Field label="Amount (GMD)"><input className={inputClass} value={subscriptionForm.amount} onChange={(e) => setSubscriptionForm({ ...subscriptionForm, amount: e.target.value })} required /></Field>
              <Field label="Customer"><input className={inputClass} value={subscriptionForm.customer} onChange={(e) => setSubscriptionForm({ ...subscriptionForm, customer: e.target.value })} required /></Field>
              <PrimaryButton type="submit">Create subscription</PrimaryButton>
            </form>
          </PanelCard>
        </div>
      )}

      {tab === "webhooks" && (
        <DataTable
          title="Webhook deliveries"
          subtitle="Payment status callbacks"
          exportFilename="payflow-merchant-webhooks"
          dateKey="time"
          searchPlaceholder="Search by event, ID, status…"
          rows={data.webhooks as unknown as Record<string, unknown>[]}
          columns={[
            { key: "id", label: "ID", render: (r) => <span className="font-semibold">{String(r.id)}</span> },
            { key: "event", label: "Event" },
            { key: "status", label: "Status", render: (r) => <span className="capitalize">{String(r.status)}</span> },
            { key: "time", label: "Time", render: (r) => formatTimestamp(String(r.time)), exportValue: (r) => formatTimestamp(String(r.time)) },
          ]}
        />
      )}
    </DashboardShell>
  );
}
