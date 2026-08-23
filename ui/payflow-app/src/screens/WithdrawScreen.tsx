import { useState } from "react";
import { KeyboardAvoidingView, Platform, ScrollView, StyleSheet, Text } from "react-native";
import { BackHeader } from "../components/AppHeader";
import { AgentQrCard } from "../components/AgentQrCard";
import {
  AmountHero,
  BalanceBanner,
  FormError,
  SubmitButton,
  WalletPicker,
} from "../components/OperationForm";
import { formatFraudError } from "../utils/fraudErrors";
import { useWallet } from "../context/WalletContext";
import { fonts, spacing } from "../theme";

export function WithdrawScreen() {
  const {
    data,
    withdraw,
    withdrawalQr,
    selectedWallet,
    selectedWalletId,
    setSelectedWalletId,
    setTab,
    balanceHidden,
  } = useWallet();
  const [amount, setAmount] = useState("");
  const [error, setError] = useState<string | null>(null);

  const submit = async () => {
    const result = await withdraw(Number(amount), selectedWalletId);
    if (!result.ok) setError(formatFraudError(result.error ?? "Withdrawal failed"));
    else setError(null);
  };

  return (
    <KeyboardAvoidingView style={styles.flex} behavior={Platform.OS === "ios" ? "padding" : undefined}>
      <ScrollView
        contentContainerStyle={styles.wrap}
        showsVerticalScrollIndicator={false}
        keyboardShouldPersistTaps="handled"
      >
        <BackHeader title="Withdraw cash" onBack={() => setTab("home")} />

        <Text style={styles.subtitle}>Collect cash from any Payflow agent near you.</Text>

        <BalanceBanner wallet={selectedWallet} hidden={balanceHidden} />

        <WalletPicker
          wallets={data.wallets}
          selectedId={selectedWalletId}
          onSelect={setSelectedWalletId}
          hidden={balanceHidden}
        />

        <AmountHero amount={amount} onChange={setAmount} currency={selectedWallet.currency} />

        <FormError message={error} />
        <SubmitButton label="Request withdrawal" onPress={submit} />

        {withdrawalQr ? (
          <AgentQrCard payload={withdrawalQr} title="Withdrawal QR code" hint="Show this to receive cash." />
        ) : null}
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  flex: { flex: 1 },
  wrap: { padding: spacing.screen, paddingBottom: 120, gap: spacing.gap },
  subtitle: { fontSize: 14, fontFamily: fonts.regular, color: "#64748b", marginTop: -8 },
});
