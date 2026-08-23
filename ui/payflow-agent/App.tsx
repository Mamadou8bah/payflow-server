import {
  SpaceGrotesk_400Regular,
  SpaceGrotesk_500Medium,
  SpaceGrotesk_600SemiBold,
  SpaceGrotesk_700Bold,
  useFonts,
} from "@expo-google-fonts/space-grotesk";
import { useEffect, useRef } from "react";
import { StatusBar } from "expo-status-bar";
import { ActivityIndicator, SafeAreaView, StyleSheet, View } from "react-native";
import { BottomNav } from "./src/components/BottomNav";
import { AuthProvider, useAuth } from "./src/context/AuthContext";
import { AgentProvider, navTabFor, useAgent } from "./src/context/AgentContext";
import { applyFontDefaults } from "./src/setupFonts";
import { ActivityScreen } from "./src/screens/ActivityScreen";
import { ConfirmScreen } from "./src/screens/ConfirmScreen";
import { HomeScreen } from "./src/screens/HomeScreen";
import { LoginScreen } from "./src/screens/LoginScreen";
import { MoreScreen } from "./src/screens/MoreScreen";
import { NewDepositScreen } from "./src/screens/NewDepositScreen";
import { NewWithdrawalScreen } from "./src/screens/NewWithdrawalScreen";
import { ProfileScreen } from "./src/screens/ProfileScreen";
import { QueueScreen } from "./src/screens/QueueScreen";
import { ScanScreen } from "./src/screens/ScanScreen";
import { SupportScreen } from "./src/screens/SupportScreen";
import { colors } from "./src/theme";
import type { TabId } from "./src/types";

const mainTabs = new Set<TabId>(["home", "scan", "queue", "activity", "more"]);

function MainApp() {
  const { tab, setTab } = useAgent();
  const showNav = mainTabs.has(tab);

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar style="dark" />
      <View style={styles.body}>
        {tab === "home" && <HomeScreen />}
        {tab === "scan" && <ScanScreen />}
        {tab === "queue" && <QueueScreen />}
        {tab === "activity" && <ActivityScreen />}
        {tab === "more" && <MoreScreen />}
        {tab === "confirm" && <ConfirmScreen />}
        {tab === "new-deposit" && <NewDepositScreen />}
        {tab === "new-withdrawal" && <NewWithdrawalScreen />}
        {tab === "profile" && <ProfileScreen />}
        {tab === "support" && <SupportScreen />}
      </View>

      {showNav ? <BottomNav active={navTabFor(tab)} onChange={setTab} /> : null}
    </SafeAreaView>
  );
}

function Root() {
  const { session, loading } = useAuth();
  if (loading) {
    return (
      <View style={styles.loading}>
        <ActivityIndicator size="large" color={colors.primary} />
      </View>
    );
  }
  if (!session) {
    return <LoginScreen />;
  }
  return (
    <AgentProvider>
      <MainApp />
    </AgentProvider>
  );
}

export default function App() {
  const [fontsLoaded] = useFonts({
    SpaceGrotesk_400Regular,
    SpaceGrotesk_500Medium,
    SpaceGrotesk_600SemiBold,
    SpaceGrotesk_700Bold,
  });
  const fontsApplied = useRef(false);

  useEffect(() => {
    if (fontsLoaded && !fontsApplied.current) {
      applyFontDefaults();
      fontsApplied.current = true;
    }
  }, [fontsLoaded]);

  if (!fontsLoaded) {
    return (
      <View style={styles.loading}>
        <ActivityIndicator size="large" color={colors.primary} />
      </View>
    );
  }

  return (
    <AuthProvider>
      <Root />
    </AuthProvider>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: colors.background },
  body: { flex: 1 },
  loading: { flex: 1, alignItems: "center", justifyContent: "center", backgroundColor: colors.background },
});
