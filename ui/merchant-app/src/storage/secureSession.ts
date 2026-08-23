import AsyncStorage from "@react-native-async-storage/async-storage";
import { Platform } from "react-native";

async function getSecureStore() {
  if (Platform.OS === "web") return null;
  try {
    return await import("expo-secure-store");
  } catch {
    return null;
  }
}

export async function saveSession(key: string, value: string): Promise<void> {
  const secureStore = await getSecureStore();
  if (secureStore) {
    await secureStore.setItemAsync(key, value);
    return;
  }
  await AsyncStorage.setItem(key, value);
}

export async function loadSession(key: string): Promise<string | null> {
  const secureStore = await getSecureStore();
  if (secureStore) {
    return secureStore.getItemAsync(key);
  }
  return AsyncStorage.getItem(key);
}

export async function clearSession(key: string): Promise<void> {
  const secureStore = await getSecureStore();
  if (secureStore) {
    await secureStore.deleteItemAsync(key);
    return;
  }
  await AsyncStorage.removeItem(key);
}
