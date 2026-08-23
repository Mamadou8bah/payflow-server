import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { authApi } from "../api/merchantRegistration";
import { bindTokenSession } from "../api/sessionAuth";
import { clearSession, getSession, saveSession, updateStoredSession, type MerchantSession } from "../storage/session";

export type Screen =
  | "welcome"
  | "login"
  | "reg-phone"
  | "reg-otp"
  | "reg-business"
  | "reg-owner"
  | "reg-account"
  | "pending"
  | "home";

type RegistrationDraft = {
  token: string;
  phoneLocal: string;
  otp: string;
  business: {
    businessName: string;
    tradingName: string;
    category: string;
    region: string;
    cityOrArea: string;
    streetAddress: string;
    businessRegistrationNumber: string;
  };
  owner: {
    firstName: string;
    lastName: string;
    ownerIdType: string;
    ownerIdNumber: string;
  };
  account: {
    email: string;
    password: string;
    confirmPassword: string;
    acceptedTerms: boolean;
  };
};

type AppContextValue = {
  screen: Screen;
  setScreen: (screen: Screen) => void;
  session: MerchantSession | null;
  loading: boolean;
  error: string;
  setError: (message: string) => void;
  reg: RegistrationDraft;
  updateReg: (patch: Partial<RegistrationDraft>) => void;
  resetReg: () => void;
  refreshSession: () => Promise<void>;
  signIn: (email: string, password: string) => Promise<void>;
  signOut: () => Promise<void>;
  completeRegistration: (businessName: string, userStatus: string) => void;
};

const emptyReg = (): RegistrationDraft => ({
  token: "",
  phoneLocal: "",
  otp: "",
  business: {
    businessName: "",
    tradingName: "",
    category: "RETAIL_SHOP",
    region: "KANIFING",
    cityOrArea: "",
    streetAddress: "",
    businessRegistrationNumber: "",
  },
  owner: {
    firstName: "",
    lastName: "",
    ownerIdType: "GAMBIAN_NATIONAL_ID",
    ownerIdNumber: "",
  },
  account: {
    email: "",
    password: "",
    confirmPassword: "",
    acceptedTerms: false,
  },
});

const AppContext = createContext<AppContextValue | null>(null);

export function AppProvider({ children }: { children: ReactNode }) {
  const [screen, setScreen] = useState<Screen>("welcome");
  const [session, setSession] = useState<MerchantSession | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [reg, setReg] = useState<RegistrationDraft>(emptyReg);

  const refreshSession = useCallback(async () => {
    const stored = await getSession();
    setSession(stored);
    if (stored?.userStatus === "PENDING_REVIEW") {
      setScreen("pending");
    } else if (stored) {
      setScreen("home");
    }
  }, []);

  const signOut = useCallback(async () => {
    await clearSession();
    setSession(null);
    setScreen("welcome");
  }, []);

  const persistSession = useCallback(async (stored: MerchantSession) => {
    await updateStoredSession(stored);
    setSession(stored);
  }, []);

  useEffect(() => {
    bindTokenSession({
      getSession: () => session,
      updateSession: persistSession,
      clearSession: signOut,
    });
  }, [session, persistSession, signOut]);

  useEffect(() => {
    refreshSession().finally(() => setLoading(false));
  }, [refreshSession]);

  const value = useMemo<AppContextValue>(
    () => ({
      screen,
      setScreen,
      session,
      loading,
      error,
      setError,
      reg,
      updateReg: (patch) => setReg((prev) => ({ ...prev, ...patch })),
      resetReg: () => setReg(emptyReg()),
      refreshSession,
      signIn: async (email, password) => {
        setError("");
        const auth = await authApi.login(email.trim(), password);
        if (auth.role !== "MERCHANT") {
          throw new Error("This app is for merchant accounts only");
        }
        await saveSession(auth, "");
        await refreshSession();
      },
      signOut,
      completeRegistration: (businessName, userStatus) => {
        if (userStatus === "PENDING_REVIEW") {
          setScreen("pending");
        } else {
          setScreen("home");
        }
      },
    }),
    [screen, session, loading, error, reg, refreshSession, signOut],
  );

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
}

export function useApp() {
  const ctx = useContext(AppContext);
  if (!ctx) throw new Error("useApp must be used within AppProvider");
  return ctx;
}
