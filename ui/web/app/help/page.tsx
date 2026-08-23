import { ContentBlock, SitePage } from "../site-page";

export default function HelpCenterPage() {
  return (
    <SitePage
      title="Help center"
      description="Quick answers for developers, admins, and teams evaluating Payflow."
    >
      <ContentBlock title="Developer web access">
        <p>
          Create a developer account on the web to manage API keys, review payments, and configure webhooks. Admins sign
          in with an existing operations account.
        </p>
      </ContentBlock>

      <ContentBlock title="Customers and merchants">
        <p>
          Customer wallets and merchant registration live in the Payflow mobile apps. The web merchant registration page
          explains how to run the merchant app locally.
        </p>
      </ContentBlock>

      <ContentBlock title="Useful links">
        <ul className="list-disc space-y-2 pl-5">
          <li>
            <a href="/docs" className="font-bold text-[#123c91]">
              Developer docs
            </a>
          </li>
          <li>
            <a href="/developers/integration" className="font-bold text-[#123c91]">
              Integration guide
            </a>
          </li>
          <li>
            <a href="/operations/reconciliation" className="font-bold text-[#123c91]">
              Reconciliation overview
            </a>
          </li>
          <li>
            <a href="/contact" className="font-bold text-[#123c91]">
              Contact sales
            </a>
          </li>
        </ul>
      </ContentBlock>
    </SitePage>
  );
}
