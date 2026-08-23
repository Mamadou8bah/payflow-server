import { useState } from "react";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { merchantRegistrationApi } from "../../api/merchantRegistration";
import { AppInput, Field, LinkButton, PrimaryButton, Screen } from "../../components/ui";
import { StepIndicator } from "../../components/StepIndicator";
import { BUSINESS_CATEGORIES, GAMBIA_REGIONS } from "../../constants/registration";
import { useApp } from "../../context/AppContext";
import { colors, radii } from "../../theme";

function OptionList({ options, value, onChange }: { options: readonly { value: string; label: string }[]; value: string; onChange: (v: string) => void }) {
  return (
    <View style={styles.options}>
      {options.map((opt) => {
        const active = opt.value === value;
        return (
          <Pressable key={opt.value} onPress={() => onChange(opt.value)} style={[styles.option, active && styles.optionActive]}>
            <Text style={[styles.optionText, active && styles.optionTextActive]}>{opt.label}</Text>
          </Pressable>
        );
      })}
    </View>
  );
}

export function BusinessScreen() {
  const { reg, updateReg, setScreen, setError, error } = useApp();
  const [loading, setLoading] = useState(false);
  const business = reg.business;

  async function handleSubmit() {
    setError("");
    setLoading(true);
    try {
      await merchantRegistrationApi.saveBusiness({
        registrationToken: reg.token,
        businessName: business.businessName,
        tradingName: business.tradingName || undefined,
        category: business.category,
        region: business.region,
        cityOrArea: business.cityOrArea,
        streetAddress: business.streetAddress,
        businessRegistrationNumber: business.businessRegistrationNumber || undefined,
      });
      setScreen("reg-owner");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not save business");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Screen>
      <Text style={styles.title}>Business details</Text>
      <StepIndicator current={3} />

      <Field label="Business name">
        <AppInput value={business.businessName} onChangeText={(v) => updateReg({ business: { ...business, businessName: v } })} placeholder="Acme Shop" />
      </Field>
      <Field label="Category">
        <OptionList options={BUSINESS_CATEGORIES} value={business.category} onChange={(v) => updateReg({ business: { ...business, category: v } })} />
      </Field>
      <Field label="Region">
        <OptionList options={GAMBIA_REGIONS} value={business.region} onChange={(v) => updateReg({ business: { ...business, region: v } })} />
      </Field>
      <Field label="City / area">
        <AppInput value={business.cityOrArea} onChangeText={(v) => updateReg({ business: { ...business, cityOrArea: v } })} />
      </Field>
      <Field label="Street address">
        <AppInput value={business.streetAddress} onChangeText={(v) => updateReg({ business: { ...business, streetAddress: v } })} />
      </Field>

      {error ? <Text style={styles.error}>{error}</Text> : null}

      <PrimaryButton label={loading ? "Saving…" : "Continue"} onPress={handleSubmit} disabled={loading} />
      <LinkButton label="Back" onPress={() => setScreen("reg-otp")} />
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
