import { ScrollView, StyleSheet } from "react-native";
import { ScreenTitle } from "../components/AppHeader";
import { OperationRow } from "../components/OperationRow";
import { useAgent } from "../context/AgentContext";
import { spacing } from "../theme";

export function ActivityScreen() {
  const { completed, openOperation } = useAgent();

  return (
    <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
      <ScreenTitle title="Activity" subtitle="Operations you completed" />

      {completed.map((op) => (
        <OperationRow key={op.reference} op={op} onPress={() => openOperation(op)} />
      ))}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  content: { padding: spacing.screen, paddingBottom: 120 },
});
