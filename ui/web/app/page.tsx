const platformCards = [
  {
    title: "Consumer Wallet",
    description: "Send money, receive money, transfer between wallets, and pay merchants or bills from one account."
  },
  {
    title: "Merchant Console",
    description: "Accept online payments, generate payment links, track transactions, and manage payouts."
  },
  {
    title: "Admin Operations",
    description: "Review activity, monitor compliance, resolve disputes, and manage platform health."
  },
  {
    title: "Developer Portal",
    description: "Integrate collections, payouts, wallet creation, webhooks, subscriptions, and verification APIs."
  }
];

const developerApis = [
  "POST /api/v1/deposits",
  "POST /api/v1/withdrawals",
  "POST /api/v1/wallets",
  "GET /api/v1/transactions/reference",
  "POST /api/v1/subscriptions",
  "POST /api/v1/webhooks/modem-pay/charges"
];

export default function HomePage() {
  return (
    <main className="page-shell">
      <section className="hero">
        <div className="eyebrow">Payflow Platform</div>
        <h1>Payments for consumers, merchants, admins, and developers.</h1>
        <p>
          A clean, high-trust payment experience spanning wallet operations, merchant collections,
          payouts, subscriptions, and webhook-driven automation.
        </p>
        <div className="hero-actions">
          <a href="#platform">Explore the platform</a>
          <a href="#developer" className="secondary">Developer APIs</a>
        </div>
      </section>

      <section className="grid-section" id="platform">
        {platformCards.map((card) => (
          <article key={card.title} className="card">
            <h2>{card.title}</h2>
            <p>{card.description}</p>
          </article>
        ))}
      </section>

      <section className="split-section">
        <article className="panel">
          <div className="section-label">Marketing</div>
          <h2>Launch pages that convert and explain the value fast.</h2>
          <p>
            Use this area for pricing, product proof, onboarding steps, FAQs, and calls to action.
          </p>
        </article>

        <article className="panel accent" id="admin">
          <div className="section-label">Admin</div>
          <h2>Admin and support workflows in one secure place.</h2>
          <p>
            Add dashboard metrics, approvals, disputes, risk signals, and account controls here.
          </p>
        </article>
      </section>

      <section className="developer" id="developer">
        <div>
          <div className="section-label">Developer</div>
          <h2>Integration-first APIs for collections, payouts, wallets, and webhooks.</h2>
        </div>
        <div className="api-list">
          {developerApis.map((api) => (
            <code key={api}>{api}</code>
          ))}
        </div>
      </section>
    </main>
  );
}
