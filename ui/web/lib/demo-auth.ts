import { findDemoUser, getDashboardPath, type DemoRole, type DemoUser } from "./mock/demo-users";

/** Demo logins are opt-in for local development only. Never enable in production builds. */
export const DEMO_AUTH_ENABLED = process.env.NEXT_PUBLIC_ENABLE_DEMO_AUTH === "true";

const SESSION_KEY = "payflow_demo_session";

export type DemoSession = Pick<DemoUser, "email" | "role" | "name">;

function readStorage(): Storage | null {
  if (typeof window === "undefined") return null;
  return window.localStorage;
}

export function authenticateDemoUser(email: string, password: string): DemoUser | null {
  if (!DEMO_AUTH_ENABLED) return null;
  return findDemoUser(email, password);
}

export function saveDemoSession(user: DemoUser): void {
  if (!DEMO_AUTH_ENABLED) return;
  const storage = readStorage();
  if (!storage) return;

  const session: DemoSession = {
    email: user.email,
    role: user.role,
    name: user.name,
  };

  storage.setItem(SESSION_KEY, JSON.stringify(session));
}

export function getDemoSession(): DemoSession | null {
  if (!DEMO_AUTH_ENABLED) return null;
  const storage = readStorage();
  if (!storage) return null;

  const raw = storage.getItem(SESSION_KEY);
  if (!raw) return null;

  try {
    return JSON.parse(raw) as DemoSession;
  } catch {
    return null;
  }
}

export function clearDemoSession(): void {
  const storage = readStorage();
  if (!storage) return;
  storage.removeItem(SESSION_KEY);
}

export function getDemoDashboardPath(role: DemoRole): string {
  return getDashboardPath(role);
}
