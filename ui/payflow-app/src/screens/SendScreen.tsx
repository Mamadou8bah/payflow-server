import { useState } from "react";
import { KeyboardAvoidingView, Platform, StyleSheet } from "react-native";
import {
  AmountHero,
  BalanceBanner,
  ContactChips,
  FormError,
  FormField,
  FormInput,
  OperationScreen,
  SubmitButton,
  WalletPicker,
} from "../components/OperationForm";
import { formatFraudError } from "../utils/fraudErrors";
import { useWallet } from "../context/WalletContext";

export function SendScreen() {
  const {
    data,
    sendMoney,
    selectedWalletId,
    setSelectedWalletId,
    selectedWallet,
    balanceHidden,
  } = useWallet();
  const [destination, setDestination] = useState("");
  const [amount, setAmount] = useState("");
  const [error, setError] = useState<string | null>(null);

  const submit = async () => {
    const result = await sendMoney(destination.trim(), Number(amount), selectedWalletId);
    if (!result.ok) setError(formatFraudError(result.error ?? "Transfer failed"));
    else setError(null);
  };

  return (
    <KeyboardAvoidingView style={styles.flex} behavior={Platform.OS === "ios" ? "padding" : undefined}>
      <OperationScreen title="Send money" subtitle="Free wallet-to-wallet transfers">
        <BalanceBanner wallet={selectedWallet} hidden={balanceHidden} />

        <ContactChips
          contacts={data.contacts}
          selected={destination}
          onSelect={(phone) => {
            setDestination(phone);
            setError(null);
          }}
        />

        <WalletPicker
          wallets={data.wallets}
          selectedId={selectedWalletId}
          onSelect={setSelectedWalletId}
          hidden={balanceHidden}
        />

        <AmountHero amount={amount} onChange={setAmount} currency={selectedWallet.currency} />

        <FormField label="Recipient phone or wallet ID">
          <FormInput
            placeholder="+220 7XX XXXX"
            value={destination}
            onChangeText={(v) => {
              setDestination(v);
              setError(null);
            }}
            keyboardType="phone-pad"
          />
        </FormField>

        <FormError message={error} />
        <SubmitButton label="Send transfer" onPress={submit} />
      </OperationScreen>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  flex: { flex: 1 },
});
