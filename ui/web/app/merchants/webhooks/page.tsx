import { ContentBlock, SitePage } from "../../site-page";

export default function MerchantWebhooksPage() {
  return (
    <SitePage
      title="Webhook updates"
      description="How Payflow notifies your systems when payment and collection statuses change."
    >
      <ContentBlock title="Why webhooks matter">
        <p>
          Polling is fragile for payment status. Webhooks let your backend react when a customer completes checkout, a
          payment fails, or a collection needs review.
        </p>
      </ContentBlock>

      <ContentBlock title="Common events">
        <ul className="list-disc space-y-2 pl-5">
          <li>
            <code className="rounded bg-slate-100 px-1.5 py-0.5 text-sm">payment.paid</code> — checkout completed
          </li>
          <li>
            <code className="rounded bg-slate-100 px-1.5 py-0.5 text-sm">payment.failed</code> — payment did not succeed
          </li>
          <li>Additional lifecycle events as your integration expands</li>
        </ul>
      </ContentBlock>

      <ContentBlock title="Next steps">
        <p>
          Developers configure webhook endpoints from the dashboard after creating an API key. Read the{" "}
          <a href="/developers/integration" className="font-bold text-[#123c91]">
            integration guide
          </a>{" "}
          and{" "}
          <a href="/docs" className="font-bold text-[#123c91]">
            docs
          </a>
          .
        </p>
      </ContentBlock>
    </SitePage>
  );
}
