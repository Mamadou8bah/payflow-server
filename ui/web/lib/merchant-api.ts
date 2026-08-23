import { API_BASE } from "./api-base";

type ApiEnvelope<T> = {
  success: boolean;
  message: string;
  data: T;
};

async function post<T>(path: string, body: unknown): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });

  const payload = (await response.json()) as ApiEnvelope<T>;
  if (!response.ok || !payload.success) {
    throw new Error(payload.message || "Request failed");
  }
  return payload.data;
}

export type StepResponse = {
  registrationToken: string;
  stage: number;
  message: string;
  expiresAt: string;
};

export type CompleteResponse = {
  auth: {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    expiresInSeconds: number;
    userId: number;
    username: string;
    role: string;
    userStatus: string;
    twoFactorRequired: boolean;
    twoFactorChallengeId: string | null;
  };
  businessName: string;
  verificationStatus: string;
  message: string;
};

export const merchantRegistrationApi = {
  sendPhone: (phoneNumber: string) =>
    post<StepResponse>("/api/auth/merchant/register/phone", { phoneNumber }),

  verifyPhone: (registrationToken: string, code: string) =>
    post<StepResponse>("/api/auth/merchant/register/verify-phone", { registrationToken, code }),

  saveBusiness: (payload: {
    registrationToken: string;
    businessName: string;
    tradingName?: string;
    category: string;
    region: string;
    cityOrArea: string;
    streetAddress: string;
    businessRegistrationNumber?: string;
  }) => post<StepResponse>("/api/auth/merchant/register/business", payload),

  saveOwner: (payload: {
    registrationToken: string;
    firstName: string;
    lastName: string;
    ownerIdType: string;
    ownerIdNumber: string;
  }) => post<StepResponse>("/api/auth/merchant/register/owner", payload),

  complete: (payload: {
    registrationToken: string;
    email: string;
    password: string;
    confirmPassword: string;
    acceptedTerms: boolean;
  }) => post<CompleteResponse>("/api/auth/merchant/register/complete", payload),
};

export function saveMerchantSession(auth: CompleteResponse["auth"], businessName: string) {
  if (typeof window === "undefined") return;
  localStorage.setItem(
    "payflow_merchant_session",
    JSON.stringify({
      accessToken: auth.accessToken,
      refreshToken: auth.refreshToken,
      email: auth.username,
      role: auth.role,
      userStatus: auth.userStatus,
      businessName,
    }),
  );
}

export function updateMerchantSession(tokens: { accessToken: string; refreshToken: string }) {
  if (typeof window === "undefined") return;
  const current = getMerchantTokenSession();
  if (!current) return;
  localStorage.setItem(
    "payflow_merchant_session",
    JSON.stringify({ ...current, accessToken: tokens.accessToken, refreshToken: tokens.refreshToken }),
  );
}

export function clearMerchantSession() {
  if (typeof window === "undefined") return;
  localStorage.removeItem("payflow_merchant_session");
}

export function getMerchantTokenSession(): {
  accessToken: string;
  refreshToken: string;
  email: string;
  role: string;
  userStatus: string;
  businessName: string;
} | null {
  if (typeof window === "undefined") return null;
  const raw = localStorage.getItem("payflow_merchant_session");
  if (!raw) return null;
  try {
    return JSON.parse(raw) as ReturnType<typeof getMerchantTokenSession>;
  } catch {
    return null;
  }
}

export function getMerchantSession(): {
  accessToken: string;
  email: string;
  role: string;
  userStatus: string;
  businessName: string;
} | null {
  const session = getMerchantTokenSession();
  if (!session) return null;
  const { refreshToken: _r, ...rest } = session;
  return rest;
}
