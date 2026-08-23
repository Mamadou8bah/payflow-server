import { ScrollView, StyleSheet, Text, View } from "react-native";
import { ScreenTitle } from "../components/AppHeader";
import { WalletListCard } from "../components/WalletCarousel";
import { PrimaryButton } from "../components/ui";
import { useWallet } from "../context/WalletContext";
import { colors, spacing } from "../theme";

export function WalletsScreen() {
  const { data, setTab } = useWallet();

  return (
    <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
      <ScreenTitle title="My wallets" subtitle="Tap a wallet for top up, send, withdraw, and more" />

      {data.wallets.map((wallet) => (
        <WalletListCard key={wallet.id} wallet={wallet} />
      ))}

      <PrimaryButton
        label="Add new wallet"
        color={colors.orange}
        onPress={() => {}}
        style={styles.addBtn}
      />
      <PrimaryButton label="Top up selected wallet" onPress={() => setTab("topup")} />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  content: { padding: spacing.screen, paddingBottom: 32 },
  addBtn: { marginBottom: 12, marginTop: 8 },
});
