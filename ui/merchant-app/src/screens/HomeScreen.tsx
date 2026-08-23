import { useEffect, useState } from "react";
import { ActivityIndicator, StyleSheet, Text, View } from "react-native";
import { Card, PayflowLogo, PrimaryButton, Screen } from "../components/ui";
import { fetchMerchantDashboard } from "../api/dashboard";
import { useApp } from "../context/AppContext";
import { colors } from "../theme";

export function HomeScreen() {
  const { session, signOut } = useApp();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [stats, setStats] = useState({ totalBalance: 0, activeLinks: 0, currency: "GMD" });

  useEffect(() => {
    if (!session?.accessToken) {
      setLoading(false);
      return;
    }
    setError("");
    fetchMerchantDashboard()
      .then(setStats)
      .catch((err) => setError(err instanceof Error ? err.message : "Failed to load dashboard"))
      .finally(() => setLoading(false));
  }, [session?.accessToken]);

  return (
    <Screen>
      <View style={styles.header}>
        <PayflowLogo size={36} />
        <View>
          <Text style={styles.eyebrow}>Merchant dashboard</Text>
          <Text style={styles.title}>{session?.businessName ?? "Your business"}</Text>
        </View>
      </View>

      <Card>
        <Text style={styles.cardTitle}>Collections</Text>
        <Text style={styles.cardBody}>
          Create payment links, track transactions, and manage deposits from your merchant wallet.
        </Text>
      </Card>

      {error ? (
        <Card style={styles.errorCard}>
          <Text style={styles.errorText}>{error}</Text>
        </Card>
      ) : null}

      {loading ? (
        <ActivityIndicator color={colors.primary} />
      ) : (
        <View style={styles.statsRow}>
          <Card style={styles.stat}>
            <Text style={styles.statLabel}>Balance</Text>
            <Text style={styles.statValue}>
              {stats.currency} {stats.totalBalance.toLocaleString()}
            </Text>
          </Card>
          <Card style={styles.stat}>
            <Text style={styles.statLabel}>Links</Text>
            <Text style={styles.statValue}>{stats.activeLinks} active</Text>
          </Card>
        </View>
      )}

      <PrimaryButton label="Sign out" onPress={signOut} color={colors.slate700} />
    </Screen>
  );
}

const styles = StyleSheet.create({
  header: { flexDirection: "row", alignItems: "center", gap: 12 },
  eyebrow: { fontSize: 11, fontWeight: "700", color: colors.slate500, textTransform: "uppercase" },
  title: { fontSize: 22, fontWeight: "800", color: colors.slate900 },
  cardTitle: { fontSize: 18, fontWeight: "800", color: colors.slate900 },
  cardBody: { marginTop: 8, fontSize: 14, lineHeight: 20, color: colors.slate500 },
  statsRow: { flexDirection: "row", gap: 12 },
  stat: { flex: 1 },
  statLabel: { fontSize: 12, fontWeight: "700", color: colors.slate500, textTransform: "uppercase" },
  statValue: { marginTop: 6, fontSize: 20, fontWeight: "800", color: colors.primary },
  errorCard: { backgroundColor: "#fef2f2", borderColor: "#fecaca" },
  errorText: { color: "#b91c1c", fontSize: 14, fontWeight: "600" },
});
