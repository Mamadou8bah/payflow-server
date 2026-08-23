import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { api, type AuthSession } from "../api/client";
import { bindTokenSession } from "../api/sessionAuth";
import { clearSession, loadSession, saveSession } from "../storage/secureSession";

const SESSION_KEY = "payflow_customer_session";

type AuthContextValue = {
  session: AuthSession | null;
  loading: boolean;
  signIn: (email: string, password: string) => Promise<void>;
  signOut: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<AuthSession | null>(null);
  const [loading, setLoading] = useState(true);

  const persistSession = useCallback(async (stored: AuthSession) => {
    await saveSession(SESSION_KEY, JSON.stringify(stored));
    setSession(stored);
  }, []);

  const signOut = useCallback(async () => {
    await clearSession(SESSION_KEY);
    setSession(null);
  }, []);

  useEffect(() => {
    bindTokenSession({
      getSession: () => session,
      updateSession: persistSession,
      clearSession: signOut,
    });
  }, [session, persistSession, signOut]);

  useEffect(() => {
    loadSession(SESSION_KEY)
      .then((raw) => {
        if (raw) setSession(JSON.parse(raw) as AuthSession);
      })
      .catch(() => {
        // Corrupted session — force re-login
      })
      .finally(() => setLoading(false));
  }, []);

  const signIn = useCallback(async (email: string, password: string) => {
    const auth = await api.login(email, password);
    if (auth.twoFactorRequired) {
      throw new Error("Two-factor authentication is required for this account");
    }
    if (auth.role === "AGENT") {
      throw new Error("Agent accounts must use the Payflow Agent app");
    }
    const stored: AuthSession = {
      accessToken: auth.accessToken,
      refreshToken: auth.refreshToken,
      username: auth.username,
      role: auth.role,
      firstName: auth.firstName,
      lastName: auth.lastName,
    };
    await persistSession(stored);
  }, [persistSession]);

  const value = useMemo(
    () => ({ session, loading, signIn, signOut }),
    [session, loading, signIn, signOut],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
