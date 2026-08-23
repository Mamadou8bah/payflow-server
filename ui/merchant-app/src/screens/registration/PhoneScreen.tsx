import { useState } from "react";
import { StyleSheet, Text, View } from "react-native";
import { merchantRegistrationApi } from "../../api/merchantRegistration";
import { AppInput, Field, LinkButton, PayflowLogo, PrimaryButton, Screen } from "../../components/ui";
import { StepIndicator } from "../../components/StepIndicator";
import { formatGambianPhone, toE164Phone } from "../../constants/registration";
import { useApp } from "../../context/AppContext";
import { colors } from "../../theme";

export function PhoneScreen() {
  const { reg, updateReg, setScreen, setError, error } = useApp();
  const [loading, setLoading] = useState(false);

  async function handleSubmit() {
    setError("");
    setLoading(true);
    try {
      const res = await merchantRegistrationApi.sendPhone(toE164Phone(reg.phoneLocal));
      updateReg({ token: res.registrationToken });
      setScreen("reg-otp");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not send code");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Screen>
      <PayflowLogo />
      <Text style={styles.title}>Verify your phone</Text>
      <StepIndicator current={1} />

      <Field label="Gambian mobile" hint="We will send a 6-digit code by SMS">
        <View style={styles.phoneRow}>
          <Text style={styles.prefix}>+220</Text>
          <AppInput
            style={styles.phoneInput}
            keyboardType="number-pad"
            value={formatGambianPhone(reg.phoneLocal)}
            onChangeText={(v) => updateReg({ phoneLocal: v.replace(/\D/g, "").slice(0, 7) })}
            placeholder="712 3456"
          />
        </View>
      </Field>

      {error ? <Text style={styles.error}>{error}</Text> : null}

      <PrimaryButton label={loading ? "Sending…" : "Send verification code"} onPress={handleSubmit} disabled={loading || reg.phoneLocal.length < 7} />
      <LinkButton label="Back" onPress={() => setScreen("welcome")} />
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 24, fontWeight: "800", color: colors.slate900 },
  phoneRow: { flexDirection: "row", alignItems: "center", borderWidth: 1, borderColor: colors.border, borderRadius: 16, overflow: "hidden", backgroundColor: colors.surface },
  prefix: { paddingHorizontal: 14, fontWeight: "700", color: colors.slate500, backgroundColor: colors.surfaceMuted },
  phoneInput: { flex: 1, borderWidth: 0 },
  error: { color: colors.rose800, fontWeight: "600" },
});
