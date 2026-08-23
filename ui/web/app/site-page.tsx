import { LandingHeader } from "./landing-header";
import { footerColumns, headerNavGroups } from "./site-nav";

export function SitePage({
  title,
  description,
  children,
}: {
  title: string;
  description?: string;
  children: React.ReactNode;
}) {
  return (
    <>
      <LandingHeader groups={headerNavGroups} />
      <main className="min-h-screen overflow-x-clip bg-slate-100 text-slate-900">
        <section className="border-b border-slate-200 bg-white">
          <div className="mx-auto w-[min(880px,calc(100%-1.5rem))] py-10 sm:py-14">
            <p className="text-xs font-bold uppercase tracking-[0.18em] text-orange-600">Payflow</p>
            <h1 className="mt-3 text-3xl font-black tracking-tight text-slate-950 sm:text-4xl">{title}</h1>
            {description ? <p className="mt-4 max-w-2xl text-base leading-relaxed text-slate-600 sm:text-lg">{description}</p> : null}
          </div>
        </section>

        <section className="mx-auto w-[min(880px,calc(100%-1.5rem))] py-8 sm:py-12">
          <div className="space-y-8 text-base leading-relaxed text-slate-700">{children}</div>
        </section>

        <footer className="border-t border-slate-200 bg-white">
          <div className="mx-auto grid w-[min(1280px,calc(100%-1.5rem))] gap-8 py-8 sm:grid-cols-2 lg:grid-cols-4">
            {footerColumns.map((column) => (
              <div key={column.title}>
                <p className="text-xs font-bold uppercase tracking-[0.16em] text-orange-600">{column.title}</p>
                <ul className="mt-3 space-y-2 text-sm text-slate-700">
                  {column.links.map((link) => (
                    <li key={link.href + link.label}>
                      <a href={link.href} className="transition-colors hover:text-orange-600">
                        {link.label}
                      </a>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
          <div className="border-t border-slate-100 py-4 text-center text-sm text-slate-500">
            © 2026 Payflow. All rights reserved.
          </div>
        </footer>
      </main>
    </>
  );
}

export function ContentBlock({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <section className="rounded-2xl border border-slate-200 bg-white p-5 sm:p-6">
      <h2 className="text-lg font-black text-slate-950">{title}</h2>
      <div className="mt-3 space-y-3 text-slate-700">{children}</div>
    </section>
  );
}
