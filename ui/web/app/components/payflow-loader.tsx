type PayflowLoaderProps = {
  message?: string;
  submessage?: string;
};

export function PayflowLoader({
  message = "Loading",
  submessage,
}: PayflowLoaderProps) {
  return (
    <main className="flex min-h-screen flex-col items-center justify-center bg-[#eaf0ff] px-4">
      <div className="flex w-full max-w-xs flex-col items-center gap-6">
        <img
          src="https://res.cloudinary.com/dflsnes44/image/upload/v1780228196/payflow_no_bg_f0l7on.png"
          alt="Payflow"
          className="h-11 w-11 object-contain"
        />

        <div className="relative h-14 w-14" aria-hidden="true">
          <span className="absolute inset-0 rounded-full border-[3px] border-slate-200" />
          <span className="absolute inset-0 animate-spin rounded-full border-[3px] border-transparent border-t-[#123c91] border-r-[#123c91]/30" />
        </div>

        <div className="w-full text-center">
          <p className="text-sm font-bold text-slate-800">{message}</p>
          {submessage ? <p className="mt-1.5 text-xs font-medium text-slate-500">{submessage}</p> : null}
          <div className="mt-4 flex justify-center gap-1.5" aria-hidden="true">
            <span className="payflow-loader-dot h-1.5 w-1.5 rounded-full bg-[#123c91]" />
            <span className="payflow-loader-dot h-1.5 w-1.5 rounded-full bg-[#123c91] [animation-delay:150ms]" />
            <span className="payflow-loader-dot h-1.5 w-1.5 rounded-full bg-[#123c91] [animation-delay:300ms]" />
          </div>
        </div>
      </div>
    </main>
  );
}

export function DashboardLoadingSkeleton() {
  return (
    <main className="min-h-screen bg-[#eaf0ff] text-slate-900">
      <header className="bg-white">
        <div className="mx-auto flex w-[min(1440px,calc(100%-1.5rem))] items-center gap-4 px-3 py-3 lg:px-5">
          <div className="h-9 w-9 shrink-0 animate-pulse rounded-xl bg-slate-200" />
          <div className="mx-auto flex gap-2">
            {Array.from({ length: 5 }).map((_, i) => (
              <div key={i} className="h-9 w-20 animate-pulse rounded-full bg-slate-200" />
            ))}
          </div>
          <div className="h-9 w-24 shrink-0 animate-pulse rounded-full bg-slate-200" />
        </div>
      </header>

      <div className="mx-auto w-[min(1440px,calc(100%-1.5rem))] space-y-5 px-3 py-6 lg:px-5">
        <div className="h-10 w-full max-w-md animate-pulse rounded-2xl bg-white" />
        <div className="grid gap-5 lg:grid-cols-2">
          <div className="h-48 animate-pulse rounded-[1.75rem] bg-white" />
          <div className="h-48 animate-pulse rounded-[1.75rem] bg-white" />
        </div>
        <div className="h-64 animate-pulse rounded-[1.75rem] bg-white" />
      </div>

      <div className="pointer-events-none fixed inset-0 flex items-center justify-center bg-[#eaf0ff]/40">
        <div className="flex flex-col items-center gap-4 rounded-[1.75rem] bg-white px-8 py-6">
          <div className="relative h-10 w-10" aria-hidden="true">
            <span className="absolute inset-0 rounded-full border-2 border-slate-200" />
            <span className="absolute inset-0 animate-spin rounded-full border-2 border-transparent border-t-[#123c91]" />
          </div>
          <p className="text-sm font-bold text-slate-800">Preparing your dashboard</p>
        </div>
      </div>
    </main>
  );
}
