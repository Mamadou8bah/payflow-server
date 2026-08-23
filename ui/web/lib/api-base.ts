export function resolveApiBase(envValue: string | undefined): string {
  if (envValue) {
    if (process.env.NODE_ENV === "production" && envValue.startsWith("http://")) {
      throw new Error("NEXT_PUBLIC_API_BASE_URL must use HTTPS in production");
    }
    return envValue.replace(/\/+$/, "");
  }
  if (process.env.NODE_ENV !== "production") {
    return "http://localhost:5000";
  }
  throw new Error("NEXT_PUBLIC_API_BASE_URL is required in production");
}

export const API_BASE = resolveApiBase(process.env.NEXT_PUBLIC_API_BASE_URL);
