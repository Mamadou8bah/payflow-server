import { ContentBlock, SitePage } from "../../site-page";

export default function CookiePolicyPage() {
  return (
    <SitePage
      title="Cookie policy"
      description="How Payflow uses cookies and similar technologies on the web."
    >
      <ContentBlock title="What we use">
        <p>
          Payflow uses essential cookies and local storage to keep sessions signed in, remember preferences, and protect
          forms. We may use limited analytics to understand how documentation and marketing pages are used.
        </p>
      </ContentBlock>

      <ContentBlock title="Essential storage">
        <ul className="list-disc space-y-2 pl-5">
          <li>Authentication and session continuity</li>
          <li>CSRF and security controls</li>
          <li>Basic UI preferences such as dismissed notices</li>
        </ul>
      </ContentBlock>

      <ContentBlock title="Managing preferences">
        <p>
          You can clear cookies and site data in your browser settings. Disabling essential storage may prevent login or
          dashboard access from working correctly.
        </p>
      </ContentBlock>

      <p className="text-sm text-slate-500">Last updated: August 23, 2026</p>
    </SitePage>
  );
}
