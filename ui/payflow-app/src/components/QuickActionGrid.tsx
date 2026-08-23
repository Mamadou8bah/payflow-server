import { Pressable, StyleSheet, Text, View } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { colors, radii } from "../theme";

type Action = {
  label: string;
  icon: keyof typeof Ionicons.glyphMap;
  color: string;
  onPress: () => void;
};

export function QuickActionGrid({ actions }: { actions: Action[] }) {
  return (
    <View style={styles.grid}>
      {actions.map((action) => (
        <Pressable key={action.label} onPress={action.onPress} style={styles.item}>
          <View style={[styles.circle, { backgroundColor: action.color }]}>
            <Ionicons name={action.icon} size={22} color={colors.white} />
          </View>
          <Text style={styles.label}>{action.label}</Text>
        </Pressable>
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  grid: { flexDirection: "row", justifyContent: "space-between" },
  item: { alignItems: "center", width: "22%" },
  circle: {
    width: 56,
    height: 56,
    borderRadius: 28,
    alignItems: "center",
    justifyContent: "center",
    marginBottom: 8,
  },
  label: { fontSize: 12, fontWeight: "600", color: colors.slate700, textAlign: "center" },
});
