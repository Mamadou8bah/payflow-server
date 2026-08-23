"use client";

import { useEffect, useState } from "react";
import { clearDemoSession, getDemoSession, type DemoSession } from "../../lib/demo-auth";

export function DashboardShell({
  title,
  subtitle,
  children,
}: {
  title: string;
  subtitle: string;
  children: React.ReactNode;
}) {
  const [session, setSession] = useState<DemoSession | null>(null);

  useEffect(() => {
    setSession(getDemoSession());
  }, []);

  function handleLogout() {
    clearDemoSession();
    window.location.href = "/login";
  }

  return (
    <main className="min-h-screen bg-slate-50 text-slate-900">
      <header className="sticky top-0 z-30 border-b border-slate-200 bg-white/95 backdrop-blur">
        <div className="mx-auto flex w-[min(1440px,calc(100%-2rem))] items-center gap-4 px-4 py-4 lg:px-6">
          <a href="/" className="flex items-center gap-3">
            <img
              src="https://res.cloudinary.com/dflsnes44/image/upload/v1780228196/payflow_no_bg_f0l7on.png"
              alt="Payflow"
              className="h-10 w-10 object-contain"
            />
            <div className="leading-tight">
              <p className="text-sm font-black uppercase tracking-[0.24em] text-slate-500">Payflow</p>
              <p className="text-xl font-black text-slate-950">{title}</p>
            </div>
          </a>

          <div className="ml-auto flex items-center gap-3">
            {session ? (
              <span className="hidden text-sm font-semibold text-slate-600 sm:inline">
                {session.name} · {session.role}
              </span>
            ) : null}
            <button
              type="button"
              onClick={handleLogout}
              className="rounded-full border border-slate-200 px-4 py-2 text-sm font-semibold text-slate-700 transition-colors hover:bg-slate-50"
            >
              Log out
            </button>
          </div>
        </div>
      </header>

      <div className="mx-auto w-[min(1440px,calc(100%-2rem))] px-4 py-6 lg:px-6 lg:py-8">
        <div className="mb-6 rounded-2xl border border-blue-200 bg-blue-50 px-5 py-4 text-sm text-blue-900">
          <p className="font-bold">Demo mode</p>
          <p className="mt-1 text-blue-800">{subtitle}</p>
        </div>
        {children}
      </div>
    </main>
  );
}
