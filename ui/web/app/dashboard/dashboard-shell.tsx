"use client";

import { useEffect, useState } from "react";
import { clearAuthSession, getAuthSession } from "../../lib/auth-session";
import { clearDemoSession, getDemoSession } from "../../lib/demo-auth";
import type { DemoRole } from "../../lib/mock/demo-users";
import type { DashboardTab } from "./dashboard-tabs";

const roleLabels: Record<DemoRole, string> = {
  admin: "Admin",
  merchant: "Merchant",
  developer: "Developer",
  customer: "Customer",
};

export function DashboardShell({
  tabs,
  activeTab,
  onTabChange,
  children,
  message,
}: {
  tabs: DashboardTab[];
  activeTab: string;
  onTabChange: (tab: string) => void;
  children: React.ReactNode;
  message?: { type: "success" | "error"; text: string } | null;
}) {
  const [session, setSession] = useState(() => getAuthSession() ?? getDemoSession());

  useEffect(() => {
    setSession(getAuthSession() ?? getDemoSession());
  }, []);

  function handleLogout() {
    clearAuthSession();
    clearDemoSession();
    window.location.href = "/login";
  }

  const isLive = session && "source" in session && session.source === "api";

  const initials = session?.name
    ?.split(" ")
    .map((part) => part[0])
    .join("")
    .slice(0, 2)
    .toUpperCase();

  return (
    <main className="min-h-screen bg-[#eaf0ff] text-slate-900">
      <header className="sticky top-0 z-40 bg-white shadow-md">
        <div className="mx-auto flex w-[min(1440px,calc(100%-1.5rem))] items-center gap-4 px-3 py-3 lg:px-5">
          <a href="/" className="flex shrink-0 items-center gap-2.5">
            <img
              src="https://res.cloudinary.com/dflsnes44/image/upload/v1780228196/payflow_no_bg_f0l7on.png"
              alt="Payflow"
              className="h-9 w-9 object-contain"
            />
            <span className="hidden text-lg font-black tracking-tight text-[#123c91] sm:inline">Payflow</span>
          </a>

          <nav className="mx-auto flex max-w-full items-center gap-1 overflow-x-auto rounded-full bg-slate-200 p-1">
            {tabs.map((item) => {
              const active = activeTab === item.id;
              return (
                <button
                  key={item.id}
                  type="button"
                  onClick={() => onTabChange(item.id)}
                  className={`flex shrink-0 items-center gap-2 rounded-full px-3 py-2 text-sm font-bold transition-all ${
                    active ? "bg-[#123c91] text-white shadow-md" : "text-slate-700 hover:bg-white"
                  }`}
                >
                  {item.icon}
                  <span className="hidden md:inline">{item.label}</span>
                </button>
              );
            })}
          </nav>

          <div className="flex shrink-0 items-center gap-2">
            {session ? (
              <span className="hidden rounded-full bg-blue-200 px-3 py-1.5 text-xs font-bold capitalize text-[#123c91] lg:inline">
                {roleLabels[session.role]}
              </span>
            ) : null}
            <span className={`hidden rounded-full px-3 py-1.5 text-xs font-bold lg:inline ${isLive ? "bg-emerald-200 text-emerald-950" : "bg-orange-200 text-orange-900"}`}>
              {isLive ? "Live" : "Demo"}
            </span>
            <div className="grid h-10 w-10 place-items-center rounded-full bg-[#123c91] text-xs font-black text-white" title={session?.name}>
              {initials ?? "PF"}
            </div>
            <button
              type="button"
              onClick={handleLogout}
              className="hidden rounded-full bg-slate-200 px-3 py-2 text-xs font-bold text-slate-800 hover:bg-slate-300 sm:inline"
            >
              Log out
            </button>
          </div>
        </div>
      </header>

      <div className="mx-auto w-[min(1440px,calc(100%-1.5rem))] px-3 py-5 lg:px-5 lg:py-6">
        {message ? (
          <div
            className={`mb-5 rounded-2xl px-4 py-3 text-sm font-semibold ${
              message.type === "success" ? "bg-emerald-200 text-emerald-950" : "bg-rose-200 text-rose-950"
            }`}
          >
            {message.text}
          </div>
        ) : null}
        {children}
      </div>
    </main>
  );
}

export function PanelCard({
  title,
  subtitle,
  action,
  children,
  className = "",
  variant = "default",
}: {
  title?: string;
  subtitle?: string;
  action?: React.ReactNode;
  children: React.ReactNode;
  className?: string;
  variant?: "default" | "hero";
}) {
  if (variant === "hero") {
    return (
      <section className={`overflow-hidden rounded-[2rem] bg-gradient-to-br from-[#123c91] via-[#1649a8] to-[#1e5bb8] p-6 text-white shadow-lg ${className}`}>
        {children}
      </section>
    );
  }

  return (
    <section className={`rounded-[2rem] bg-white p-5 shadow-md sm:p-6 ${className}`}>
      {(title || action) && (
        <div className="mb-4 flex items-start justify-between gap-3">
          <div>
            {title ? <h2 className="text-lg font-black text-slate-950">{title}</h2> : null}
            {subtitle ? <p className="mt-1 text-xs font-medium text-slate-500">{subtitle}</p> : null}
          </div>
          {action}
        </div>
      )}
      {children}
    </section>
  );
}

export function PrimaryButton({ children, ...props }: React.ButtonHTMLAttributes<HTMLButtonElement>) {
  return (
    <button
      {...props}
      className={`h-12 w-full rounded-full bg-[#123c91] text-sm font-black text-white shadow-md transition-colors hover:bg-[#0d2f76] disabled:cursor-not-allowed disabled:bg-slate-400 ${props.className ?? ""}`}
    >
      {children}
    </button>
  );
}

export function SecondaryButton({ children, ...props }: React.ButtonHTMLAttributes<HTMLButtonElement>) {
  return (
    <button
      {...props}
      className={`h-11 rounded-full bg-slate-200 px-5 text-sm font-bold text-slate-800 transition-colors hover:bg-slate-300 ${props.className ?? ""}`}
    >
      {children}
    </button>
  );
}

export const inputClass =
  "h-11 w-full rounded-2xl bg-slate-200 px-4 text-sm font-semibold text-slate-900 outline-none transition-colors focus:bg-white focus:ring-4 focus:ring-blue-200";

export const selectClass =
  "h-11 rounded-2xl bg-slate-200 px-4 text-sm font-semibold text-slate-800 outline-none focus:bg-white focus:ring-4 focus:ring-blue-200";

export const tableClass = "min-w-full text-left text-sm";
export const tableHeadClass = "bg-slate-200 text-slate-700";
export const tableRowClass = "odd:bg-white even:bg-slate-100";

export function formatTimestamp(value: string) {
  return new Intl.DateTimeFormat("en-GB", { dateStyle: "medium", timeStyle: "short" }).format(new Date(value));
}
