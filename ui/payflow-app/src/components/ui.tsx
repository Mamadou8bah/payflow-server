import { Pressable, ScrollView, StyleSheet, Text, TextInput, View, type TextInputProps, type ViewStyle } from "react-native";
import { colors, radii, shadow } from "../theme";

export function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <View style={styles.field}>
      <Text style={styles.fieldLabel}>{label}</Text>
      {children}
    </View>
  );
}

export function AppInput(props: TextInputProps) {
  return <TextInput {...props} placeholderTextColor={colors.slate400} style={[styles.input, props.style]} />;
}

export function PrimaryButton({
  label,
  onPress,
  color = colors.primary,
  textColor = colors.white,
  style,
}: {
  label: string;
  onPress: () => void;
  color?: string;
  textColor?: string;
  style?: ViewStyle;
}) {
  return (
    <Pressable onPress={onPress} style={({ pressed }) => [styles.primaryButton, { backgroundColor: color, opacity: pressed ? 0.9 : 1 }, style]}>
      <Text style={[styles.primaryButtonText, { color: textColor }]}>{label}</Text>
    </Pressable>
  );
}

export function Card({ children, style }: { children: React.ReactNode; style?: ViewStyle }) {
  return <View style={[styles.card, shadow.card, style]}>{children}</View>;
}

export function AmountDisplay({ amount, onChange }: { amount: string; onChange: (v: string) => void }) {
  return (
    <View style={styles.amountWrap}>
      <Text style={styles.amountCurrency}>GMD</Text>
      <TextInput
        value={amount}
        onChangeText={onChange}
        keyboardType="decimal-pad"
        placeholder="0.00"
        placeholderTextColor={colors.slate300}
        style={styles.amountInput}
      />
    </View>
  );
}

export function MethodRow({
  label,
  subtitle,
  onPress,
  icon,
}: {
  label: string;
  subtitle?: string;
  onPress: () => void;
  icon?: string;
}) {
  return (
    <Pressable onPress={onPress} style={styles.methodRow}>
      <View style={styles.methodIcon}>
        <Text style={styles.methodIconText}>{icon ?? "›"}</Text>
      </View>
      <View style={styles.methodBody}>
        <Text style={styles.methodLabel}>{label}</Text>
        {subtitle ? <Text style={styles.methodSub}>{subtitle}</Text> : null}
      </View>
      <Text style={styles.methodChevron}>›</Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  field: { gap: 8 },
  fieldLabel: { fontSize: 13, fontWeight: "600", color: colors.slate500 },
  input: {
    height: 52,
    borderRadius: radii.md,
    backgroundColor: colors.surfaceMuted,
    paddingHorizontal: 16,
    fontSize: 16,
    fontWeight: "500",
    color: colors.slate900,
    borderWidth: 1,
    borderColor: colors.border,
  },
  primaryButton: {
    height: 52,
    borderRadius: radii.pill,
    alignItems: "center",
    justifyContent: "center",
  },
  primaryButtonText: { fontSize: 16, fontWeight: "700" },
  card: {
    backgroundColor: colors.surface,
    borderRadius: radii.lg,
    padding: 20,
    borderWidth: 1,
    borderColor: colors.border,
  },
  amountWrap: {
    alignItems: "center",
    paddingVertical: 24,
    backgroundColor: colors.surfaceMuted,
    borderRadius: radii.lg,
    marginVertical: 16,
  },
  amountCurrency: { fontSize: 14, fontWeight: "600", color: colors.slate500 },
  amountInput: {
    fontSize: 40,
    fontWeight: "800",
    color: colors.slate900,
    marginTop: 4,
    minWidth: 120,
    textAlign: "center",
  },
  methodRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 14,
    paddingVertical: 16,
    borderBottomWidth: 1,
    borderBottomColor: colors.border,
  },
  methodIcon: {
    width: 44,
    height: 44,
    borderRadius: radii.sm,
    backgroundColor: colors.blue100,
    alignItems: "center",
    justifyContent: "center",
  },
  methodIconText: { fontSize: 18, fontWeight: "700", color: colors.primary },
  methodBody: { flex: 1 },
  methodLabel: { fontSize: 15, fontWeight: "600", color: colors.slate900 },
  methodSub: { fontSize: 12, color: colors.slate500, marginTop: 2 },
  methodChevron: { fontSize: 22, color: colors.slate400 },
});
