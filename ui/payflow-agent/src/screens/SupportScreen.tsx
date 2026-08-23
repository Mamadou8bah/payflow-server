import { ScrollView, StyleSheet, Text, View } from "react-native";
import { BackHeader } from "../components/AppHeader";
import { PayflowLogo } from "../components/PayflowLogo";
import { useAgent } from "../context/AgentContext";
import { colors, fonts, radii, spacing } from "../theme";

export function SupportScreen() {
  const { setTab } = useAgent();

  return (
    <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
      <BackHeader title="Operations support" onBack={() => setTab("more")} />

      <View style={styles.hero}>
        <PayflowLogo size={40} />
        <Text style={styles.heroTitle}>PayFlow agent support</Text>
        <Text style={styles.heroSub}>Available 8am – 8pm daily</Text>
      </View>

      <View style={[styles.bubble, styles.supportBubble]}>
        <Text style={[styles.bubbleText, styles.supportText]}>
          Need help with an operation? Contact operations support with the customer reference and we will assist.
        </Text>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  content: { padding: spacing.screen, paddingBottom: 120, gap: 10 },
  hero: {
    alignItems: "center",
    backgroundColor: colors.surface,
    borderRadius: radii.lg,
    padding: 20,
    marginBottom: 8,
    borderWidth: 1,
    borderColor: colors.border,
  },
  heroTitle: { fontSize: 18, fontFamily: fonts.bold, color: colors.slate900, marginTop: 10 },
  heroSub: { fontSize: 13, fontFamily: fonts.regular, color: colors.slate500, marginTop: 4 },
  bubble: { maxWidth: "85%", padding: 14, borderRadius: radii.md },
  supportBubble: { alignSelf: "flex-start", backgroundColor: colors.surfaceMuted, borderWidth: 1, borderColor: colors.border },
  bubbleText: { fontSize: 14, lineHeight: 20 },
  supportText: { color: colors.slate800, fontFamily: fonts.regular },
});
