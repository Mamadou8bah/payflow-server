"use client";

import { useState } from "react";
import { AuthField, AuthShell, DividerText, SocialButton } from "./auth-shell";

function UserIcon() {
  return (
    <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="1.8" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
      <path d="M12 12.25a4 4 0 1 0 0-8 4 4 0 0 0 0 8Z" />
      <path d="M4.75 20.25a7.25 7.25 0 0 1 14.5 0" />
    </svg>
  );
}

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

export function CombinedAuthPage() {
  const [mode, setMode] = useState<"login" | "signup">("login");
  const isLogin = mode === "login";

  return (
    <AuthShell heading={isLogin ? "Welcome back!" : "Create account!"}>
      <div className="mt-12">
        {isLogin ? (
          <form className="space-y-6">
            <AuthField type="email" placeholder="Enter your email address" icon={<MailIcon />} />
            <AuthField type="password" placeholder="Enter your password" icon={<EyeOffIcon />} />

            <div className="flex items-center justify-between gap-4 text-base font-semibold text-slate-700">
              <label className="flex items-center gap-3">
                <input type="checkbox" defaultChecked className="h-5 w-5 rounded border-slate-300 accent-[#123c91]" />
                Remember me
              </label>
              <a href="#" className="font-black text-[#123c91]">
                Forgot password?
              </a>
            </div>

            <button type="button" className="h-14 w-full rounded-full bg-[#123c91] text-base font-black text-white shadow-sm transition-colors hover:bg-[#0d2f76]">
              Login
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
          <form className="space-y-5">
            <AuthField type="text" placeholder="Enter your full name" icon={<UserIcon />} />
            <AuthField type="email" placeholder="Enter your email address" icon={<MailIcon />} />
            <AuthField type="password" placeholder="Create your password" icon={<EyeOffIcon />} />
            <AuthField type="password" placeholder="Confirm your password" icon={<EyeOffIcon />} />

            <label className="flex items-start gap-2 text-xs font-semibold leading-relaxed text-slate-700">
              <input type="checkbox" defaultChecked className="mt-0.5 h-4 w-4 rounded border-slate-300 accent-[#123c91]" />
              I agree to Payflow's terms, privacy policy, and secure payment rules.
            </label>

            <button type="button" className="h-14 w-full rounded-full bg-[#123c91] text-base font-black text-white shadow-sm transition-colors hover:bg-[#0d2f76]">
              Sign up
            </button>

            <DividerText>or continue with</DividerText>

            <div className="grid gap-3 sm:grid-cols-2">
              <SocialButton label="Google">
                <GoogleIcon />
              </SocialButton>
              <SocialButton label="Facebook">
                <FacebookIcon />
              </SocialButton>
            </div>
          </form>
        )}

        <p className="mt-14 text-center text-base text-slate-700">
          {isLogin ? "Don't have an account?" : "Already have an account?"}{" "}
          <button type="button" onClick={() => setMode(isLogin ? "signup" : "login")} className="font-black text-[#123c91]">
            {isLogin ? "Sign up" : "Login"}
          </button>
        </p>
      </div>
    </AuthShell>
  );
}
