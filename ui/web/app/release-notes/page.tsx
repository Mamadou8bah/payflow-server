import { ContentBlock, SitePage } from "../site-page";

export default function ReleaseNotesPage() {
  return (
    <SitePage
      title="Release notes"
      description="Product and platform updates for the Payflow web experience and APIs."
    >
      <ContentBlock title="August 2026">
        <ul className="list-disc space-y-2 pl-5">
          <li>Web signup limited to developer accounts; customer and merchant onboarding remain in mobile apps</li>
          <li>Landing and dashboard mobile navigation improved for smaller screens</li>
          <li>Brand accent updated across marketing surfaces</li>
          <li>Legal, company, help, and status pages published on the web</li>
        </ul>
      </ContentBlock>

      <ContentBlock title="Earlier platform work">
        <ul className="list-disc space-y-2 pl-5">
          <li>Developer dashboard for API keys, payments, and webhooks</li>
          <li>Admin tools for wallets, reversals, risk, and reconciliation</li>
          <li>Docs and Swagger entry points for API integration</li>
        </ul>
      </ContentBlock>
    </SitePage>
  );
}
