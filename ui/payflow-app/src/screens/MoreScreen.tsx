import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { ScreenTitle } from "../components/AppHeader";
import { useWallet } from "../context/WalletContext";
import { colors, radii, shadow, spacing } from "../theme";

type MenuItem = {
  label: string;
  subtitle: string;
  icon: keyof typeof Ionicons.glyphMap;
  tab: "profile" | "support" | "topup" | "withdraw" | "wallets";
};

const menuItems: MenuItem[] = [
  { label: "Personal account", subtitle: "Profile, settings, security", icon: "person-outline", tab: "profile" },
  { label: "Support chat", subtitle: "Talk to Payflow support", icon: "chatbubbles-outline", tab: "support" },
  { label: "Replenish wallet", subtitle: "Mobile money or agent", icon: "add-circle-outline", tab: "topup" },
  { label: "Withdraw cash", subtitle: "Agent-assisted cash-out", icon: "cash-outline", tab: "withdraw" },
  { label: "My wallets", subtitle: "Manage your wallets", icon: "wallet-outline", tab: "wallets" },
];

export function MoreScreen() {
  const { data, setTab } = useWallet();

  return (
    <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
      <ScreenTitle title="More" subtitle="Account and services" />

      <View style={styles.profileCard}>
        <View style={styles.profileInfo}>
          <Text style={styles.name}>{data.session.name}</Text>
          <Text style={styles.phone}>{data.session.phone}</Text>
        </View>
        <Pressable onPress={() => setTab("profile")}>
          <Text style={styles.editLink}>Edit</Text>
        </Pressable>
      </View>

      {menuItems.map((item) => (
        <Pressable key={item.label} onPress={() => setTab(item.tab)} style={[styles.menuRow, shadow.card]}>
          <View style={styles.menuIcon}>
            <Ionicons name={item.icon} size={22} color={colors.primary} />
          </View>
          <View style={styles.menuBody}>
            <Text style={styles.menuLabel}>{item.label}</Text>
            <Text style={styles.menuSub}>{item.subtitle}</Text>
          </View>
          <Ionicons name="chevron-forward" size={20} color={colors.slate400} />
        </Pressable>
      ))}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  content: { padding: spacing.screen, paddingBottom: 32 },
  profileCard: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    backgroundColor: colors.surface,
    borderRadius: radii.lg,
    padding: 16,
    marginBottom: 20,
    borderWidth: 1,
    borderColor: colors.border,
  },
  profileInfo: { flex: 1 },
  name: { fontSize: 17, fontWeight: "800", color: colors.slate900 },
  phone: { fontSize: 13, color: colors.slate500, marginTop: 2 },
  editLink: { fontSize: 14, fontWeight: "700", color: colors.primary },
  menuRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 14,
    backgroundColor: colors.surface,
    borderRadius: radii.md,
    padding: 16,
    marginBottom: 10,
    borderWidth: 1,
    borderColor: colors.border,
  },
  menuIcon: {
    width: 44,
    height: 44,
    borderRadius: radii.sm,
    backgroundColor: colors.blue100,
    alignItems: "center",
    justifyContent: "center",
  },
  menuBody: { flex: 1 },
  menuLabel: { fontSize: 15, fontWeight: "700", color: colors.slate900 },
  menuSub: { fontSize: 12, color: colors.slate500, marginTop: 2 },
});
