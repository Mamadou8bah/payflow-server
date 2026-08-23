"use client";

import { useEffect, useState } from "react";
import { createPortal } from "react-dom";

export type LandingNavGroup = {
  label: string;
  links: Array<{ label: string; href: string }>;
};

export function LandingHeader({ groups }: { groups: LandingNavGroup[] }) {
  const [mobileOpen, setMobileOpen] = useState(false);
  const [openDesktopGroup, setOpenDesktopGroup] = useState<string | null>(null);
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  useEffect(() => {
    if (!mobileOpen) return;
    const previous = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    return () => {
      document.body.style.overflow = previous;
    };
  }, [mobileOpen]);

  useEffect(() => {
    function onResize() {
      if (window.innerWidth >= 768) setMobileOpen(false);
    }
    window.addEventListener("resize", onResize);
    return () => window.removeEventListener("resize", onResize);
  }, []);

  useEffect(() => {
    function onKey(e: KeyboardEvent) {
      if (e.key === "Escape") setMobileOpen(false);
    }
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, []);

  const mobileMenu =
    mounted &&
    createPortal(
      <div className={`fixed inset-0 z-[100000] md:hidden ${mobileOpen ? "" : "pointer-events-none"}`} aria-hidden={!mobileOpen}>
        <button
          type="button"
          className={`absolute inset-0 bg-slate-900/50 transition-opacity duration-200 ${mobileOpen ? "opacity-100" : "opacity-0"}`}
          aria-label="Close menu"
          onClick={() => setMobileOpen(false)}
        />
        <aside
          className={`absolute inset-y-0 right-0 flex w-full max-w-sm flex-col border-l border-slate-200 bg-white transition-transform duration-300 ease-out ${
            mobileOpen ? "translate-x-0" : "translate-x-full"
          }`}
        >
          <div className="flex h-14 shrink-0 items-center justify-between border-b border-slate-200 px-4">
            <p className="text-sm font-black text-[#123c91]">Menu</p>
            <button
              type="button"
              className="inline-flex h-10 w-10 items-center justify-center rounded-lg text-slate-700 hover:bg-slate-100"
              aria-label="Close menu"
              onClick={() => setMobileOpen(false)}
            >
              <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
                <path d="M6 6l12 12M18 6L6 18" />
              </svg>
            </button>
          </div>

          <nav className="min-h-0 flex-1 overflow-y-auto px-2 py-3">
            {groups.map((group) => (
              <div key={group.label} className="mb-4">
                <p className="px-3 py-2 text-xs font-bold uppercase tracking-[0.14em] text-slate-500">{group.label}</p>
                <ul className="space-y-0.5">
                  {group.links.map((link) => (
                    <li key={`${group.label}-${link.label}`}>
                      <a
                        href={link.href}
                        className="block rounded-lg px-3 py-3 text-base font-semibold text-slate-900 hover:bg-slate-100"
                        onClick={() => setMobileOpen(false)}
                      >
                        {link.label}
                      </a>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </nav>

          <div
            className="shrink-0 space-y-2 border-t border-slate-200 p-4"
            style={{ paddingBottom: "max(1rem, env(safe-area-inset-bottom))" }}
          >
            <a
              href="/signup"
              className="flex h-12 items-center justify-center rounded-full bg-orange-600 text-sm font-bold text-slate-900"
              onClick={() => setMobileOpen(false)}
            >
              Get started
            </a>
            <a
              href="/login"
              className="flex h-12 items-center justify-center rounded-full border border-slate-200 bg-white text-sm font-bold text-slate-900"
              onClick={() => setMobileOpen(false)}
            >
              Log in
            </a>
          </div>
        </aside>
      </div>,
      document.body,
    );

  return (
    <header className="sticky top-0 z-[9999] border-b border-slate-200 bg-white">
      <div className="mx-auto flex h-14 w-[min(1280px,calc(100%-1.5rem))] items-center gap-3 sm:h-16">
        <a href="/" className="flex shrink-0 items-center gap-2.5">
          <img
            src="https://res.cloudinary.com/dflsnes44/image/upload/v1780228196/payflow_no_bg_f0l7on.png"
            alt="Payflow"
            className="h-8 w-8 object-contain sm:h-9 sm:w-9"
          />
          <span className="text-base font-black tracking-tight text-[#123c91] sm:text-lg">Payflow</span>
        </a>

        <nav className="mx-auto hidden items-center justify-center gap-0.5 text-sm font-semibold text-slate-700 md:flex lg:gap-1">
          {groups.map((group) => {
            const open = openDesktopGroup === group.label;
            return (
              <div
                key={group.label}
                className="relative"
                onMouseEnter={() => setOpenDesktopGroup(group.label)}
                onMouseLeave={() => setOpenDesktopGroup(null)}
              >
                <button
                  className="flex items-center gap-1 rounded-full px-3 py-2 transition-colors hover:bg-slate-100 hover:text-slate-950 lg:px-3.5"
                  type="button"
                  aria-expanded={open}
                  onClick={() => setOpenDesktopGroup(open ? null : group.label)}
                >
                  {group.label}
                  <svg
                    viewBox="0 0 20 20"
                    className={`h-3.5 w-3.5 text-slate-400 transition-transform ${open ? "rotate-180" : ""}`}
                    fill="currentColor"
                    aria-hidden="true"
                  >
                    <path d="M5.23 7.21a.75.75 0 0 1 1.06.02L10 11.17l3.71-3.94a.75.75 0 1 1 1.08 1.04l-4.25 4.5a.75.75 0 0 1-1.08 0l-4.25-4.5a.75.75 0 0 1 .02-1.06Z" />
                  </svg>
                </button>
                <div
                  className={`absolute left-1/2 top-full z-[10000] mt-2 w-56 -translate-x-1/2 rounded-xl border border-slate-200 bg-white p-1.5 transition-all ${
                    open ? "visible translate-y-0 opacity-100" : "invisible -translate-y-1 opacity-0"
                  }`}
                >
                  <div className="absolute -top-3 left-0 h-3 w-full" />
                  {group.links.map((link) => (
                    <a
                      key={link.label}
                      href={link.href}
                      className="block rounded-lg px-3.5 py-2.5 text-sm font-semibold text-slate-700 transition-colors hover:bg-orange-50 hover:text-orange-700"
                      onClick={() => setOpenDesktopGroup(null)}
                    >
                      {link.label}
                    </a>
                  ))}
                </div>
              </div>
            );
          })}
        </nav>

        <div className="ml-auto flex items-center gap-2">
          <a
            className="hidden h-10 items-center rounded-full border border-slate-200 px-4 text-sm font-bold text-slate-800 transition-colors hover:bg-slate-50 sm:inline-flex"
            href="/login"
          >
            Log in
          </a>
          <a
            className="hidden h-10 items-center rounded-full bg-orange-600 px-4 text-sm font-bold text-slate-900 transition-colors hover:bg-orange-500 sm:inline-flex"
            href="/signup"
          >
            Get started
          </a>
          <button
            type="button"
            className="inline-flex h-10 w-10 items-center justify-center rounded-lg text-slate-800 transition-colors hover:bg-slate-100 md:hidden"
            aria-expanded={mobileOpen}
            aria-label={mobileOpen ? "Close menu" : "Open menu"}
            onClick={() => setMobileOpen((v) => !v)}
          >
            <span className="sr-only">Menu</span>
            <span className="relative block h-3.5 w-5">
              <span
                className={`absolute left-0 block h-0.5 w-5 rounded-full bg-slate-900 transition-all duration-200 ${
                  mobileOpen ? "top-1.5 rotate-45" : "top-0"
                }`}
              />
              <span
                className={`absolute left-0 top-1.5 block h-0.5 w-5 rounded-full bg-slate-900 transition-all duration-200 ${
                  mobileOpen ? "opacity-0" : "opacity-100"
                }`}
              />
              <span
                className={`absolute left-0 block h-0.5 w-5 rounded-full bg-slate-900 transition-all duration-200 ${
                  mobileOpen ? "top-1.5 -rotate-45" : "top-3"
                }`}
              />
            </span>
          </button>
        </div>
      </div>
      {mobileMenu}
    </header>
  );
}
