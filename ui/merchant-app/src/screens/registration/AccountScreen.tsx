import { useState } from "react";
import { Pressable, StyleSheet, Text } from "react-native";
import { merchantRegistrationApi } from "../../api/merchantRegistration";
import { saveSession } from "../../storage/session";
import { AppInput, Field, LinkButton, PrimaryButton, Screen } from "../../components/ui";
import { StepIndicator } from "../../components/StepIndicator";
import { useApp } from "../../context/AppContext";
import { colors } from "../../theme";

export function AccountScreen() {
  const { reg, updateReg, setScreen, setError, error, completeRegistration, refreshSession } = useApp();
  const [loading, setLoading] = useState(false);
  const account = reg.account;

  async function handleSubmit() {
    setError("");
    if (account.password !== account.confirmPassword) {
      setError("Passwords do not match");
      return;
    }
    if (!account.acceptedTerms) {
      setError("Please accept the terms to continue");
      return;
    }

    setLoading(true);
    try {
      const res = await merchantRegistrationApi.complete({
        registrationToken: reg.token,
        email: account.email,
        password: account.password,
        confirmPassword: account.confirmPassword,
        acceptedTerms: account.acceptedTerms,
      });
      await saveSession(res.auth, res.businessName);
      await refreshSession();
      completeRegistration(res.businessName, res.auth.userStatus);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Registration failed");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Screen>
      <Text style={styles.title}>Create login</Text>
      <StepIndicator current={5} />

      <Field label="Email">
        <AppInput autoCapitalize="none" keyboardType="email-address" value={account.email} onChangeText={(v) => updateReg({ account: { ...account, email: v } })} />
      </Field>
      <Field label="Password">
        <AppInput secureTextEntry value={account.password} onChangeText={(v) => updateReg({ account: { ...account, password: v } })} />
      </Field>
      <Field label="Confirm password">
        <AppInput secureTextEntry value={account.confirmPassword} onChangeText={(v) => updateReg({ account: { ...account, confirmPassword: v } })} />
      </Field>

      <Pressable onPress={() => updateReg({ account: { ...account, acceptedTerms: !account.acceptedTerms } })} style={styles.termsRow}>
        <Text style={styles.checkbox}>{account.acceptedTerms ? "☑" : "☐"}</Text>
        <Text style={styles.termsText}>I agree to Payflow merchant terms and confirm the information provided is accurate.</Text>
      </Pressable>

      {error ? <Text style={styles.error}>{error}</Text> : null}

      <PrimaryButton label={loading ? "Submitting…" : "Submit application"} onPress={handleSubmit} disabled={loading} />
      <LinkButton label="Back" onPress={() => setScreen("reg-owner")} />
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 24, fontWeight: "800", color: colors.slate900 },
  error: { color: colors.rose800, fontWeight: "600" },
  termsRow: { flexDirection: "row", gap: 10, alignItems: "flex-start" },
  checkbox: { fontSize: 18, color: colors.primary },
  termsText: { flex: 1, fontSize: 13, lineHeight: 20, color: colors.slate700 },
});
