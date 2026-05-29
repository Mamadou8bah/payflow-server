import { StatusBar } from "expo-status-bar";
import { SafeAreaView, ScrollView, StyleSheet, Text, View } from "react-native";

const tasks = [
  "Accept and confirm deposits",
  "Review withdrawal requests",
  "Process merchant payouts",
  "Monitor queue status and activity"
];

export default function App() {
  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar style="light" />
      <ScrollView contentContainerStyle={styles.container}>
        <View style={styles.hero}>
          <Text style={styles.kicker}>Payflow Agent</Text>
          <Text style={styles.title}>Operations for agent-led cash movements.</Text>
          <Text style={styles.subtitle}>
            A companion app for agents handling deposits, withdrawals, and assisted customer payments.
          </Text>
        </View>

        <View style={styles.card}>
          <Text style={styles.sectionTitle}>Agent workflow</Text>
          {tasks.map((task) => (
            <Text key={task} style={styles.feature}>• {task}</Text>
          ))}
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: "#07111f"
  },
  container: {
    padding: 24,
    gap: 16
  },
  hero: {
    backgroundColor: "#0d1b30",
    borderRadius: 24,
    padding: 24,
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.08)"
  },
  kicker: {
    color: "#8b5cf6",
    textTransform: "uppercase",
    letterSpacing: 1.4,
    fontSize: 12,
    fontWeight: "700"
  },
  title: {
    color: "#eef4ff",
    fontSize: 34,
    fontWeight: "800",
    marginTop: 10,
    lineHeight: 40
  },
  subtitle: {
    color: "#a8b7d1",
    fontSize: 16,
    lineHeight: 24,
    marginTop: 12
  },
  card: {
    backgroundColor: "#0d1b30",
    borderRadius: 24,
    padding: 24,
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.08)"
  },
  sectionTitle: {
    color: "#eef4ff",
    fontSize: 18,
    fontWeight: "700",
    marginBottom: 12
  },
  feature: {
    color: "#d5def0",
    fontSize: 15,
    lineHeight: 24,
    marginTop: 4
  }
});
