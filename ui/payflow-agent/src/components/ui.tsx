import { Pressable, StyleSheet, Text, TextInput, View, type TextInputProps, type ViewStyle } from "react-native";
import { colors, fonts, radii, shadow } from "../theme";

export function Field({ label, children, hint }: { label: string; children: React.ReactNode; hint?: string }) {
  return (
    <View style={styles.field}>
      <Text style={styles.fieldLabel}>{label}</Text>
      {children}
      {hint ? <Text style={styles.fieldHint}>{hint}</Text> : null}
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
  disabled,
  style,
}: {
  label: string;
  onPress: () => void;
  color?: string;
  disabled?: boolean;
  style?: ViewStyle;
}) {
  return (
    <Pressable
      onPress={onPress}
      disabled={disabled}
      style={({ pressed }) => [
        styles.primaryButton,
        { backgroundColor: color, opacity: disabled ? 0.5 : pressed ? 0.92 : 1 },
        style,
      ]}
    >
      <Text style={styles.primaryButtonText}>{label}</Text>
    </Pressable>
  );
}

export function Card({ children, style }: { children: React.ReactNode; style?: ViewStyle }) {
  return <View style={[styles.card, shadow.card, style]}>{children}</View>;
}

export function FormError({ message }: { message?: string | null }) {
  if (!message) return null;
  return <Text style={styles.error}>{message}</Text>;
}

const styles = StyleSheet.create({
  field: { gap: 8 },
  fieldLabel: { fontSize: 13, fontFamily: fonts.bold, color: colors.slate700 },
  fieldHint: { fontSize: 12, fontFamily: fonts.regular, color: colors.slate500 },
  input: {
    height: 52,
    borderRadius: radii.md,
    backgroundColor: colors.surfaceMuted,
    paddingHorizontal: 16,
    fontSize: 16,
    fontFamily: fonts.regular,
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
  primaryButtonText: { fontSize: 16, fontFamily: fonts.bold, color: colors.white },
  card: {
    backgroundColor: colors.surface,
    borderRadius: radii.lg,
    padding: 16,
    borderWidth: 1,
    borderColor: colors.border,
  },
  error: { fontSize: 13, fontFamily: fonts.medium, color: colors.rose800, textAlign: "center" },
});
