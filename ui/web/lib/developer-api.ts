import { authenticatedFetch, readAuthenticatedJson } from "./authenticated-fetch";

type ApiEnvelope<T> = {
  success: boolean;
  message: string;
  data: T;
};

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await authenticatedFetch(path, init);
  const payload = await readAuthenticatedJson<ApiEnvelope<T>>(response);
  if (!payload.success) {
    throw new Error(payload.message || "Request failed");
  }
  return payload.data;
}

export type ApiKeyInfo = {
  name: string;
  publicId: string;
  expiresAt: string;
  revoked: boolean;
  apiToken: string | null;
};

export const developerApi = {
  listApiKeys: () => request<ApiKeyInfo[]>("/api/auth/api-keys"),

  createApiKey: (name: string, expiresInDays: number) =>
    request<ApiKeyInfo>("/api/auth/api-keys", {
      method: "POST",
      body: JSON.stringify({ name, expiresInDays }),
    }),

  revokeApiKey: (apiKey: string) =>
    request<string>("/api/auth/api-keys/revoke", {
      method: "POST",
      headers: { "X-Api-Key": apiKey },
    }),

  deleteApiKey: (apiKey: string) =>
    request<string>("/api/auth/api-keys", {
      method: "DELETE",
      headers: { "X-Api-Key": apiKey },
    }),
};
