"use client";

import { useEffect, useState } from "react";
import { getAuthSession, type AppSession } from "../../lib/auth-session";
import { getDemoSession } from "../../lib/demo-auth";
import { CustomerAppView } from "../app/customer-app-view";
import { AdminDashboardView } from "../operations/admin/admin-dashboard-view";
import { DashboardLoadingSkeleton } from "../components/payflow-loader";
import { DeveloperDashboardView } from "./developer-dashboard-view";
import { MerchantDashboardView } from "./merchant-dashboard-view";

function readSession(): AppSession | null {
  if (typeof window === "undefined") return null;
  const auth = getAuthSession();
  if (auth) return auth;
  const demo = getDemoSession();
  if (!demo) return null;
  return { ...demo, source: "demo" };
}

export function DashboardView() {
  const [session, setSession] = useState<AppSession | null>(readSession);
  const [ready, setReady] = useState(() => typeof window !== "undefined");

  useEffect(() => {
    setSession(readSession());
    setReady(true);
  }, []);

  if (!ready || !session) {
    return <DashboardLoadingSkeleton />;
  }

  switch (session.role) {
    case "admin":
      return <AdminDashboardView />;
    case "merchant":
      return <MerchantDashboardView />;
    case "developer":
      return <DeveloperDashboardView />;
    case "customer":
      return <CustomerAppView />;
  }
}
