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

import { WalletActionsSheet } from "./src/components/WalletActionsSheet";

import { TransactionDetailSheet } from "./src/components/TransactionDetailSheet";

import { AuthProvider, useAuth } from "./src/context/AuthContext";

import { WalletProvider, navTabFor, useWallet } from "./src/context/WalletContext";

import { applyFontDefaults } from "./src/setupFonts";

import { ActivityScreen } from "./src/screens/ActivityScreen";

import { HomeScreen } from "./src/screens/HomeScreen";

import { LoginScreen } from "./src/screens/LoginScreen";

import { MoreScreen } from "./src/screens/MoreScreen";

import { ProfileScreen } from "./src/screens/ProfileScreen";

import { SendScreen } from "./src/screens/SendScreen";

import { SupportScreen } from "./src/screens/SupportScreen";

import { TopUpScreen } from "./src/screens/TopUpScreen";

import { WalletsScreen } from "./src/screens/WalletsScreen";

import { WithdrawScreen } from "./src/screens/WithdrawScreen";

import { colors } from "./src/theme";

import type { TabId } from "./src/types";



const mainTabs = new Set<TabId>(["home", "wallets", "send", "activity", "more"]);



function MainApp() {

  const { tab, setTab } = useWallet();

  const showNav = mainTabs.has(tab);



  return (

    <SafeAreaView style={styles.safeArea}>

      <StatusBar style="dark" />

      <View style={styles.body}>

        {tab === "home" && <HomeScreen />}

        {tab === "wallets" && <WalletsScreen />}

        {tab === "send" && <SendScreen />}

        {tab === "activity" && <ActivityScreen />}

        {tab === "more" && <MoreScreen />}

        {tab === "topup" && <TopUpScreen />}

        {tab === "withdraw" && <WithdrawScreen />}

        {tab === "support" && <SupportScreen />}

        {tab === "profile" && <ProfileScreen />}

      </View>



      {showNav ? <BottomNav active={navTabFor(tab)} onChange={setTab} /> : null}

      <WalletActionsSheet />

      <TransactionDetailSheet />

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

    <WalletProvider>

      <MainApp />

    </WalletProvider>

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
