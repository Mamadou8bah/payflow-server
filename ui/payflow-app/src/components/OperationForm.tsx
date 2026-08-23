import { Pressable, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { brand } from "../constants/brand";
import { colors, fonts, radii, shadow, spacing } from "../theme";
import type { CustomerWallet, QuickContact } from "../types";
import { formatBalance } from "../utils/format";

export function OperationScreen({
  title,
  subtitle,
  children,
}: {
  title: string;
  subtitle?: string;
  children: React.ReactNode;
}) {
  return (
    <ScrollView
      contentContainerStyle={styles.scroll}
      showsVerticalScrollIndicator={false}
      keyboardShouldPersistTaps="handled"
    >
      <Text style={styles.title}>{title}</Text>
      {subtitle ? <Text style={styles.subtitle}>{subtitle}</Text> : null}
      {children}
    </ScrollView>
  );
}

export function BalanceBanner({
  wallet,
  hidden,
  label = "Available balance",
}: {
  wallet: CustomerWallet;
  hidden: boolean;
  label?: string;
}) {
  return (
    <View style={[styles.banner, shadow.card]}>
      <View>
        <Text style={styles.bannerLabel}>{label}</Text>
        <Text style={styles.bannerWallet}>{wallet.name}</Text>
      </View>
      <Text style={styles.bannerAmount}>{formatBalance(wallet.balance, wallet.currency, hidden)}</Text>
    </View>
  );
}

export function WalletPicker({
  wallets,
  selectedId,
  onSelect,
  hidden,
}: {
  wallets: CustomerWallet[];
  selectedId: number;
  onSelect: (id: number) => void;
  hidden: boolean;
}) {
  return (
    <View style={styles.section}>
      <Text style={styles.sectionLabel}>Wallet</Text>
      <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.chips}>
        {wallets.map((w) => {
          const active = w.id === selectedId;
          return (
            <Pressable
              key={w.id}
              onPress={() => onSelect(w.id)}
              style={[styles.chip, active && styles.chipActive]}
            >
              <Text style={[styles.chipName, active && styles.chipNameActive]}>{w.name}</Text>
              <Text style={[styles.chipBal, active && styles.chipBalActive]}>
                {formatBalance(w.balance, w.currency, hidden)}
              </Text>
            </Pressable>
          );
        })}
      </ScrollView>
    </View>
  );
}

export function AmountHero({
  amount,
  onChange,
  currency,
}: {
  amount: string;
  onChange: (v: string) => void;
  currency: string;
}) {
  return (
    <View style={[styles.amountCard, shadow.card]}>
      <Text style={styles.amountLabel}>Amount</Text>
      <View style={styles.amountRow}>
        <Text style={styles.amountCurrency}>{currency}</Text>
        <TextInput
          value={amount}
          onChangeText={onChange}
          keyboardType="decimal-pad"
          placeholder="0.00"
          placeholderTextColor={colors.slate300}
          style={styles.amountInput}
        />
      </View>
    </View>
  );
}

export function ContactChips({
  contacts,
  selected,
  onSelect,
}: {
  contacts: QuickContact[];
  selected: string;
  onSelect: (phone: string) => void;
}) {
  return (
    <View style={styles.section}>
      <Text style={styles.sectionLabel}>Quick send</Text>
      <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.chips}>
        {contacts.map((c) => {
          const active = selected === c.phone;
          return (
            <Pressable
              key={c.id}
              onPress={() => onSelect(c.phone)}
              style={[styles.contactChip, active && styles.contactChipActive]}
            >
              <Text style={[styles.contactName, active && styles.contactNameActive]}>{c.name}</Text>
              <Text style={[styles.contactPhone, active && styles.contactPhoneActive]}>{c.phone}</Text>
            </Pressable>
          );
        })}
      </ScrollView>
    </View>
  );
}

export function MethodTabs<T extends string>({
  options,
  value,
  onChange,
}: {
  options: { id: T; label: string }[];
  value: T;
  onChange: (v: T) => void;
}) {
  return (
    <View style={styles.methodTabs}>
      {options.map((opt) => {
        const active = opt.id === value;
        return (
          <Pressable
            key={opt.id}
            onPress={() => onChange(opt.id)}
            style={[styles.methodTab, active && styles.methodTabActive]}
          >
            <Text style={[styles.methodTabText, active && styles.methodTabTextActive]}>{opt.label}</Text>
          </Pressable>
        );
      })}
    </View>
  );
}

export function FormField({
  label,
  children,
}: {
  label: string;
  children: React.ReactNode;
}) {
  return (
    <View style={styles.field}>
      <Text style={styles.fieldLabel}>{label}</Text>
      {children}
    </View>
  );
}

export function FormInput(props: React.ComponentProps<typeof TextInput>) {
  return (
    <TextInput
      {...props}
      placeholderTextColor={colors.slate400}
      style={[styles.input, props.style]}
    />
  );
}

export function FormError({ message }: { message?: string | null }) {
  if (!message) return null;
  return <Text style={styles.error}>{message}</Text>;
}

export function SubmitButton({
  label,
  onPress,
  color = brand.orange,
}: {
  label: string;
  onPress: () => void;
  color?: string;
}) {
  return (
    <Pressable onPress={onPress} style={({ pressed }) => [styles.submit, { backgroundColor: color, opacity: pressed ? 0.92 : 1 }]}>
      <Text style={styles.submitText}>{label}</Text>
    </Pressable>
  );
}

export function OptionList({
  items,
}: {
  items: { label: string; subtitle?: string; icon: keyof typeof Ionicons.glyphMap; onPress: () => void }[];
}) {
  return (
    <View style={[styles.optionList, shadow.card]}>
      {items.map((item, index) => (
        <Pressable
          key={item.label}
          onPress={item.onPress}
          style={[styles.optionRow, index < items.length - 1 && styles.optionBorder]}
        >
          <View style={styles.optionIcon}>
            <Ionicons name={item.icon} size={20} color={colors.primary} />
          </View>
          <View style={styles.optionBody}>
            <Text style={styles.optionLabel}>{item.label}</Text>
            {item.subtitle ? <Text style={styles.optionSub}>{item.subtitle}</Text> : null}
          </View>
          <Ionicons name="chevron-forward" size={18} color={colors.slate400} />
        </Pressable>
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  scroll: { padding: spacing.screen, paddingBottom: 120, gap: spacing.gap },
  title: { fontSize: 26, fontFamily: fonts.bold, color: colors.slate900 },
  subtitle: { fontSize: 14, fontFamily: fonts.regular, color: colors.slate500, marginTop: 4 },
  banner: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    backgroundColor: colors.white,
    borderRadius: radii.lg,
    padding: 16,
    borderWidth: 1,
    borderColor: colors.border,
  },
  bannerLabel: { fontSize: 12, fontFamily: fonts.regular, color: colors.slate500 },
  bannerWallet: { fontSize: 15, fontFamily: fonts.bold, color: colors.slate900, marginTop: 2 },
  bannerAmount: { fontSize: 18, fontFamily: fonts.bold, color: brand.orange },
  section: { gap: 10 },
  sectionLabel: { fontSize: 13, fontFamily: fonts.bold, color: colors.slate700, textTransform: "uppercase", letterSpacing: 0.6 },
  chips: { gap: 10, paddingRight: 8 },
  chip: {
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderRadius: radii.lg,
    backgroundColor: colors.white,
    borderWidth: 1,
    borderColor: colors.border,
    minWidth: 130,
  },
  chipActive: { backgroundColor: colors.primary, borderColor: colors.primary },
  chipName: { fontSize: 14, fontFamily: fonts.bold, color: colors.slate900 },
  chipNameActive: { color: colors.white },
  chipBal: { fontSize: 12, fontFamily: fonts.regular, color: colors.slate500, marginTop: 4 },
  chipBalActive: { color: "rgba(255,255,255,0.8)" },
  amountCard: {
    backgroundColor: colors.white,
    borderRadius: radii.lg,
    padding: 20,
    borderWidth: 1,
    borderColor: colors.border,
  },
  amountLabel: { fontSize: 13, fontFamily: fonts.bold, color: colors.slate500, textTransform: "uppercase", letterSpacing: 0.6 },
  amountRow: { flexDirection: "row", alignItems: "center", justifyContent: "center", gap: 8, marginTop: 12 },
  amountCurrency: { fontSize: 22, fontFamily: fonts.bold, color: colors.slate500 },
  amountInput: {
    fontSize: 40,
    fontFamily: fonts.black,
    color: colors.slate900,
    minWidth: 140,
    textAlign: "left",
    padding: 0,
  },
  contactChip: {
    paddingHorizontal: 14,
    paddingVertical: 12,
    borderRadius: radii.lg,
    backgroundColor: colors.white,
    borderWidth: 1,
    borderColor: colors.border,
    marginRight: 4,
  },
  contactChipActive: { borderColor: brand.orange, backgroundColor: brand.orange + "12" },
  contactName: { fontSize: 14, fontFamily: fonts.bold, color: colors.slate900 },
  contactNameActive: { color: brand.orange },
  contactPhone: { fontSize: 11, fontFamily: fonts.regular, color: colors.slate500, marginTop: 2 },
  contactPhoneActive: { color: colors.slate700 },
  methodTabs: {
    flexDirection: "row",
    backgroundColor: colors.white,
    borderRadius: radii.pill,
    padding: 4,
    borderWidth: 1,
    borderColor: colors.border,
  },
  methodTab: { flex: 1, paddingVertical: 10, alignItems: "center", borderRadius: radii.pill },
  methodTabActive: { backgroundColor: brand.orange },
  methodTabText: { fontSize: 13, fontFamily: fonts.medium, color: colors.slate500 },
  methodTabTextActive: { color: colors.white, fontFamily: fonts.bold },
  field: { gap: 8 },
  fieldLabel: { fontSize: 13, fontFamily: fonts.bold, color: colors.slate700 },
  input: {
    height: 52,
    borderRadius: radii.md,
    backgroundColor: colors.white,
    paddingHorizontal: 16,
    fontSize: 16,
    fontFamily: fonts.regular,
    color: colors.slate900,
    borderWidth: 1,
    borderColor: colors.border,
  },
  error: { fontSize: 13, fontFamily: fonts.medium, color: colors.rose800, textAlign: "center" },
  submit: {
    height: 52,
    borderRadius: radii.pill,
    alignItems: "center",
    justifyContent: "center",
    marginTop: 4,
  },
  submitText: { fontSize: 16, fontFamily: fonts.bold, color: colors.white },
  optionList: {
    backgroundColor: colors.white,
    borderRadius: radii.lg,
    borderWidth: 1,
    borderColor: colors.border,
    overflow: "hidden",
  },
  optionRow: { flexDirection: "row", alignItems: "center", gap: 12, padding: 16 },
  optionBorder: { borderBottomWidth: 1, borderBottomColor: colors.border },
  optionIcon: {
    width: 40,
    height: 40,
    borderRadius: radii.sm,
    backgroundColor: colors.blue100,
    alignItems: "center",
    justifyContent: "center",
  },
  optionBody: { flex: 1 },
  optionLabel: { fontSize: 15, fontFamily: fonts.bold, color: colors.slate900 },
  optionSub: { fontSize: 12, fontFamily: fonts.regular, color: colors.slate500, marginTop: 2 },
});
