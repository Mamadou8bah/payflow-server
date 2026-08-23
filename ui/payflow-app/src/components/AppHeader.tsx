import { Pressable, StyleSheet, Text, View } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useWallet } from "../context/WalletContext";
import { colors, radii } from "../theme";
import { getGreeting } from "../utils/format";

export function AppHeader() {
  const { data, setTab } = useWallet();

  return (
    <View style={styles.row}>
      <View style={styles.greeting}>
        <Text style={styles.greetingLabel}>{getGreeting()}</Text>
        <Text style={styles.greetingName}>{data.session.name}</Text>
      </View>

      <Pressable style={styles.iconBtn} onPress={() => setTab("support")}>
        <Ionicons name="chatbubble-outline" size={20} color={colors.slate700} />
      </Pressable>
      <Pressable style={styles.iconBtn}>
        <Ionicons name="notifications-outline" size={20} color={colors.slate700} />
        {data.pendingCount > 0 ? (
          <View style={styles.badge}>
            <Text style={styles.badgeText}>{data.pendingCount}</Text>
          </View>
        ) : null}
      </Pressable>
    </View>
  );
}

export function ScreenTitle({ title, subtitle }: { title: string; subtitle?: string }) {
  return (
    <View style={styles.titleWrap}>
      <Text style={styles.title}>{title}</Text>
      {subtitle ? <Text style={styles.subtitle}>{subtitle}</Text> : null}
    </View>
  );
}

export function BackHeader({ title, onBack }: { title: string; onBack: () => void }) {
  return (
    <View style={styles.backRow}>
      <Pressable onPress={onBack} style={styles.backBtn}>
        <Ionicons name="chevron-back" size={22} color={colors.primary} />
      </Pressable>
      <Text style={styles.backTitle}>{title}</Text>
      <View style={styles.backSpacer} />
    </View>
  );
}

const styles = StyleSheet.create({
  row: { flexDirection: "row", alignItems: "center", gap: 10 },
  greeting: { flex: 1 },
  greetingLabel: { fontSize: 13, fontWeight: "600", color: colors.slate500 },
  greetingName: { fontSize: 18, fontWeight: "800", color: colors.slate900, marginTop: 2 },
  iconBtn: {
    width: 40,
    height: 40,
    borderRadius: radii.md,
    backgroundColor: colors.surface,
    alignItems: "center",
    justifyContent: "center",
    borderWidth: 1,
    borderColor: colors.border,
  },
  badge: {
    position: "absolute",
    top: 2,
    right: 2,
    minWidth: 16,
    height: 16,
    borderRadius: 8,
    backgroundColor: colors.rose500,
    alignItems: "center",
    justifyContent: "center",
    paddingHorizontal: 3,
  },
  badgeText: { color: colors.white, fontSize: 9, fontWeight: "800" },
  titleWrap: { marginBottom: 4 },
  title: { fontSize: 24, fontWeight: "800", color: colors.slate900 },
  subtitle: { fontSize: 14, color: colors.slate500, marginTop: 4 },
  backRow: { flexDirection: "row", alignItems: "center", marginBottom: 8 },
  backBtn: { width: 36, height: 36, alignItems: "center", justifyContent: "center" },
  backTitle: { flex: 1, textAlign: "center", fontSize: 17, fontWeight: "700", color: colors.slate900 },
  backSpacer: { width: 36 },
});
