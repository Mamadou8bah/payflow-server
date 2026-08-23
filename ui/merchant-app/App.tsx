import {
  OpenSans_400Regular,
  OpenSans_500Medium,
  OpenSans_700Bold,
  OpenSans_800ExtraBold,
  useFonts,
} from "@expo-google-fonts/open-sans";
import { useEffect, useRef } from "react";
import { ActivityIndicator, SafeAreaView, StyleSheet, View } from "react-native";
import { StatusBar } from "expo-status-bar";
import { AppProvider, useApp } from "./src/context/AppContext";
import { applyOpenSansDefaults } from "./src/setupFonts";
import { WelcomeScreen } from "./src/screens/WelcomeScreen";
import { LoginScreen } from "./src/screens/LoginScreen";
import { PhoneScreen } from "./src/screens/registration/PhoneScreen";
import { OtpScreen } from "./src/screens/registration/OtpScreen";
import { BusinessScreen } from "./src/screens/registration/BusinessScreen";
import { OwnerScreen } from "./src/screens/registration/OwnerScreen";
import { AccountScreen } from "./src/screens/registration/AccountScreen";
import { PendingScreen } from "./src/screens/PendingScreen";
import { HomeScreen } from "./src/screens/HomeScreen";
import { colors } from "./src/theme";

function Router() {
  const { screen, loading } = useApp();

  if (loading) {
    return (
      <View style={styles.loading}>
        <ActivityIndicator size="large" color={colors.primary} />
      </View>
    );
  }

  switch (screen) {
    case "welcome":
      return <WelcomeScreen />;
    case "login":
      return <LoginScreen />;
    case "reg-phone":
      return <PhoneScreen />;
    case "reg-otp":
      return <OtpScreen />;
    case "reg-business":
      return <BusinessScreen />;
    case "reg-owner":
      return <OwnerScreen />;
    case "reg-account":
      return <AccountScreen />;
    case "pending":
      return <PendingScreen />;
    case "home":
      return <HomeScreen />;
    default:
      return <WelcomeScreen />;
  }
}

function MainApp() {
  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar style="dark" />
      <Router />
    </SafeAreaView>
  );
}

export default function App() {
  const [fontsLoaded] = useFonts({
    OpenSans_400Regular,
    OpenSans_500Medium,
    OpenSans_700Bold,
    OpenSans_800ExtraBold,
  });
  const fontsApplied = useRef(false);

  useEffect(() => {
    if (fontsLoaded && !fontsApplied.current) {
      applyOpenSansDefaults();
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
    <AppProvider>
      <MainApp />
    </AppProvider>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: colors.background },
  loading: { flex: 1, alignItems: "center", justifyContent: "center", backgroundColor: colors.background },
});
