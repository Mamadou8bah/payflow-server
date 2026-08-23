import { createContext, useCallback, useContext, useMemo, useState, type ReactNode } from "react";
import { agentApi } from "../api/client";
import { useAuth } from "./AuthContext";
import type {
  AgentOperation,
  AgentSession,
  AgentStats,
  AgentQrPayload,
  OpResult,
  TabId,
} from "../types";

const EMPTY_STATS: AgentStats = {
  todayDeposits: 0,
  todayWithdrawals: 0,
  todayVolume: 0,
  pendingCount: 0,
  completedToday: 0,
};

type AgentContextValue = {
  session: AgentSession;
  stats: AgentStats;
  tab: TabId;
  setTab: (tab: TabId) => void;
  pending: AgentOperation[];
  completed: AgentOperation[];
  activeOperation: AgentOperation | null;
  lookupReference: (reference: string) => Promise<OpResult>;
  lookupQr: (payload: AgentQrPayload) => Promise<OpResult>;
  completeActive: () => Promise<OpResult>;
  clearActive: () => void;
  openOperation: (operation: AgentOperation) => void;
  createOnBehalf: (
    type: "DEPOSIT" | "WITHDRAWAL",
    customerName: string,
    amount: number,
    currency: string,
  ) => OpResult;
  recentCompleted: AgentOperation[];
};

const AgentContext = createContext<AgentContextValue | null>(null);

export function AgentProvider({ children }: { children: ReactNode }) {
  const { session: authSession } = useAuth();
  const [tab, setTab] = useState<TabId>("home");
  const [pending, setPending] = useState<AgentOperation[]>([]);
  const [completed, setCompleted] = useState<AgentOperation[]>([]);
  const [stats, setStats] = useState(EMPTY_STATS);
  const [activeOperation, setActiveOperation] = useState<AgentOperation | null>(null);

  const agentSession = useMemo<AgentSession>(
    () => ({
      name:
        authSession?.firstName && authSession?.lastName
          ? `${authSession.firstName} ${authSession.lastName}`
          : authSession?.username ?? "Agent",
      agentCode: authSession?.username ?? "",
      location: "",
      phone: "",
      email: authSession?.username ?? "",
    }),
    [authSession],
  );

  const requireAuth = useCallback((): OpResult | null => {
    if (!authSession?.accessToken) {
      return { ok: false, error: "Please sign in to continue" };
    }
    return null;
  }, [authSession]);

  const lookupReference = useCallback(
    async (reference: string): Promise<OpResult> => {
      const authError = requireAuth();
      if (authError) return authError;

      try {
        const op = await agentApi.lookup(reference);
        const mapped: AgentOperation = {
          operation: op.operation as AgentOperation["operation"],
          reference: op.reference,
          walletId: op.walletId,
          walletName: op.walletName,
          amount: Number(op.amount),
          currency: op.currency,
          status: op.status as AgentOperation["status"],
          userId: op.userId,
          merchantName: op.merchantName,
          createdAt: new Date().toISOString(),
        };
        if (mapped.status === "COMPLETED") {
          return { ok: false, error: "This operation was already completed." };
        }
        setActiveOperation(mapped);
        setTab("confirm");
        return { ok: true };
      } catch (err) {
        return { ok: false, error: err instanceof Error ? err.message : "Operation not found." };
      }
    },
    [authSession, requireAuth],
  );

  const lookupQr = useCallback(
    async (payload: AgentQrPayload): Promise<OpResult> => {
      return lookupReference(payload.reference);
    },
    [lookupReference],
  );

  const completeActive = useCallback(async (): Promise<OpResult> => {
    if (!activeOperation) return { ok: false, error: "No operation selected." };
    if (activeOperation.status === "COMPLETED") {
      return { ok: false, error: "Already completed." };
    }

    const authError = requireAuth();
    if (authError) return authError;

    try {
      const op = await agentApi.complete(activeOperation.reference);
      const completedOp: AgentOperation = {
        ...activeOperation,
        status: op.status as AgentOperation["status"],
        createdAt: new Date().toISOString(),
      };
      setPending((prev) => prev.filter((item) => item.reference !== activeOperation.reference));
      setCompleted((prev) => [completedOp, ...prev]);
      setStats((prev) => ({
        ...prev,
        pendingCount: Math.max(0, prev.pendingCount - 1),
        completedToday: prev.completedToday + 1,
        todayDeposits: activeOperation.operation === "DEPOSIT" ? prev.todayDeposits + 1 : prev.todayDeposits,
        todayWithdrawals:
          activeOperation.operation === "WITHDRAWAL" ? prev.todayWithdrawals + 1 : prev.todayWithdrawals,
        todayVolume: prev.todayVolume + activeOperation.amount,
      }));
      setActiveOperation(completedOp);
      return { ok: true };
    } catch (err) {
      return { ok: false, error: err instanceof Error ? err.message : "Completion failed." };
    }
  }, [activeOperation, authSession, requireAuth]);

  const clearActive = useCallback(() => {
    setActiveOperation(null);
  }, []);

  const openOperation = useCallback((operation: AgentOperation) => {
    setActiveOperation(operation);
    setTab("confirm");
  }, []);

  const createOnBehalf = useCallback(
    (_type: "DEPOSIT" | "WITHDRAWAL", _customerName: string, _amount: number, _currency: string): OpResult => {
      return {
        ok: false,
        error: "Creating operations on behalf of customers is not available. Ask the customer to start from their app.",
      };
    },
    [],
  );

  const recentCompleted = useMemo(() => completed.slice(0, 5), [completed]);

  const value = useMemo(
    () => ({
      session: agentSession,
      stats,
      tab,
      setTab,
      pending,
      completed,
      activeOperation,
      lookupReference,
      lookupQr,
      completeActive,
      clearActive,
      openOperation,
      createOnBehalf,
      recentCompleted,
    }),
    [
      agentSession,
      stats,
      tab,
      pending,
      completed,
      activeOperation,
      lookupReference,
      lookupQr,
      completeActive,
      clearActive,
      openOperation,
      createOnBehalf,
      recentCompleted,
    ],
  );

  return <AgentContext.Provider value={value}>{children}</AgentContext.Provider>;
}

export function useAgent() {
  const ctx = useContext(AgentContext);
  if (!ctx) throw new Error("useAgent must be used within AgentProvider");
  return ctx;
}

export function navTabFor(tab: TabId): TabId {
  if (tab === "confirm" || tab === "new-deposit" || tab === "new-withdrawal") return "scan";
  if (tab === "profile" || tab === "support") return "more";
  return tab;
}
