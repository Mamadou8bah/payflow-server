export function resolveApiBase(envValue: string | undefined): string {
  if (envValue) {
    if (!__DEV__ && envValue.startsWith("http://")) {
      throw new Error("EXPO_PUBLIC_API_BASE_URL must use HTTPS in production builds");
    }
    return envValue.replace(/\/+$/, "");
  }
  if (__DEV__) {
    return "http://localhost:5000";
  }
  throw new Error("EXPO_PUBLIC_API_BASE_URL is required for production builds");
}

export const API_BASE = resolveApiBase(process.env.EXPO_PUBLIC_API_BASE_URL);
