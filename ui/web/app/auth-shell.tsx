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
    <main className="min-h-screen bg-[#eaf0ff] px-4 py-6 text-slate-900 sm:px-6 lg:px-10">
      <div className="mx-auto flex min-h-[calc(100vh-3rem)] w-full max-w-[100rem] items-center justify-center">
        <div className="grid w-full max-w-[76rem] overflow-hidden rounded-[2.5rem] border border-slate-300 bg-white shadow-[0_24px_80px_rgba(15,23,42,0.10)] lg:grid-cols-[0.98fr_1fr]">
          <section className="flex flex-col bg-white px-6 py-6 sm:px-10 lg:px-12">
            <a href="/" className="mx-auto flex items-center gap-3">
              <img
                src="https://res.cloudinary.com/dflsnes44/image/upload/v1780228196/payflow_no_bg_f0l7on.png"
                alt="Payflow"
                className="h-12 w-12 object-contain"
              />
              <span className="text-3xl font-black tracking-[-0.04em] text-[#123c91]">Payflow</span>
            </a>

            <div className="mx-auto mt-8 w-full max-w-[29rem]">
              <h1 className="text-center text-2xl font-black text-slate-800">{heading}</h1>
              {children}
            </div>

            {footerText && footerLinkHref && footerLinkLabel ? (
              <p className="mt-auto pt-12 text-center text-base text-slate-700">
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
        className="h-12 w-full rounded-full border border-slate-300 bg-white px-5 pr-12 text-sm font-semibold text-slate-900 outline-none placeholder:text-slate-500 focus:border-[#123c91] focus:ring-4 focus:ring-blue-100"
      />
      <span className="absolute right-5 top-1/2 -translate-y-1/2 text-slate-500">{icon}</span>
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
      className="flex h-11 items-center justify-center gap-3 rounded-full border border-slate-200 bg-white px-5 text-sm font-bold text-slate-700 shadow-sm transition-colors hover:bg-slate-50"
    >
      {children}
      {label}
    </button>
  );
}
