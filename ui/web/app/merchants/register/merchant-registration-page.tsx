import Link from "next/link";
import { AuthShell } from "../../auth-shell";

export default function MerchantRegisterPage() {
  return (
    <AuthShell heading="Merchant registration">
      <div className="mt-6 space-y-5 text-center">
        <p className="text-sm leading-relaxed text-slate-600">
          Merchant registration is handled in the <strong>Payflow Merchant</strong> mobile app — designed for Gambian businesses with phone verification, business details, and owner ID checks.
        </p>
        <div className="rounded-2xl border border-blue-200 bg-blue-50 px-5 py-5 text-left text-sm text-slate-700">
          <p className="font-bold text-slate-900">Run the merchant app locally</p>
          <pre className="mt-3 overflow-x-auto rounded-xl bg-slate-950 p-4 font-mono text-xs text-emerald-100">{`cd ui/merchant-app
npm install
npm start`}</pre>
        </div>
        <p className="text-xs text-slate-500">
          Web registration is for developers. Admins sign in with an existing account.
        </p>
        <Link
          href="/signup"
          className="inline-flex h-14 w-full items-center justify-center rounded-full bg-[#123c91] text-base font-black text-white transition-colors hover:bg-[#0d2f76]"
        >
          Create a developer account
        </Link>
        <p className="text-sm text-slate-600">
          Already registered?{" "}
          <Link href="/login" className="font-black text-[#123c91]">
            Sign in
          </Link>
        </p>
      </div>
    </AuthShell>
  );
}
