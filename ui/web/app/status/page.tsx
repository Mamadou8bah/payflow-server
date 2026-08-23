import { ContentBlock, SitePage } from "../site-page";

export default function StatusPage() {
  return (
    <SitePage
      title="System status"
      description="Current operating status for Payflow APIs, dashboards, and payment flows."
    >
      <ContentBlock title="Current status">
        <p className="font-bold text-emerald-700">All systems operational</p>
        <p>
          API authentication, wallet transfers, payment links, webhook delivery, and admin dashboards are available.
        </p>
      </ContentBlock>

      <ContentBlock title="Components">
        <ul className="space-y-3">
          {[
            ["API and authentication", "Operational"],
            ["Wallet transfers", "Operational"],
            ["Payment links and checkout", "Operational"],
            ["Webhook delivery", "Operational"],
            ["Developer and admin web", "Operational"],
          ].map(([name, state]) => (
            <li key={name} className="flex items-center justify-between gap-4 border-b border-slate-100 pb-3 last:border-0 last:pb-0">
              <span>{name}</span>
              <span className="text-sm font-bold text-emerald-700">{state}</span>
            </li>
          ))}
        </ul>
      </ContentBlock>

      <ContentBlock title="Incidents">
        <p>No active incidents. Past maintenance notes appear in Release notes when relevant.</p>
      </ContentBlock>
    </SitePage>
  );
}
