type AuthShellProps = {
  heading: string;
  children: React.ReactNode;
  footerText?: string;
  footerLinkLabel?: string;
  footerLinkHref?: string;
};

export function AuthShell({
  heading,
  children,
  footerText,
  footerLinkLabel,
  footerLinkHref,
}: AuthShellProps) {
  return (
    <main className="min-h-screen overflow-x-clip bg-[#eaf0ff] text-slate-900">
      <div className="mx-auto flex min-h-screen w-full max-w-[100rem] items-stretch justify-center lg:items-center lg:px-10 lg:py-8">
        <div className="grid w-full max-w-[76rem] bg-white lg:overflow-hidden lg:rounded-[2rem] lg:border lg:border-slate-200 lg:grid-cols-[0.98fr_1fr]">
          <section className="flex w-full flex-col px-4 pb-8 pt-5 sm:px-8 sm:pb-10 sm:pt-8 lg:px-12 lg:py-10">
            <a href="/" className="flex shrink-0 items-center gap-2.5 sm:mx-auto sm:gap-3">
              <img
                src="https://res.cloudinary.com/dflsnes44/image/upload/v1780228196/payflow_no_bg_f0l7on.png"
                alt="Payflow"
                className="h-9 w-9 object-contain sm:h-12 sm:w-12"
              />
              <span className="text-xl font-black tracking-[-0.04em] text-[#123c91] sm:text-3xl">Payflow</span>
            </a>

            <div className="mx-auto mt-6 w-full max-w-[29rem] sm:mt-8">
              <h1 className="text-left text-2xl font-black text-slate-900 sm:text-center sm:text-2xl">{heading}</h1>
              {children}
            </div>

            {footerText && footerLinkHref && footerLinkLabel ? (
              <p className="mx-auto mt-8 w-full max-w-[29rem] text-center text-sm text-slate-700 sm:mt-auto sm:pt-10 sm:text-base">
                {footerText}{" "}
                <a href={footerLinkHref} className="font-black text-[#123c91]">
                  {footerLinkLabel}
                </a>
              </p>
            ) : null}
          </section>

          <section className="hidden overflow-hidden bg-slate-200 lg:block">
            <img
              src="https://res.cloudinary.com/dflsnes44/image/upload/v1780245250/lp_pwrrme.jpg"
              alt="Payflow visual"
              className="h-full w-full object-cover"
            />
          </section>
        </div>
      </div>
    </main>
  );
}

export function AuthField({
  type,
  placeholder,
  icon,
  value,
  onChange,
  name,
}: {
  type: string;
  placeholder: string;
  icon: React.ReactNode;
  value?: string;
  onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void;
  name?: string;
}) {
  return (
    <label className="relative block">
      <input
        type={type}
        name={name}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        className="h-12 w-full rounded-2xl border border-slate-300 bg-white px-4 pr-12 text-base font-semibold text-slate-900 outline-none placeholder:text-slate-500 focus:border-[#123c91] focus:ring-4 focus:ring-blue-100 sm:h-12 sm:rounded-full sm:px-5 sm:text-sm"
      />
      <span className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-500 sm:right-5">{icon}</span>
    </label>
  );
}

export function DividerText({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex items-center gap-3 text-sm font-semibold text-slate-500">
      <span className="h-px flex-1 bg-slate-200" />
      {children}
      <span className="h-px flex-1 bg-slate-200" />
    </div>
  );
}

export function SocialButton({
  label,
  children,
}: {
  label: string;
  children: React.ReactNode;
}) {
  return (
    <button
      type="button"
      className="flex h-12 w-full items-center justify-center gap-3 rounded-2xl border border-slate-200 bg-white px-5 text-sm font-bold text-slate-700 transition-colors hover:bg-slate-50 sm:h-11 sm:rounded-full"
    >
      {children}
      {label}
    </button>
  );
}
