import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { LinearGradient } from "expo-linear-gradient";
import { Ionicons } from "@expo/vector-icons";
import { brand } from "../constants/brand";
import { PayflowLogo } from "./PayflowLogo";
import type { AgentStats } from "../types";
import { colors, fonts, radii, shadow } from "../theme";
import { formatMoney } from "../utils/format";

type StatCard = {
  id: string;
  label: string;
  value: string;
  sub: string;
  icon: keyof typeof Ionicons.glyphMap;
  kind: "primary" | "orange" | "emerald";
};

function buildCards(stats: AgentStats): StatCard[] {
  return [
    {
      id: "volume",
      label: "Today's volume",
      value: formatMoney(stats.todayVolume, "GMD"),
      sub: `${stats.completedToday} completed`,
      icon: "trending-up-outline",
      kind: "primary",
    },
    {
      id: "deposits",
      label: "Cash-in",
      value: String(stats.todayDeposits),
      sub: "Deposits today",
      icon: "arrow-down-circle-outline",
      kind: "emerald",
    },
    {
      id: "withdrawals",
      label: "Cash-out",
      value: String(stats.todayWithdrawals),
      sub: "Withdrawals today",
      icon: "arrow-up-circle-outline",
      kind: "orange",
    },
  ];
}

const gradients = {
  primary: ["#1a4fad", "#123c91", "#0d2f76"] as const,
  orange: ["#fb923c", "#f97316", "#ea580c"] as const,
  emerald: ["#059669", "#047857", "#065f46"] as const,
};

export function StatsCarousel({ stats }: { stats: AgentStats }) {
  const cards = buildCards(stats);

  return (
    <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.scroll}>
      {cards.map((card) => (
        <LinearGradient
          key={card.id}
          colors={[...gradients[card.kind]]}
          start={{ x: 0, y: 0 }}
          end={{ x: 1, y: 1 }}
          style={[styles.card, shadow.card]}
        >
          <View style={styles.cardTop}>
            <PayflowLogo size={28} />
            <View style={styles.iconWrap}>
              <Ionicons name={card.icon} size={18} color={colors.white} />
            </View>
          </View>
          <Text style={styles.cardLabel}>{card.label}</Text>
          <Text style={styles.cardValue}>{card.value}</Text>
          <Text style={styles.cardSub}>{card.sub}</Text>
          {stats.pendingCount > 0 && card.id === "volume" ? (
            <View style={styles.pendingPill}>
              <Text style={styles.pendingText}>{stats.pendingCount} pending</Text>
            </View>
          ) : null}
        </LinearGradient>
      ))}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  scroll: { gap: 12, paddingRight: 8 },
  card: {
    width: 280,
    minHeight: 160,
    borderRadius: radii.lg,
    padding: 20,
    justifyContent: "space-between",
  },
  cardTop: { flexDirection: "row", justifyContent: "space-between", alignItems: "center" },
  iconWrap: {
    width: 36,
    height: 36,
    borderRadius: radii.sm,
    backgroundColor: "rgba(255,255,255,0.15)",
    alignItems: "center",
    justifyContent: "center",
  },
  cardLabel: {
    fontSize: 12,
    fontFamily: fonts.medium,
    color: "rgba(255,255,255,0.75)",
    textTransform: "uppercase",
    letterSpacing: 0.8,
    marginTop: 16,
  },
  cardValue: { fontSize: 28, fontFamily: fonts.bold, color: colors.white, marginTop: 4 },
  cardSub: { fontSize: 13, fontFamily: fonts.regular, color: "rgba(255,255,255,0.85)", marginTop: 4 },
  pendingPill: {
    alignSelf: "flex-start",
    marginTop: 10,
    backgroundColor: brand.orange,
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: radii.pill,
  },
  pendingText: { fontSize: 11, fontFamily: fonts.bold, color: colors.white },
});
