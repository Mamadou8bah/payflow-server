import { ContentBlock, SitePage } from "../../site-page";

export default function ReconciliationPage() {
  return (
    <SitePage
      title="Reconciliation"
      description="How operators review mismatches, reports, and ledger alignment in Payflow."
    >
      <ContentBlock title="What reconciliation covers">
        <p>
          Reconciliation helps admins compare expected payment activity with recorded wallet and ledger outcomes. It
          supports investigation when reports show gaps or unresolved mismatches.
        </p>
      </ContentBlock>

      <ContentBlock title="Admin workflow">
        <ul className="list-disc space-y-2 pl-5">
          <li>Review unmatched or delayed items</li>
          <li>Inspect related transactions and wallet state</li>
          <li>Resolve mismatches with a clear audit trail</li>
          <li>Keep operational reports current for finance and risk teams</li>
        </ul>
      </ContentBlock>

      <ContentBlock title="Get access">
        <p>
          Reconciliation tools are available to admin accounts in the{" "}
          <a href="/dashboard" className="font-bold text-[#123c91]">
            dashboard
          </a>
          . Developers building payment flows should also review the{" "}
          <a href="/company/security" className="font-bold text-[#123c91]">
            security
          </a>{" "}
          and{" "}
          <a href="/company/compliance" className="font-bold text-[#123c91]">
            compliance
          </a>{" "}
          pages.
        </p>
      </ContentBlock>
    </SitePage>
  );
}
