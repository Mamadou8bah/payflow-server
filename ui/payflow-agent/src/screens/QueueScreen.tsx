import { ScrollView, StyleSheet, Text, View } from "react-native";
import { ScreenTitle } from "../components/AppHeader";
import { OperationRow } from "../components/OperationRow";
import { useAgent } from "../context/AgentContext";
import { colors, fonts, spacing } from "../theme";

export function QueueScreen() {
  const { pending, openOperation } = useAgent();

  return (
    <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
      <ScreenTitle
        title="Pending queue"
        subtitle={`${pending.length} operation${pending.length === 1 ? "" : "s"} awaiting completion`}
      />

      {pending.length === 0 ? (
        <View style={styles.empty}>
          <Text style={styles.emptyTitle}>Queue is clear</Text>
          <Text style={styles.emptySub}>New customer QR codes will appear here when scanned.</Text>
        </View>
      ) : (
        pending.map((op) => <OperationRow key={op.reference} op={op} onPress={() => openOperation(op)} />)
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  content: { padding: spacing.screen, paddingBottom: 120 },
  empty: {
    marginTop: 40,
    alignItems: "center",
    padding: 24,
    backgroundColor: colors.surface,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: colors.border,
  },
  emptyTitle: { fontSize: 18, fontFamily: fonts.bold, color: colors.slate900 },
  emptySub: { fontSize: 14, fontFamily: fonts.regular, color: colors.slate500, marginTop: 8, textAlign: "center" },
});
