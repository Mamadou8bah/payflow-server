import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { AppHeader } from "../components/AppHeader";
import { QuickActionGrid } from "../components/QuickActionGrid";
import { TransactionRow } from "../components/TransactionRow";
import { WalletCarousel } from "../components/WalletCarousel";
import { useWallet } from "../context/WalletContext";
import { colors, spacing } from "../theme";

export function HomeScreen() {
  const { data, setTab, recentTransactions, selectedWalletId } = useWallet();

  return (
    <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
      <AppHeader />

      <WalletCarousel wallets={data.wallets} selectedId={selectedWalletId} />

      <QuickActionGrid
        actions={[
          { label: "Replenish", icon: "add-circle-outline", color: colors.orange, onPress: () => setTab("topup") },
          { label: "Transfer", icon: "swap-horizontal-outline", color: colors.primary, onPress: () => setTab("send") },
          { label: "Withdraw", icon: "cash-outline", color: colors.primaryMid, onPress: () => setTab("withdraw") },
          { label: "Analytics", icon: "pie-chart-outline", color: colors.emerald700, onPress: () => setTab("activity") },
        ]}
      />

      <View style={styles.sectionHeader}>
        <Text style={styles.sectionTitle}>All transactions</Text>
        <Pressable onPress={() => setTab("activity")}>
          <Text style={styles.seeAll}>See all</Text>
        </Pressable>
      </View>

      <View>
        {recentTransactions.map((txn) => (
          <TransactionRow key={txn.id} txn={txn} />
        ))}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  content: { padding: spacing.screen, paddingBottom: 32, gap: spacing.gap },
  sectionHeader: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginTop: 4 },
  sectionTitle: { fontSize: 18, fontWeight: "800", color: colors.slate900 },
  seeAll: { fontSize: 14, fontWeight: "600", color: colors.primary },
});
