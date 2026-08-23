import { ContentBlock, SitePage } from "../../site-page";

export default function TermsOfServicePage() {
  return (
    <SitePage
      title="Terms of service"
      description="The terms that govern use of Payflow’s websites, developer tools, APIs, and related services."
    >
      <ContentBlock title="Agreement">
        <p>
          By creating a Payflow account or using our APIs, dashboards, or documentation, you agree to these terms. If you
          use Payflow on behalf of a business, you confirm you are authorized to bind that business.
        </p>
      </ContentBlock>

      <ContentBlock title="Accounts and roles">
        <p>
          The Payflow web platform is intended for developers and administrators. Customer and merchant registration is
          handled through Payflow mobile apps. You are responsible for protecting credentials, API keys, and access to
          your organization.
        </p>
      </ContentBlock>

      <ContentBlock title="Acceptable use">
        <ul className="list-disc space-y-2 pl-5">
          <li>Do not attempt unauthorized access to wallets, APIs, or admin tools</li>
          <li>Do not use Payflow for unlawful, deceptive, or abusive activity</li>
          <li>Do not interfere with reconciliation, risk controls, or audit logging</li>
          <li>Keep integration traffic within rate limits and published API rules</li>
        </ul>
      </ContentBlock>

      <ContentBlock title="Payments and ledger records">
        <p>
          Payflow records money movement through wallet balances and ledger postings. Transaction outcomes depend on
          validation, risk checks, available balance, and operational status. Reversals and adjustments may be applied
          when required for corrections or dispute handling.
        </p>
      </ContentBlock>

      <ContentBlock title="Service changes">
        <p>
          We may update features, documentation, and these terms as the platform evolves. Continued use after an update
          means you accept the revised terms.
        </p>
      </ContentBlock>

      <ContentBlock title="Contact">
        <p>
          Questions about these terms can be sent through our{" "}
          <a href="/contact" className="font-bold text-[#123c91]">
            contact page
          </a>
          .
        </p>
      </ContentBlock>

      <p className="text-sm text-slate-500">Last updated: August 23, 2026</p>
    </SitePage>
  );
}
