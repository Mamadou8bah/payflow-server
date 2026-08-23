"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { BASE_URL, navGroups } from "./docs-data";

export function DocsShell({ children }: { children: React.ReactNode }) {
  const [activeId, setActiveId] = useState("introduction");
  const [mobileNavOpen, setMobileNavOpen] = useState(false);

  useEffect(() => {
    const ids = navGroups.flatMap((g) => g.items.map((i) => i.id));
    const elements = ids
      .map((id) => document.getElementById(id))
      .filter((el): el is HTMLElement => el !== null);

    const observer = new IntersectionObserver(
      (entries) => {
        const visible = entries
          .filter((e) => e.isIntersecting)
          .sort((a, b) => b.intersectionRatio - a.intersectionRatio);
        if (visible[0]?.target?.id) {
          setActiveId(visible[0].target.id);
        }
      },
      { rootMargin: "-20% 0px -65% 0px", threshold: [0, 0.25, 0.5] },
    );

    elements.forEach((el) => observer.observe(el));
    return () => observer.disconnect();
  }, []);

  return (
    <div className="min-h-screen bg-slate-100 text-slate-900">
      <header className="sticky top-0 z-50 border-b border-slate-200 bg-white shadow-sm">
        <div className="mx-auto flex h-16 w-full max-w-[1440px] items-center gap-4 px-4 lg:px-8">
          <Link href="/" className="flex shrink-0 items-center gap-2.5">
            <img
              src="https://res.cloudinary.com/dflsnes44/image/upload/v1780228196/payflow_no_bg_f0l7on.png"
              alt="Payflow"
              className="h-8 w-8 object-contain"
            />
            <div className="leading-tight">
              <p className="text-[10px] font-bold uppercase tracking-[0.22em] text-slate-500">Payflow</p>
              <p className="text-sm font-black text-[#123c91]">API Docs</p>
            </div>
          </Link>

          <div className="hidden flex-1 justify-center md:flex">
            <label className="w-full max-w-md">
              <span className="sr-only">Search documentation</span>
              <input
                type="search"
                placeholder="Search endpoints, guides…"
                className="h-10 w-full rounded-lg border border-slate-200 bg-slate-50 px-4 text-sm outline-none transition-colors placeholder:text-slate-400 focus:border-[#123c91] focus:bg-white focus:ring-2 focus:ring-blue-100"
              />
            </label>
          </div>

          <div className="ml-auto flex items-center gap-2">
            <span className="hidden rounded-md bg-slate-100 px-2.5 py-1 font-mono text-xs font-semibold text-slate-600 sm:inline">
              v1.0
            </span>
            <a
              href={`${BASE_URL}/swagger-ui.html`}
              target="_blank"
              rel="noreferrer"
              className="hidden rounded-lg bg-[#123c91] px-4 py-2 text-sm font-bold text-white transition-colors hover:bg-[#0d2f76] sm:inline-flex"
            >
              Open Swagger
            </a>
            <button
              type="button"
              onClick={() => setMobileNavOpen((v) => !v)}
              className="rounded-lg border border-slate-200 px-3 py-2 text-sm font-bold text-slate-700 lg:hidden"
            >
              Menu
            </button>
          </div>
        </div>
      </header>

      <div className="mx-auto flex w-full max-w-[1440px]">
        <aside
          className={`${
            mobileNavOpen ? "block" : "hidden"
          } w-full shrink-0 border-b border-slate-200 bg-white px-4 py-6 lg:block lg:w-64 lg:border-b-0 lg:border-r lg:px-0 lg:py-8 xl:w-72`}
        >
          <nav className="lg:sticky lg:top-20 lg:max-h-[calc(100vh-6rem)] lg:overflow-y-auto lg:px-5">
            {navGroups.map((group) => (
              <div key={group.title} className="mb-6">
                <p className="mb-2 px-2 text-[11px] font-bold uppercase tracking-[0.18em] text-slate-500">
                  {group.title}
                </p>
                <ul className="space-y-0.5">
                  {group.items.map((item) => {
                    const active = activeId === item.id;
                    return (
                      <li key={item.id}>
                        <a
                          href={`#${item.id}`}
                          onClick={() => setMobileNavOpen(false)}
                          className={`block rounded-lg px-3 py-2 text-sm font-semibold transition-colors ${
                            active
                              ? "bg-[#123c91] text-white shadow-sm"
                              : "text-slate-600 hover:bg-slate-100 hover:text-slate-950"
                          }`}
                        >
                          {item.label}
                        </a>
                      </li>
                    );
                  })}
                </ul>
              </div>
            ))}

            <div className="mt-4 rounded-xl bg-slate-950 p-4 text-white">
              <p className="text-[10px] font-bold uppercase tracking-[0.18em] text-blue-300">Base URL</p>
              <p className="mt-2 break-all font-mono text-xs text-slate-300">{BASE_URL}</p>
            </div>
          </nav>
        </aside>

        <div className="min-w-0 flex-1 px-4 py-8 lg:px-10 lg:py-10 xl:pr-12">{children}</div>
      </div>
    </div>
  );
}
