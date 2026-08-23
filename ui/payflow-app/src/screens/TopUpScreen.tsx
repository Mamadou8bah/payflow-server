import { useState } from "react";
import { KeyboardAvoidingView, Platform, ScrollView, StyleSheet, Text } from "react-native";
import { BackHeader } from "../components/AppHeader";
import { AgentQrCard } from "../components/AgentQrCard";
import {
  AmountHero,
  BalanceBanner,
  FormError,
  FormField,
  FormInput,
  MethodTabs,
  SubmitButton,
  WalletPicker,
} from "../components/OperationForm";
import { formatFraudError } from "../utils/fraudErrors";
import { useWallet } from "../context/WalletContext";
import { fonts, spacing } from "../theme";

export function TopUpScreen() {
  const {
    data,
    topUp,
    depositQr,
    selectedWallet,
    selectedWalletId,
    setSelectedWalletId,
    setTab,
    balanceHidden,
  } = useWallet();
  const [amount, setAmount] = useState("");
  const [phone, setPhone] = useState(data.session.phone);
  const [method, setMethod] = useState<"mobile_money" | "agent">("mobile_money");
  const [error, setError] = useState<string | null>(null);

  const submit = async () => {
    const result = await topUp(Number(amount), method, phone, selectedWalletId);
    if (!result.ok) setError(formatFraudError(result.error ?? "Top up failed"));
    else setError(null);
  };

  return (
    <KeyboardAvoidingView style={styles.flex} behavior={Platform.OS === "ios" ? "padding" : undefined}>
      <ScrollView
        contentContainerStyle={styles.wrap}
        showsVerticalScrollIndicator={false}
        keyboardShouldPersistTaps="handled"
      >
        <BackHeader title="Replenish" onBack={() => setTab("home")} />

        <BalanceBanner wallet={selectedWallet} hidden={balanceHidden} label="Top up to" />

        <WalletPicker
          wallets={data.wallets}
          selectedId={selectedWalletId}
          onSelect={setSelectedWalletId}
          hidden={balanceHidden}
        />

        <AmountHero amount={amount} onChange={setAmount} currency={selectedWallet.currency} />

        <MethodTabs
          options={[
            { id: "mobile_money" as const, label: "Mobile money" },
            { id: "agent" as const, label: "Agent cash-in" },
          ]}
          value={method}
          onChange={setMethod}
        />

        {method === "mobile_money" ? (
          <FormField label="Phone number">
            <FormInput value={phone} onChangeText={setPhone} keyboardType="phone-pad" />
          </FormField>
        ) : (
          <Text style={styles.agentHint}>
            Generate a QR code and show it to a Payflow agent to deposit cash.
          </Text>
        )}

        <FormError message={error} />
        <SubmitButton
          label={method === "mobile_money" ? "Pay with mobile money" : "Generate deposit QR"}
          onPress={submit}
        />

        {depositQr && method === "agent" ? (
          <AgentQrCard payload={depositQr} title="Deposit QR code" hint="Show this to a Payflow agent." />
        ) : null}
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  flex: { flex: 1 },
  wrap: { padding: spacing.screen, paddingBottom: 120, gap: spacing.gap },
  agentHint: { fontSize: 14, fontFamily: fonts.regular, color: "#64748b", lineHeight: 20 },
});
