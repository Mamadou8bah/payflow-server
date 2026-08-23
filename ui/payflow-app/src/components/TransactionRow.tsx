import { Pressable, StyleSheet, Text, View } from "react-native";
import { useWallet } from "../context/WalletContext";
import { colors, fonts, radii } from "../theme";
import type { CustomerTransaction } from "../types";
import { formatMoney, formatTimestamp } from "../utils/format";

export function TransactionRow({
  txn,
  onPress,
}: {
  txn: CustomerTransaction;
  onPress?: () => void;
}) {
  const { openTransaction } = useWallet();
  const isCredit = txn.type === "TRANSFER_IN" || txn.type === "DEPOSIT";
  const sign = isCredit ? "+" : "−";

  return (
    <Pressable
      onPress={onPress ?? (() => openTransaction(txn.id))}
      style={({ pressed }) => [styles.row, pressed && styles.rowPressed]}
    >
      <View style={styles.body}>
        <Text style={styles.name} numberOfLines={1}>
          {txn.counterparty}
        </Text>
        <Text style={styles.meta}>
          {txn.method} · {formatTimestamp(txn.time)}
        </Text>
      </View>
      <View style={styles.right}>
        <Text style={styles.amount}>
          {sign}
          {formatMoney(txn.amount, txn.currency)}
        </Text>
        <Text style={styles.method}>{txn.category ?? txn.method}</Text>
      </View>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: colors.surface,
    borderRadius: radii.md,
    padding: 14,
    marginBottom: 10,
    borderWidth: 1,
    borderColor: colors.border,
  },
  rowPressed: { opacity: 0.88 },
  body: { flex: 1, minWidth: 0, paddingRight: 12 },
  name: { fontSize: 15, fontFamily: fonts.medium, color: colors.slate900 },
  meta: { fontSize: 12, fontFamily: fonts.regular, color: colors.slate500, marginTop: 2 },
  right: { alignItems: "flex-end" },
  amount: { fontSize: 15, fontFamily: fonts.bold, color: colors.slate900 },
  method: { fontSize: 11, fontFamily: fonts.regular, color: colors.slate500, marginTop: 2 },
});
