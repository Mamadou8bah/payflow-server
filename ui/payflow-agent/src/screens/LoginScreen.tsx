import { useState } from "react";
import { ActivityIndicator, StyleSheet, Text, TextInput, View } from "react-native";
import { PrimaryButton, Screen } from "../components/ui";
import { useAuth } from "../context/AuthContext";
import { colors } from "../theme";

export function LoginScreen() {
  const { signIn } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleLogin() {
    setError("");
    setLoading(true);
    try {
      await signIn(email.trim(), password);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Login failed");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Screen>
      <Text style={styles.title}>Payflow Agent</Text>
      <Text style={styles.subtitle}>Sign in to process cash-in and cash-out operations</Text>

      <TextInput
        style={styles.input}
        placeholder="Agent email"
        autoCapitalize="none"
        value={email}
        onChangeText={setEmail}
      />
      <TextInput
        style={styles.input}
        placeholder="Password"
        secureTextEntry
        value={password}
        onChangeText={setPassword}
      />

      {error ? <Text style={styles.error}>{error}</Text> : null}

      {loading ? (
        <ActivityIndicator color={colors.primary} />
      ) : (
        <PrimaryButton label="Sign in" onPress={handleLogin} />
      )}
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 28, fontWeight: "800", color: colors.slate900 },
  subtitle: { marginTop: 8, marginBottom: 24, fontSize: 14, color: colors.slate500 },
  input: {
    marginBottom: 12,
    borderWidth: 1,
    borderColor: colors.slate300,
    borderRadius: 14,
    paddingHorizontal: 14,
    paddingVertical: 12,
    fontSize: 15,
    backgroundColor: "#fff",
  },
  error: { marginBottom: 12, color: colors.rose500, fontSize: 13, fontWeight: "600" },
});
