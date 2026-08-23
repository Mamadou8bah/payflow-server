import { StyleSheet, Text, View } from "react-native";
import { REGISTRATION_STEPS } from "../constants/registration";
import { colors } from "../theme";

export function StepIndicator({ current }: { current: number }) {
  return (
    <View style={styles.wrap}>
      {REGISTRATION_STEPS.map((step) => {
        const active = current === step.id;
        const done = current > step.id;
        return (
          <View key={step.id} style={styles.item}>
            <View style={[styles.bar, done || active ? styles.barActive : null]} />
            <Text style={[styles.label, active ? styles.labelActive : null]}>{step.title}</Text>
          </View>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  wrap: { flexDirection: "row", gap: 6, marginBottom: 8 },
  item: { flex: 1 },
  bar: { height: 4, borderRadius: 99, backgroundColor: colors.slate300 },
  barActive: { backgroundColor: colors.primary },
  label: { marginTop: 6, fontSize: 9, fontWeight: "700", textTransform: "uppercase", color: colors.slate500, textAlign: "center" },
  labelActive: { color: colors.primary },
});
