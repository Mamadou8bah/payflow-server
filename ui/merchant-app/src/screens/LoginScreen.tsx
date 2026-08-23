import { useState } from "react";
import { StyleSheet, Text, View } from "react-native";
import { AppInput, Field, LinkButton, PayflowLogo, PrimaryButton, Screen } from "../components/ui";
import { useApp } from "../context/AppContext";
import { colors } from "../theme";

export function LoginScreen() {
  const { signIn, setScreen, setError, error } = useApp();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit() {
    setError("");
    setLoading(true);
    try {
      await signIn(email, password);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Login failed");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Screen>
      <PayflowLogo />
      <Text style={styles.title}>Welcome back</Text>
      <Text style={styles.sub}>Sign in to your merchant account</Text>

      <Field label="Email">
        <AppInput autoCapitalize="none" keyboardType="email-address" value={email} onChangeText={setEmail} placeholder="you@business.gm" />
      </Field>
      <Field label="Password">
        <AppInput secureTextEntry value={password} onChangeText={setPassword} placeholder="Your password" />
      </Field>

      {error ? <Text style={styles.error}>{error}</Text> : null}

      <PrimaryButton label={loading ? "Signing in…" : "Sign in"} onPress={handleSubmit} disabled={loading} />
      <LinkButton label="Create a merchant account" onPress={() => setScreen("reg-phone")} />
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 26, fontWeight: "800", color: colors.slate900 },
  sub: { fontSize: 14, color: colors.slate500, marginBottom: 8 },
  error: { color: colors.rose800, fontWeight: "600", fontSize: 14 },
});
