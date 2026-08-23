import { Pressable, StyleSheet, Text, View } from "react-native";
import { colors, fonts, radii } from "../theme";
import type { AgentOperation } from "../types";
import { formatMoney, formatTimestamp, operationLabel, statusLabel } from "../utils/format";

export function OperationRow({ op, onPress }: { op: AgentOperation; onPress?: () => void }) {
  const isDeposit = op.operation === "DEPOSIT";
  const status = op.status;
  const statusColor =
    status === "COMPLETED"
      ? colors.emerald700
      : status === "AWAITING_AGENT"
        ? colors.amber700
        : colors.slate500;

  return (
    <Pressable onPress={onPress} style={({ pressed }) => [styles.row, pressed && styles.pressed]}>
      <View style={[styles.badge, isDeposit ? styles.depositBadge : styles.withdrawBadge]}>
        <Text style={styles.badgeText}>{isDeposit ? "IN" : "OUT"}</Text>
      </View>
      <View style={styles.body}>
        <Text style={styles.name} numberOfLines={1}>
          {op.merchantName}
        </Text>
        <Text style={styles.meta}>
          {operationLabel(op.operation)} · {formatTimestamp(op.createdAt)}
        </Text>
        <Text style={[styles.status, { color: statusColor }]}>{statusLabel(status)}</Text>
      </View>
      <View style={styles.right}>
        <Text style={styles.amount}>{formatMoney(op.amount, op.currency)}</Text>
        <Text style={styles.ref}>{op.reference}</Text>
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
    gap: 12,
  },
  pressed: { opacity: 0.88 },
  badge: {
    width: 40,
    height: 40,
    borderRadius: radii.sm,
    alignItems: "center",
    justifyContent: "center",
  },
  depositBadge: { backgroundColor: colors.emerald100 },
  withdrawBadge: { backgroundColor: colors.orangeLight },
  badgeText: { fontSize: 11, fontFamily: fonts.bold, color: colors.slate800 },
  body: { flex: 1, minWidth: 0 },
  name: { fontSize: 15, fontFamily: fonts.medium, color: colors.slate900 },
  meta: { fontSize: 12, fontFamily: fonts.regular, color: colors.slate500, marginTop: 2 },
  status: { fontSize: 11, fontFamily: fonts.medium, marginTop: 4 },
  right: { alignItems: "flex-end" },
  amount: { fontSize: 15, fontFamily: fonts.bold, color: colors.slate900 },
  ref: { fontSize: 10, fontFamily: fonts.regular, color: colors.slate400, marginTop: 2 },
});
