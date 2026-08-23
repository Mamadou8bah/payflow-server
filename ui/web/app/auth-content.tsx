"use client";

import Link from "next/link";
import { useState } from "react";
import { authApi, type RegisterRole } from "../lib/auth-api";
import { getDashboardPath, mapBackendRole, saveAuthSession } from "../lib/auth-session";
import { authenticateDemoUser, DEMO_AUTH_ENABLED, getDemoDashboardPath, saveDemoSession } from "../lib/demo-auth";
import { DEMO_PASSWORD, demoUsers } from "../lib/mock/demo-users";
import { toE164Phone } from "../lib/merchant-registration";
import { AuthField, AuthShell, DividerText, SocialButton } from "./auth-shell";

function MailIcon() {
  return (
    <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="1.8" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
      <path d="M4 6.75h16v10.5H4V6.75Z" />
      <path d="m4.5 7.5 7.5 5.25 7.5-5.25" />
    </svg>
  );
}

function EyeOffIcon() {
  return (
    <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="1.8" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
      <path d="m3 3 18 18" />
      <path d="M10.7 10.7a2 2 0 0 0 2.6 2.6" />
      <path d="M7.4 7.7C5.6 8.8 4.1 10.3 3 12c2.2 3.4 5.2 5.1 9 5.1 1.4 0 2.7-.2 3.8-.7" />
      <path d="M10.8 6.9c.4 0 .8-.1 1.2-.1 3.8 0 6.8 1.7 9 5.2-.5.8-1.1 1.5-1.8 2.1" />
    </svg>
  );
}

function GoogleIcon() {
  return (
    <svg viewBox="0 0 24 24" className="h-5 w-5" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
      <path fill="#4285F4" d="M21.6 12.23c0-.78-.07-1.53-.2-2.23H12v4.22h5.38a4.6 4.6 0 0 1-1.99 3.02v2.74h3.22c1.89-1.74 2.99-4.3 2.99-7.75Z" />
      <path fill="#34A853" d="M12 22c2.7 0 4.96-.9 6.61-2.42l-3.22-2.74c-.9.6-2.04.95-3.39.95-2.6 0-4.81-1.76-5.6-4.12H3.07v2.83A10 10 0 0 0 12 22Z" />
      <path fill="#FBBC05" d="M6.4 13.67A6 6 0 0 1 6.08 12c0-.58.1-1.14.31-1.67V7.5H3.07A10 10 0 0 0 2 12c0 1.61.39 3.14 1.07 4.5l3.33-2.83Z" />
      <path fill="#EA4335" d="M12 6.21c1.47 0 2.79.51 3.83 1.5l2.86-2.86C16.96 3.24 14.7 2.25 12 2.25A10 10 0 0 0 3.07 7.5l3.33 2.83C7.19 7.97 9.4 6.21 12 6.21Z" />
    </svg>
  );
}

function FacebookIcon() {
  return (
    <svg viewBox="0 0 24 24" className="h-5 w-5 text-[#1877F2]" fill="currentColor" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
      <path d="M22 12a10 10 0 1 0-11.56 9.88v-6.99H7.9V12h2.54V9.8c0-2.5 1.49-3.89 3.77-3.89 1.09 0 2.23.2 2.23.2v2.45h-1.26c-1.24 0-1.63.77-1.63 1.56V12h2.78l-.44 2.89h-2.34v6.99A10 10 0 0 0 22 12Z" />
    </svg>
  );
}

function DemoAccountsPanel({ onSelect }: { onSelect: (email: string) => void }) {
  return (
    <div className="rounded-2xl border border-blue-200 bg-blue-50 px-4 py-4 text-sm text-blue-950">
      <p className="font-bold">Demo logins</p>
      <p className="mt-1 text-blue-900">
        Password for all accounts: <code className="rounded bg-white/70 px-1.5 py-0.5 text-xs">{DEMO_PASSWORD}</code>
      </p>
      <div className="mt-3 space-y-2">
        {demoUsers.map((user) => (
          <button
            key={user.email}
            type="button"
            onClick={() => onSelect(user.email)}
            className="flex w-full items-center justify-between rounded-xl border border-blue-200 bg-white px-3 py-2 text-left transition-colors hover:bg-blue-100/40"
          >
            <span>
              <span className="block font-semibold capitalize text-slate-900">{user.role}</span>
              <span className="text-xs text-slate-600">{user.email}</span>
            </span>
            <span className="text-xs font-bold uppercase tracking-wide text-[#123c91]">Use</span>
          </button>
        ))}
      </div>
    </div>
  );
}

const inputClass =
  "h-12 w-full rounded-2xl border border-slate-300 bg-white px-4 text-sm font-semibold text-slate-900 outline-none placeholder:text-slate-500 focus:border-[#123c91] focus:ring-4 focus:ring-blue-100";

export function CombinedAuthPage({ initialMode = "login" }: { initialMode?: "login" | "signup" }) {
  const [mode, setMode] = useState<"login" | "signup">(initialMode);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [signupRole, setSignupRole] = useState<RegisterRole>("DEVELOPER");
  const [signup, setSignup] = useState({
    firstName: "",
    lastName: "",
    phoneLocal: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const isLogin = mode === "login";

  async function handleLogin(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      if (DEMO_AUTH_ENABLED) {
        const demoUser = authenticateDemoUser(email, password);
        if (demoUser) {
          saveDemoSession(demoUser);
          window.location.href = getDemoDashboardPath(demoUser.role);
          return;
        }
      }

      const auth = await authApi.login(email.trim(), password);
      saveAuthSession({
        username: auth.username,
        role: auth.role,
        accessToken: auth.accessToken,
        refreshToken: auth.refreshToken,
        userStatus: auth.userStatus,
      });
      window.location.href = getDashboardPath(mapBackendRole(auth.role));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Login failed");
    } finally {
      setLoading(false);
    }
  }

  async function handleSignup(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");

    if (signup.password !== signup.confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    setLoading(true);
    try {
      const auth = await authApi.register({
        firstName: signup.firstName.trim(),
        lastName: signup.lastName.trim(),
        phoneNumber: toE164Phone(signup.phoneLocal),
        email: signup.email.trim(),
        password: signup.password,
        role: signupRole,
      });
      saveAuthSession({
        username: signup.email.trim(),
        role: auth.role,
        accessToken: auth.accessToken,
        refreshToken: auth.refreshToken,
        userStatus: auth.userStatus,
        firstName: signup.firstName.trim(),
        lastName: signup.lastName.trim(),
      });
      window.location.href = getDashboardPath(mapBackendRole(auth.role));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Registration failed");
    } finally {
      setLoading(false);
    }
  }

  function handleDemoSelect(selectedEmail: string) {
    setEmail(selectedEmail);
    setPassword(DEMO_PASSWORD);
    setError("");
  }

  return (
    <AuthShell heading={isLogin ? "Welcome back!" : "Create account!"}>
      <div className="mt-6">
        {isLogin ? (
          <form className="space-y-4" onSubmit={handleLogin}>
            {DEMO_AUTH_ENABLED ? <DemoAccountsPanel onSelect={handleDemoSelect} /> : null}

            <AuthField
              type="email"
              name="email"
              placeholder="Enter your email address"
              icon={<MailIcon />}
              value={email}
              onChange={(event) => setEmail(event.target.value)}
            />
            <AuthField
              type="password"
              name="password"
              placeholder="Enter your password"
              icon={<EyeOffIcon />}
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />

            {error ? <p className="text-sm font-semibold text-rose-600">{error}</p> : null}

            <div className="flex items-center justify-between gap-4 text-base font-semibold text-slate-700">
              <label className="flex items-center gap-3">
                <input type="checkbox" defaultChecked className="h-5 w-5 rounded border-slate-300 accent-[#123c91]" />
                Remember me
              </label>
              <a href="#" className="font-black text-[#123c91]">
                Forgot password?
              </a>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="h-14 w-full rounded-full bg-[#123c91] text-base font-black text-white shadow-sm transition-colors hover:bg-[#0d2f76] disabled:opacity-60"
            >
              {loading ? "Signing in…" : "Login"}
            </button>

            <DividerText>or continue with</DividerText>

            <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-1 2xl:grid-cols-2">
              <SocialButton label="Google">
                <GoogleIcon />
              </SocialButton>
              <SocialButton label="Facebook">
                <FacebookIcon />
              </SocialButton>
            </div>
          </form>
        ) : (
          <form className="space-y-4" onSubmit={handleSignup}>
            <p className="text-sm text-slate-600">
              {signupRole === "DEVELOPER"
                ? "Create a developer account to get API keys and accept payments in your app."
                : "Create a personal Payflow account for wallet and payments."}
            </p>

            <div className="grid gap-3 sm:grid-cols-2">
              <label className="block">
                <span className="mb-1.5 block text-xs font-bold uppercase tracking-wide text-slate-500">Account type</span>
                <select
                  className={inputClass}
                  value={signupRole}
                  onChange={(e) => setSignupRole(e.target.value as RegisterRole)}
                >
                  <option value="DEVELOPER">Developer</option>
                  <option value="USER">Personal account</option>
                </select>
              </label>
            </div>

            <div className="grid gap-3 sm:grid-cols-2">
              <input className={inputClass} placeholder="First name" value={signup.firstName} onChange={(e) => setSignup({ ...signup, firstName: e.target.value })} required />
              <input className={inputClass} placeholder="Last name" value={signup.lastName} onChange={(e) => setSignup({ ...signup, lastName: e.target.value })} required />
            </div>

            <div className="flex overflow-hidden rounded-2xl border border-slate-300 bg-white">
              <span className="flex items-center bg-slate-100 px-4 text-sm font-bold text-slate-600">+220</span>
              <input
                className="h-12 flex-1 px-4 text-sm font-semibold outline-none"
                placeholder="712 3456"
                inputMode="numeric"
                value={signup.phoneLocal}
                onChange={(e) => setSignup({ ...signup, phoneLocal: e.target.value.replace(/\D/g, "").slice(0, 7) })}
                required
              />
            </div>

            <AuthField
              type="email"
              name="signup-email"
              placeholder="Email address"
              icon={<MailIcon />}
              value={signup.email}
              onChange={(event) => setSignup({ ...signup, email: event.target.value })}
            />
            <AuthField
              type="password"
              name="signup-password"
              placeholder="Password (min 8 characters)"
              icon={<EyeOffIcon />}
              value={signup.password}
              onChange={(event) => setSignup({ ...signup, password: event.target.value })}
            />
            <AuthField
              type="password"
              name="signup-confirm"
              placeholder="Confirm password"
              icon={<EyeOffIcon />}
              value={signup.confirmPassword}
              onChange={(event) => setSignup({ ...signup, confirmPassword: event.target.value })}
            />

            {error ? <p className="text-sm font-semibold text-rose-600">{error}</p> : null}

            <button
              type="submit"
              disabled={loading}
              className="h-14 w-full rounded-full bg-[#123c91] text-base font-black text-white shadow-sm transition-colors hover:bg-[#0d2f76] disabled:opacity-60"
            >
              {loading ? "Creating account…" : signupRole === "DEVELOPER" ? "Create developer account" : "Create account"}
            </button>

            <p className="text-center text-xs text-slate-500">
              Running a business in The Gambia?{" "}
              <Link href="/merchants/register" className="font-bold text-[#123c91]">
                Register as a merchant
              </Link>{" "}
              in the Payflow Merchant app.
            </p>
          </form>
        )}

        <p className="mt-4 text-center text-base text-slate-700">
          {isLogin ? "Don't have an account?" : "Already have an account?"}{" "}
          <button type="button" onClick={() => setMode(isLogin ? "signup" : "login")} className="font-black text-[#123c91]">
            {isLogin ? "Sign up" : "Login"}
          </button>
        </p>
      </div>
    </AuthShell>
  );
}
