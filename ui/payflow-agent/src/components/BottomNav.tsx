import { Pressable, StyleSheet, Text, View } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { brand } from "../constants/brand";
import { colors, fonts, radii } from "../theme";
import type { TabId } from "../types";

type NavItem = { id: TabId; label: string; icon: keyof typeof Ionicons.glyphMap };

const tabs: NavItem[] = [
  { id: "home", label: "Home", icon: "home-outline" },
  { id: "scan", label: "Scan", icon: "qr-code-outline" },
  { id: "queue", label: "Queue", icon: "time-outline" },
  { id: "activity", label: "Activity", icon: "receipt-outline" },
  { id: "more", label: "More", icon: "person-outline" },
];

export function BottomNav({ active, onChange }: { active: TabId; onChange: (tab: TabId) => void }) {
  return (
    <View style={styles.wrapper}>
      <View style={styles.capsule}>
        {tabs.map((item) => {
          const isActive = active === item.id;
          return (
            <Pressable
              key={item.id}
              onPress={() => onChange(item.id)}
              style={[styles.item, isActive && styles.itemActive]}
            >
              <Ionicons name={item.icon} size={22} color={isActive ? colors.white : colors.slate500} />
              {isActive ? <Text style={styles.label}>{item.label}</Text> : null}
            </Pressable>
          );
        })}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    paddingHorizontal: 20,
    paddingTop: 8,
    paddingBottom: 12,
    backgroundColor: "transparent",
  },
  capsule: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    backgroundColor: colors.white,
    borderRadius: radii.pill,
    paddingVertical: 8,
    paddingHorizontal: 10,
    borderWidth: 1,
    borderColor: colors.border,
    shadowColor: brand.orange,
    shadowOpacity: 0.18,
    shadowRadius: 16,
    shadowOffset: { width: 0, height: 6 },
    elevation: 10,
  },
  item: {
    flex: 1,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: 8,
    paddingVertical: 10,
    paddingHorizontal: 8,
    borderRadius: radii.pill,
  },
  itemActive: {
    flexGrow: 1.6,
    backgroundColor: brand.orange,
    paddingHorizontal: 14,
  },
  label: {
    fontSize: 14,
    fontFamily: fonts.medium,
    color: colors.white,
  },
});
