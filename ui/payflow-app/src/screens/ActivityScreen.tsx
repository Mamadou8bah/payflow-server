import { useMemo, useState } from "react";
import { FlatList, ScrollView, StyleSheet, Text, View } from "react-native";
import { ScreenTitle } from "../components/AppHeader";
import { DonutChart } from "../components/DonutChart";
import { SegmentedControl } from "../components/SegmentedControl";
import { TransactionRow } from "../components/TransactionRow";
import { useWallet } from "../context/WalletContext";
import type { StatCategory } from "../types";
import { colors, spacing } from "../theme";

type StatMode = "expenses" | "income" | "history";
type Period = "day" | "week" | "month" | "6month";

const CHART_COLORS = ["#f97316", "#0ea5e9", "#22c55e", "#a855f7", "#eab308", "#64748b"];

function periodStart(period: Period): Date {
  const now = new Date();
  const start = new Date(now);
  if (period === "day") start.setHours(0, 0, 0, 0);
  else if (period === "week") start.setDate(now.getDate() - 7);
  else if (period === "month") start.setMonth(now.getMonth() - 1);
  else start.setMonth(now.getMonth() - 6);
  return start;
}

export function ActivityScreen() {
  const { data } = useWallet();
  const [mode, setMode] = useState<StatMode>("expenses");
  const [period, setPeriod] = useState<Period>("month");

  const categories = useMemo(() => {
    const start = periodStart(period);
    const isExpense = mode === "expenses";
    const buckets = new Map<string, number>();

    for (const txn of data.transactions) {
      const time = new Date(txn.time);
      if (Number.isNaN(time.getTime()) || time < start) continue;
      const expense = txn.type === "TRANSFER_OUT" || txn.type === "WITHDRAWAL";
      const income = txn.type === "TRANSFER_IN" || txn.type === "DEPOSIT";
      if (isExpense && !expense) continue;
      if (!isExpense && !income) continue;
      const label = txn.category || txn.type;
      buckets.set(label, (buckets.get(label) ?? 0) + txn.amount);
    }

    const entries = [...buckets.entries()].sort((a, b) => b[1] - a[1]);
    return entries.map(([label, amount], index): StatCategory => ({
      label,
      amount,
      color: CHART_COLORS[index % CHART_COLORS.length],
    }));
  }, [data.transactions, mode, period]);

  const total = useMemo(() => categories.reduce((sum, c) => sum + c.amount, 0), [categories]);
  const currency = data.wallets[0]?.currency ?? "GMD";

  if (mode === "history") {
    return (
      <View style={styles.flex}>
        <View style={styles.headerPad}>
          <ScreenTitle title="Transaction history" subtitle="Your recent wallet activity" />
          <SegmentedControl
            options={[
              { id: "expenses", label: "Expenses" },
              { id: "income", label: "Income" },
              { id: "history", label: "History" },
            ]}
            value={mode}
            onChange={setMode}
          />
        </View>
        <FlatList
          data={data.transactions}
          keyExtractor={(item) => item.id}
          contentContainerStyle={styles.list}
          ListEmptyComponent={<Text style={styles.empty}>No transactions yet</Text>}
          renderItem={({ item }) => <TransactionRow txn={item} />}
        />
      </View>
    );
  }

  return (
    <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
      <ScreenTitle title="Statistics" subtitle="Spending and income overview" />

      <SegmentedControl
        options={[
          { id: "expenses", label: "Expenses" },
          { id: "income", label: "Income" },
          { id: "history", label: "History" },
        ]}
        value={mode}
        onChange={setMode}
      />

      <SegmentedControl
        options={[
          { id: "day", label: "Day" },
          { id: "week", label: "Week" },
          { id: "month", label: "Month" },
          { id: "6month", label: "6 Month" },
        ]}
        value={period}
        onChange={setPeriod}
      />

      {categories.length === 0 ? (
        <Text style={styles.empty}>No {mode} in this period</Text>
      ) : (
        <DonutChart
          categories={categories}
          total={total}
          currency={currency}
          centerLabel={mode === "expenses" ? "Total spent" : "Total received"}
        />
      )}

      <Text style={styles.recentLabel}>Recent transactions</Text>
      {data.transactions.length === 0 ? (
        <Text style={styles.empty}>No transactions yet</Text>
      ) : (
        data.transactions.slice(0, 3).map((txn) => <TransactionRow key={txn.id} txn={txn} />)
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  flex: { flex: 1 },
  content: { padding: spacing.screen, paddingBottom: 32, gap: spacing.gap },
  headerPad: { padding: spacing.screen, paddingBottom: 0, gap: spacing.gap },
  list: { paddingHorizontal: spacing.screen, paddingBottom: 24 },
  recentLabel: { fontSize: 16, fontWeight: "700", color: colors.slate900, marginTop: 8 },
  empty: { fontSize: 14, color: colors.slate500, textAlign: "center", paddingVertical: 24 },
});
