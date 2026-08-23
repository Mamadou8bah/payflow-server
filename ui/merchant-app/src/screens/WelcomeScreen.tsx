import { StyleSheet, Text, View } from "react-native";
import { LinkButton, PayflowLogo, PrimaryButton, Screen } from "../components/ui";
import { useApp } from "../context/AppContext";
import { colors } from "../theme";

export function WelcomeScreen() {
  const { setScreen } = useApp();

  return (
    <Screen>
      <View style={styles.hero}>
        <PayflowLogo size={56} />
        <Text style={styles.eyebrow}>Payflow Merchant</Text>
        <Text style={styles.title}>Accept payments for your business</Text>
        <Text style={styles.body}>
          Register your Gambian business, verify your phone, and start collecting payments once approved.
        </Text>
      </View>

      <PrimaryButton label="Register my business" onPress={() => setScreen("reg-phone")} />
      <LinkButton label="Already registered? Sign in" onPress={() => setScreen("login")} />
    </Screen>
  );
}

const styles = StyleSheet.create({
  hero: { alignItems: "center", gap: 12, paddingVertical: 24 },
  eyebrow: { fontSize: 11, fontWeight: "800", letterSpacing: 2, color: colors.slate500, textTransform: "uppercase" },
  title: { fontSize: 28, fontWeight: "800", color: colors.slate900, textAlign: "center" },
  body: { fontSize: 15, lineHeight: 22, color: colors.slate500, textAlign: "center" },
});
