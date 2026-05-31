const navSections = [
  { label: "Overview", href: "#overview" },
  { label: "Authentication", href: "#authentication" },
  { label: "Quickstart", href: "#quickstart" },
  { label: "Core APIs", href: "#core-apis" },
  { label: "Risk and Reconciliation", href: "#risk" },
  { label: "Webhooks and Ops", href: "#ops" },
  { label: "Errors", href: "#errors" },
];

const coreApis = [
  ["Wallets", "POST /api/v1/wallets", "Create, list, inspect, close, freeze, and limit wallets."],
  ["Transfers", "POST /api/v1/transfers", "Execute money movement with idempotency keys and reversal support."],
  ["Transactions", "GET /api/v1/transactions/{id}", "Search, filter, and inspect transaction details by ID or reference."],
  ["Payment links", "POST /api/v1/payment-links", "Create links for checkout or invoice-style flows."],
];

const riskOps = [
  ["Risk evaluation", "POST /api/v1/risk/evaluate", "Score a transaction before committing funds."],
  ["Risk flags", "GET /api/v1/risk/wallets/{walletId}/flags", "Review unresolved, recent, or severity-based flags."],
  ["Reconciliation", "POST /api/v1/reconciliation/manual", "Run manual reconciliation and resolve mismatches."],
  ["Observability", "GET /actuator/health", "Check health, metrics, and runtime status during operations."],
];

const codeSamples = [
  {
    label: "Create a transfer",
    code: `curl -X POST http://localhost:5000/api/v1/transfers \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: 2a2d5c6a-7c9a-4af0-8a26-0d40b31f09c0" \
  -d '{
    "sourceWalletId": 1,
    "destinationWalletId": 2,
    "amount": 2500,
    "currency": "USD",
    "reference": "order_1042"
  }'`,
  },
  {
    label: "Evaluate risk",
    code: `curl -X POST "http://localhost:5000/api/v1/risk/evaluate?walletId=1&amount=2500&transactionId=99"`,
  },
  {
    label: "Inspect wallet balance",
    code: `curl http://localhost:5000/api/v1/wallets/1/balance`,
  },
];

const integrationNotes = [
  "Use the `Idempotency-Key` header for transfers to avoid duplicate execution.",
  "Respect login and refresh rate limits to keep auth flows stable under load.",
  "Treat risk checks as a pre-flight step before finalizing payouts or withdrawals.",
  "Use reconciliation reports to resolve mismatches between your ledger and external processors.",
];

export default function DocsPage() {
  return (
    <main className="min-h-screen bg-slate-50 text-slate-900">
      <header className="sticky top-0 z-30 border-b border-slate-200 bg-white/95 backdrop-blur">
        <div className="mx-auto flex w-[min(1440px,calc(100%-2rem))] items-center gap-4 px-4 py-4 lg:px-6">
          <a href="/" className="flex items-center gap-3">
            <div className="grid h-10 w-10 place-items-center rounded-xl bg-slate-950 text-lg font-black text-white">P</div>
            <div className="leading-tight">
              <p className="text-sm font-black uppercase tracking-[0.24em] text-slate-500">Payflow</p>
              <p className="text-xl font-black text-slate-950">Developer Documentation</p>
            </div>
          </a>

          <div className="hidden flex-1 items-center justify-center lg:flex">
            <label className="w-full max-w-xl">
              <span className="sr-only">Search documentation</span>
              <input
                type="search"
                placeholder="Search docs, endpoints, errors, and webhooks"
                className="w-full rounded-full border border-slate-200 bg-slate-50 px-5 py-3 text-sm outline-none placeholder:text-slate-400 focus:border-orange-400"
              />
            </label>
          </div>

          <div className="ml-auto flex items-center gap-3">
            <span className="hidden rounded-full border border-slate-200 px-4 py-2 text-xs font-bold uppercase tracking-[0.18em] text-slate-500 md:inline-flex">v1.0.0</span>
            <a className="rounded-full bg-slate-950 px-5 py-2.5 text-sm font-bold text-white" href="http://localhost:5000/swagger-ui.html" target="_blank" rel="noreferrer">Swagger</a>
            <a className="hidden rounded-full border border-slate-200 px-5 py-2.5 text-sm font-bold text-slate-700 md:block" href="/">Home</a>
          </div>
        </div>
      </header>

      <section className="mx-auto grid w-[min(1320px,calc(100%-2rem))] gap-6 px-4 py-6 lg:grid-cols-[260px_minmax(0,1fr)] lg:px-6 lg:py-8">
        <aside className="hidden lg:block">
          <div className="sticky top-24 space-y-6 rounded-3xl border border-slate-200 bg-white p-5 shadow-sm">
            <div>
              <p className="text-xs font-bold uppercase tracking-[0.22em] text-orange-600">On this page</p>
              <nav className="mt-4 space-y-1 text-sm font-semibold text-slate-600">
                {navSections.map((item) => (
                  <a key={item.href} href={item.href} className="block rounded-xl px-3 py-2 hover:bg-slate-100 hover:text-slate-950">{item.label}</a>
                ))}
              </nav>
            </div>

            <div className="rounded-2xl bg-slate-950 p-4 text-white">
              <p className="text-xs font-bold uppercase tracking-[0.18em] text-orange-300">Base URL</p>
              <p className="mt-2 font-mono text-sm text-white/80">http://localhost:5000</p>
              <p className="mt-3 text-sm leading-relaxed text-white/70">Use the local backend for Swagger UI, OpenAPI, and operational endpoints.</p>
            </div>

            <div className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
              <p className="text-xs font-bold uppercase tracking-[0.18em] text-slate-500">Focus</p>
              <p className="mt-2 text-sm leading-relaxed text-slate-700">This docs site is intentionally developer-only: API integration, auth, webhook handling, and observability.</p>
            </div>
          </div>
        </aside>

        <main className="mx-auto w-full max-w-5xl space-y-6">
          <section id="overview" className="rounded-3xl border border-slate-200 bg-white p-8 shadow-sm lg:p-10">
            <div className="flex flex-wrap items-center gap-3 text-xs font-bold uppercase tracking-[0.18em] text-slate-500">
              <span>API docs</span>
              <span className="h-1 w-1 rounded-full bg-slate-300" />
              <span>Payments infrastructure</span>
              <span className="h-1 w-1 rounded-full bg-slate-300" />
              <span>Reference-first</span>
            </div>

            <div className="mt-6 grid gap-8 lg:grid-cols-[1.05fr_0.95fr]">
              <div>
                <h1 className="max-w-3xl text-5xl font-black leading-[1.02] text-slate-950 md:text-6xl">Build wallets, transfers, risk checks, and reconciliation with one API surface.</h1>
                <p className="mt-5 max-w-2xl text-lg leading-relaxed text-slate-600">
                  Payflow provides the backend primitives engineers need to launch payment flows: JWT auth, idempotent transfers, wallet limits, risk scoring, reconciliation, and operational monitoring.
                </p>
                <div className="mt-8 flex flex-wrap gap-3">
                  <a href="#quickstart" className="rounded-full bg-orange-600 px-6 py-3 text-sm font-bold text-white">Quickstart</a>
                  <a href="/login" className="rounded-full border border-slate-300 px-6 py-3 text-sm font-bold text-slate-700">Login</a>
                  <a href="http://localhost:5000/v3/api-docs" target="_blank" rel="noreferrer" className="rounded-full border border-slate-300 px-6 py-3 text-sm font-bold text-slate-700">OpenAPI JSON</a>
                </div>
              </div>

              <div className="grid gap-4 rounded-3xl bg-slate-950 p-6 text-white">
                <div className="grid gap-3 sm:grid-cols-2">
                  {[
                    ["Auth", "JWT + roles"],
                    ["Transfers", "Idempotent"],
                    ["Risk", "Rules + flags"],
                    ["Ops", "Health + metrics"],
                  ].map(([label, value]) => (
                    <div key={label} className="rounded-2xl border border-white/10 bg-white/5 p-4">
                      <p className="text-xs font-bold uppercase tracking-[0.18em] text-orange-300">{label}</p>
                      <p className="mt-2 text-lg font-black">{value}</p>
                    </div>
                  ))}
                </div>

                <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
                  <p className="text-xs font-bold uppercase tracking-[0.18em] text-orange-300">Runtime endpoints</p>
                  <div className="mt-3 space-y-2 font-mono text-xs leading-relaxed text-white/75">
                    <p>GET /actuator/health</p>
                    <p>GET /actuator/metrics</p>
                    <p>GET /swagger-ui.html</p>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <section id="authentication" className="rounded-3xl border border-slate-200 bg-white p-8 shadow-sm lg:p-10">
            <p className="text-sm font-bold uppercase tracking-[0.18em] text-orange-600">Authentication</p>
            <h2 className="mt-3 text-3xl font-black text-slate-950">JWT-secured access with rate-limited login flows.</h2>
            <div className="mt-6 grid gap-4 lg:grid-cols-2">
              <article className="rounded-2xl border border-slate-200 bg-slate-50 p-5">
                <p className="text-sm font-bold uppercase tracking-[0.16em] text-slate-500">What to send</p>
                <pre className="mt-3 overflow-x-auto rounded-2xl bg-slate-950 p-4 text-xs leading-relaxed text-emerald-200">
{`Authorization: Bearer <access-token>
Content-Type: application/json`}
                </pre>
              </article>

              <article className="rounded-2xl border border-slate-200 bg-slate-50 p-5">
                <p className="text-sm font-bold uppercase tracking-[0.16em] text-slate-500">Important notes</p>
                <ul className="mt-3 space-y-3 text-sm leading-relaxed text-slate-600">
                  <li>Login and refresh requests are rate limited in the backend configuration.</li>
                  <li>User and admin roles gate the protected risk and reconciliation endpoints.</li>
                  <li>Use short-lived access tokens and refresh them server-side as needed.</li>
                </ul>
              </article>
            </div>
          </section>

          <section id="quickstart" className="rounded-3xl border border-slate-200 bg-white p-8 shadow-sm lg:p-10">
            <p className="text-sm font-bold uppercase tracking-[0.18em] text-orange-600">Quickstart</p>
            <h2 className="mt-3 text-3xl font-black text-slate-950">The fastest route to a working integration.</h2>
            <ol className="mt-6 grid gap-4 lg:grid-cols-4">
              {[
                ["1", "Authenticate", "Create a session and attach the bearer token to every request."],
                ["2", "Create wallet", "Provision a wallet, then query its balance, limits, and history."],
                ["3", "Move funds", "Send an idempotent transfer and support reversal flows."],
                ["4", "Close the loop", "Run risk checks, reconcile mismatches, and consume webhooks."],
              ].map(([step, title, text]) => (
                <li key={step} className="rounded-2xl border border-slate-200 p-5">
                  <p className="text-2xl font-black text-orange-600">{step}</p>
                  <h3 className="mt-3 text-lg font-black text-slate-950">{title}</h3>
                  <p className="mt-2 text-sm leading-relaxed text-slate-600">{text}</p>
                </li>
              ))}
            </ol>
          </section>

          <section id="core-apis" className="rounded-3xl border border-slate-200 bg-white p-8 shadow-sm lg:p-10">
            <p className="text-sm font-bold uppercase tracking-[0.18em] text-orange-600">Core APIs</p>
            <h2 className="mt-3 text-3xl font-black text-slate-950">The endpoints engineers use most.</h2>
            <div className="mt-6 grid gap-4">
              {coreApis.map(([group, endpoint, description]) => (
                <article key={endpoint} className="grid gap-4 rounded-2xl border border-slate-200 p-5 lg:grid-cols-[180px_260px_1fr] lg:items-center">
                  <div>
                    <p className="text-xs font-bold uppercase tracking-[0.18em] text-slate-500">{group}</p>
                  </div>
                  <div>
                    <span className="inline-flex rounded-full bg-slate-950 px-3 py-1 font-mono text-xs font-bold text-white">{endpoint}</span>
                  </div>
                  <p className="text-sm leading-relaxed text-slate-600">{description}</p>
                </article>
              ))}
            </div>
          </section>

          <section id="risk" className="grid gap-6 lg:grid-cols-2">
            <article className="rounded-3xl border border-slate-200 bg-white p-8 shadow-sm lg:p-10">
              <p className="text-sm font-bold uppercase tracking-[0.18em] text-orange-600">Risk and reconciliation</p>
              <h2 className="mt-3 text-3xl font-black text-slate-950">Pre-flight money movement before it lands in production.</h2>
              <div className="mt-6 space-y-4">
                {riskOps.map(([group, endpoint, description]) => (
                  <div key={endpoint} className="rounded-2xl bg-slate-50 p-4">
                    <p className="text-xs font-bold uppercase tracking-[0.16em] text-slate-500">{group}</p>
                    <p className="mt-2 font-mono text-sm font-bold text-slate-950">{endpoint}</p>
                    <p className="mt-2 text-sm leading-relaxed text-slate-600">{description}</p>
                  </div>
                ))}
              </div>
            </article>

            <article className="rounded-3xl border border-slate-200 bg-slate-950 p-8 text-white shadow-sm lg:p-10">
              <p className="text-sm font-bold uppercase tracking-[0.18em] text-orange-300">Sample request</p>
              <h2 className="mt-3 text-3xl font-black">Transfer with idempotency.</h2>
              <pre className="mt-6 overflow-x-auto rounded-2xl bg-black/30 p-5 text-xs leading-relaxed text-emerald-200">
{`curl -X POST http://localhost:5000/api/v1/transfers \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: 2a2d5c6a-7c9a-4af0-8a26-0d40b31f09c0" \
  -d '{
    "sourceWalletId": 1,
    "destinationWalletId": 2,
    "amount": 2500,
    "currency": "USD",
    "reference": "order_1042"
  }'`}
              </pre>
            </article>
          </section>

          <section id="ops" className="rounded-3xl border border-slate-200 bg-white p-8 shadow-sm lg:p-10">
            <p className="text-sm font-bold uppercase tracking-[0.18em] text-orange-600">Webhooks and ops</p>
            <h2 className="mt-3 text-3xl font-black text-slate-950">Keep downstream services in sync.</h2>
            <div className="mt-6 grid gap-4 lg:grid-cols-3">
              {[
                ["Webhooks", "Configure webhook handlers for payment events and integrate them into your backend pipeline."],
                ["Monitoring", "Use health and metrics endpoints to validate readiness, performance, and error rates."],
                ["Docs", "Swagger UI and the OpenAPI document are the fastest way to explore request and response shapes."],
              ].map(([title, text]) => (
                <article key={title} className="rounded-2xl border border-slate-200 bg-slate-50 p-5">
                  <h3 className="text-lg font-black text-slate-950">{title}</h3>
                  <p className="mt-2 text-sm leading-relaxed text-slate-600">{text}</p>
                </article>
              ))}
            </div>

            <div className="mt-6 grid gap-3 text-sm font-semibold text-slate-700 lg:grid-cols-2">
              {integrationNotes.map((note) => (
                <div key={note} className="rounded-2xl border border-slate-200 px-4 py-3">{note}</div>
              ))}
            </div>
          </section>

          <section id="errors" className="rounded-3xl border border-slate-200 bg-white p-8 shadow-sm lg:p-10">
            <p className="text-sm font-bold uppercase tracking-[0.18em] text-orange-600">Errors and limits</p>
            <h2 className="mt-3 text-3xl font-black text-slate-950">Plan for failure cases explicitly.</h2>
            <div className="mt-6 grid gap-4 lg:grid-cols-2">
              {[
                ["401 / 403", "Missing token, expired token, or a role that cannot access the endpoint."],
                ["404", "Resource not found: wallet, transaction, flag, or reconciliation report."],
                ["409", "Duplicate execution or a transaction state conflict."],
                ["429", "Rate limit exceeded for login, refresh, or keyed API usage."],
              ].map(([code, text]) => (
                <article key={code} className="rounded-2xl border border-slate-200 p-5">
                  <p className="font-mono text-sm font-black text-slate-950">{code}</p>
                  <p className="mt-2 text-sm leading-relaxed text-slate-600">{text}</p>
                </article>
              ))}
            </div>
          </section>
        </main>
      </section>
    </main>
  );
}