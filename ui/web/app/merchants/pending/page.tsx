"use client";

import Link from "next/link";
import { AuthShell } from "../../auth-shell";
import { getMerchantSession } from "../../../lib/merchant-api";

export default function MerchantPendingPage() {
  const session = getMerchantSession();

  return (
    <AuthShell heading="Application under review">
      <div className="mt-6 space-y-5">
        <div className="rounded-2xl bg-amber-50 px-4 py-4 text-sm text-amber-950">
          <p className="font-bold">Status: Pending review</p>
          <p className="mt-2 leading-relaxed">
            {session?.businessName
              ? `${session.businessName} is in our verification queue.`
              : "Your merchant application is in our verification queue."}
          </p>
        </div>

        <ul className="space-y-3 text-sm text-slate-600">
          <li className="flex gap-2"><span className="text-emerald-600">✓</span> Phone number verified</li>
          <li className="flex gap-2"><span className="text-emerald-600">✓</span> Business and owner details received</li>
          <li className="flex gap-2"><span className="text-amber-600">…</span> Compliance review (typically 1–2 business days)</li>
          <li className="flex gap-2"><span className="text-slate-400">○</span> GMD collection wallet activated after approval</li>
        </ul>

        <p className="text-xs leading-relaxed text-slate-500">
          We may call the registered mobile number or visit the business address in Greater Banjul or the regions. Keep your Gambian ID available.
        </p>

        <Link href="/dashboard" className="inline-flex h-12 w-full items-center justify-center rounded-full bg-[#123c91] text-sm font-black text-white">
          Go to dashboard
        </Link>
        <Link href="/login" className="block text-center text-sm font-semibold text-[#123c91]">
          Sign in with another account
        </Link>
      </div>
    </AuthShell>
  );
}
