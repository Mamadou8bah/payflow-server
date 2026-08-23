import { ContentBlock, SitePage } from "../site-page";

export default function ContactPage() {
  return (
    <SitePage
      title="Contact sales"
      description="Talk to Payflow about developer access, merchant onboarding, or operational rollout."
    >
      <ContentBlock title="Who should contact us">
        <ul className="list-disc space-y-2 pl-5">
          <li>Developers integrating payment links, wallets, or webhooks</li>
          <li>Operators evaluating admin, risk, and reconciliation workflows</li>
          <li>Businesses planning merchant collections in The Gambia</li>
        </ul>
      </ContentBlock>

      <ContentBlock title="How to reach us">
        <p>
          Email{" "}
          <a href="mailto:sales@payflow.gm" className="font-bold text-[#123c91]">
            sales@payflow.gm
          </a>{" "}
          with your company name, use case, and whether you need developer, admin, or merchant support.
        </p>
        <p>
          For technical documentation, start with the{" "}
          <a href="/docs" className="font-bold text-[#123c91]">
            developer docs
          </a>
          .
        </p>
      </ContentBlock>

      <ContentBlock title="Quick links">
        <ul className="list-disc space-y-2 pl-5">
          <li>
            <a href="/signup" className="font-bold text-[#123c91]">
              Create a developer account
            </a>
          </li>
          <li>
            <a href="/merchants/register" className="font-bold text-[#123c91]">
              Merchant app registration info
            </a>
          </li>
          <li>
            <a href="/help" className="font-bold text-[#123c91]">
              Help center
            </a>
          </li>
        </ul>
      </ContentBlock>
    </SitePage>
  );
}
