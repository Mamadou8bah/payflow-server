import { useState } from "react";
import { KeyboardAvoidingView, Platform, ScrollView, StyleSheet } from "react-native";
import { BackHeader } from "../components/AppHeader";
import { AppInput, Field, FormError, PrimaryButton } from "../components/ui";
import { useAgent } from "../context/AgentContext";
import { brand } from "../constants/brand";
import { spacing } from "../theme";

export function NewWithdrawalScreen() {
  const { createOnBehalf, setTab } = useAgent();
  const [customer, setCustomer] = useState("");
  const [amount, setAmount] = useState("");
  const [error, setError] = useState<string | null>(null);

  function submit() {
    setError(null);
    const result = createOnBehalf("WITHDRAWAL", customer, Number(amount), "GMD");
    if (!result.ok) setError(result.error);
  }

  return (
    <KeyboardAvoidingView style={styles.flex} behavior={Platform.OS === "ios" ? "padding" : undefined}>
      <ScrollView contentContainerStyle={styles.content} keyboardShouldPersistTaps="handled">
        <BackHeader title="New cash-out" onBack={() => setTab("home")} />
        <Field label="Customer name">
          <AppInput value={customer} onChangeText={setCustomer} placeholder="Customer full name" />
        </Field>
        <Field label="Amount (GMD)" hint="Cash to hand over after verification">
          <AppInput value={amount} onChangeText={setAmount} keyboardType="decimal-pad" placeholder="0.00" />
        </Field>
        <FormError message={error} />
        <PrimaryButton label="Create withdrawal" onPress={submit} color={brand.orange} />
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  flex: { flex: 1 },
  content: { padding: spacing.screen, paddingBottom: 120, gap: 16 },
});
