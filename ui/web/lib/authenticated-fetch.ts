import { API_BASE } from "./api-base";
import { clearAuthSession, getAuthSession, updateAuthSession } from "./auth-session";
import { clearMerchantSession, getMerchantTokenSession, updateMerchantSession } from "./merchant-api";

let refreshInFlight: Promise<string | null> | null = null;

function getTokenSession() {
  const auth = getAuthSession();
  if (auth?.accessToken && auth.refreshToken) {
    return { accessToken: auth.accessToken, refreshToken: auth.refreshToken, source: "auth" as const };
  }
  const merchant = getMerchantTokenSession();
  if (merchant) {
    return { ...merchant, source: "merchant" as const };
  }
  return null;
}

async function refreshAccessToken(): Promise<string | null> {
  if (refreshInFlight) return refreshInFlight;

  refreshInFlight = (async () => {
    const session = getTokenSession();
    if (!session?.refreshToken) return null;

    try {
      const response = await fetch(`${API_BASE}/api/auth/refresh`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ refreshToken: session.refreshToken }),
      });
      const text = await response.text();
      let payload: { success?: boolean; data?: { accessToken: string; refreshToken: string } };
      try {
        payload = JSON.parse(text);
      } catch {
        return null;
      }
      if (!response.ok || !payload.success || !payload.data) return null;

      if (session.source === "auth") {
        updateAuthSession(payload.data);
      } else {
        updateMerchantSession(payload.data);
      }
      return payload.data.accessToken;
    } catch {
      return null;
    } finally {
      refreshInFlight = null;
    }
  })();

  return refreshInFlight;
}

function clearAllSessions() {
  clearAuthSession();
  clearMerchantSession();
}

export async function authenticatedFetch(path: string, init?: RequestInit): Promise<Response> {
  const session = getTokenSession();
  if (!session?.accessToken) {
    throw new Error("Not authenticated");
  }

  const withAuth = (token: string) =>
    fetch(`${API_BASE}${path}`, {
      ...init,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
        ...(init?.headers ?? {}),
      },
    });

  let response = await withAuth(session.accessToken);
  if (response.status !== 401) return response;

  const newToken = await refreshAccessToken();
  if (!newToken) {
    clearAllSessions();
    throw new Error("Session expired — please sign in again");
  }

  response = await withAuth(newToken);
  if (response.status === 401) {
    clearAllSessions();
    throw new Error("Session expired — please sign in again");
  }
  return response;
}

export async function readAuthenticatedJson<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const text = await response.text();
    try {
      const json = JSON.parse(text) as { message?: string };
      throw new Error(json.message || text || `Request failed (${response.status})`);
    } catch (err) {
      if (err instanceof Error && err.message !== text) throw err;
      throw new Error(text || `Request failed (${response.status})`);
    }
  }
  return (await response.json()) as T;
}
