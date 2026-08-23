import { useState } from "react";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { merchantRegistrationApi } from "../../api/merchantRegistration";
import { AppInput, Field, LinkButton, PrimaryButton, Screen } from "../../components/ui";
import { StepIndicator } from "../../components/StepIndicator";
import { ID_TYPES } from "../../constants/registration";
import { useApp } from "../../context/AppContext";
import { colors, radii } from "../../theme";

export function OwnerScreen() {
  const { reg, updateReg, setScreen, setError, error } = useApp();
  const [loading, setLoading] = useState(false);
  const owner = reg.owner;

  async function handleSubmit() {
    setError("");
    setLoading(true);
    try {
      await merchantRegistrationApi.saveOwner({
        registrationToken: reg.token,
        firstName: owner.firstName,
        lastName: owner.lastName,
        ownerIdType: owner.ownerIdType,
        ownerIdNumber: owner.ownerIdNumber,
      });
      setScreen("reg-account");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not save owner");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Screen>
      <Text style={styles.title}>Owner identity</Text>
      <StepIndicator current={4} />

      <Field label="First name">
        <AppInput value={owner.firstName} onChangeText={(v) => updateReg({ owner: { ...owner, firstName: v } })} />
      </Field>
      <Field label="Last name">
        <AppInput value={owner.lastName} onChangeText={(v) => updateReg({ owner: { ...owner, lastName: v } })} />
      </Field>
      <Field label="ID type">
        <View style={styles.options}>
          {ID_TYPES.map((opt) => {
            const active = opt.value === owner.ownerIdType;
            return (
              <Pressable key={opt.value} onPress={() => updateReg({ owner: { ...owner, ownerIdType: opt.value } })} style={[styles.option, active && styles.optionActive]}>
                <Text style={[styles.optionText, active && styles.optionTextActive]}>{opt.label}</Text>
              </Pressable>
            );
          })}
        </View>
      </Field>
      <Field label="ID number">
        <AppInput value={owner.ownerIdNumber} onChangeText={(v) => updateReg({ owner: { ...owner, ownerIdNumber: v } })} />
      </Field>

      {error ? <Text style={styles.error}>{error}</Text> : null}

      <PrimaryButton label={loading ? "Saving…" : "Continue"} onPress={handleSubmit} disabled={loading} />
      <LinkButton label="Back" onPress={() => setScreen("reg-business")} />
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: { fontSize: 24, fontWeight: "800", color: colors.slate900 },
  error: { color: colors.rose800, fontWeight: "600" },
  options: { gap: 8 },
  option: { borderWidth: 1, borderColor: colors.border, borderRadius: radii.md, padding: 12, backgroundColor: colors.surface },
  optionActive: { borderColor: colors.primary, backgroundColor: colors.blue100 },
  optionText: { fontSize: 14, fontWeight: "600", color: colors.slate700 },
  optionTextActive: { color: colors.primary },
});
