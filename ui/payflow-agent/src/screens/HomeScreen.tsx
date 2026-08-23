import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { AppHeader } from "../components/AppHeader";
import { OperationRow } from "../components/OperationRow";
import { QuickActionGrid } from "../components/QuickActionGrid";
import { StatsCarousel } from "../components/StatsCarousel";
import { useAgent } from "../context/AgentContext";
import { colors, fonts, spacing } from "../theme";

export function HomeScreen() {
  const { stats, setTab, recentCompleted, openOperation } = useAgent();

  return (
    <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
      <AppHeader />

      <StatsCarousel stats={stats} />

      <QuickActionGrid
        actions={[
          { label: "Scan QR", icon: "qr-code-outline", color: colors.orange, onPress: () => setTab("scan") },
          {
            label: "Cash-in",
            icon: "arrow-down-circle-outline",
            color: colors.emerald700,
            onPress: () => setTab("new-deposit"),
          },
          {
            label: "Cash-out",
            icon: "arrow-up-circle-outline",
            color: colors.primary,
            onPress: () => setTab("new-withdrawal"),
          },
          { label: "Queue", icon: "time-outline", color: colors.primaryMid, onPress: () => setTab("queue") },
        ]}
      />

      <View style={styles.sectionHeader}>
        <Text style={styles.sectionTitle}>Recent completions</Text>
        <Pressable onPress={() => setTab("activity")}>
          <Text style={styles.seeAll}>See all</Text>
        </Pressable>
      </View>

      {recentCompleted.map((op) => (
        <OperationRow key={op.reference} op={op} onPress={() => openOperation(op)} />
      ))}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  content: { padding: spacing.screen, paddingBottom: 32, gap: spacing.gap },
  sectionHeader: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginTop: 4 },
  sectionTitle: { fontSize: 18, fontFamily: fonts.bold, color: colors.slate900 },
  seeAll: { fontSize: 14, fontFamily: fonts.medium, color: colors.primary },
});
