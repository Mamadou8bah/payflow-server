import { FlatList, Pressable, StyleSheet, Text, View } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { LinearGradient } from "expo-linear-gradient";
import { brand } from "../constants/brand";
import { useWallet } from "../context/WalletContext";
import { colors, fonts, radii, shadow } from "../theme";
import type { CustomerWallet } from "../types";
import { formatBalance } from "../utils/format";
import { PayflowLogo } from "./PayflowLogo";
import { WalletCardArt } from "./WalletCardArt";

const cardThemes: Record<
  CustomerWallet["kind"],
  { gradient: [string, string, string]; accent: string; label: string }
> = {
  primary: {
    gradient: [brand.blackSoft, brand.black, "#000000"],
    accent: brand.orange,
    label: "Primary",
  },
  savings: {
    gradient: ["#1a2332", brand.blackSoft, brand.black],
    accent: "#38bdf8",
    label: "Savings",
  },
  secondary: {
    gradient: [brand.charcoal, brand.blackSoft, brand.black],
    accent: brand.orangeBright,
    label: "Secondary",
  },
};

export function WalletCarousel({
  wallets,
  selectedId,
}: {
  wallets: CustomerWallet[];
  selectedId: number;
}) {
  const { openWalletActions } = useWallet();

  return (
    <FlatList
      horizontal
      data={wallets}
      keyExtractor={(item) => String(item.id)}
      showsHorizontalScrollIndicator={false}
      contentContainerStyle={styles.list}
      snapToInterval={304}
      decelerationRate="fast"
      renderItem={({ item }) => (
        <WalletSlide
          wallet={item}
          active={item.id === selectedId}
          onPress={() => openWalletActions(item.id)}
        />
      )}
    />
  );
}

function BalanceToggle({
  amount,
  currency,
  hidden,
  onToggle,
  variant,
}: {
  amount: number;
  currency: string;
  hidden: boolean;
  onToggle: () => void;
  variant: "carousel" | "list";
}) {
  const isCarousel = variant === "carousel";

  return (
    <View style={isCarousel ? styles.balanceRow : styles.listBalanceRow}>
      <Text style={isCarousel ? styles.balance : styles.listBalance}>
        {formatBalance(amount, currency, hidden)}
      </Text>
      <Pressable
        onPress={onToggle}
        hitSlop={10}
        style={isCarousel ? styles.eyeBtn : styles.listEyeBtn}
        accessibilityLabel={hidden ? "Show balance" : "Hide balance"}
      >
        <Ionicons
          name={hidden ? "eye-off-outline" : "eye-outline"}
          size={isCarousel ? 20 : 18}
          color={isCarousel ? "rgba(255,255,255,0.85)" : colors.slate500}
        />
      </Pressable>
    </View>
  );
}

function WalletSlide({
  wallet,
  active,
  onPress,
}: {
  wallet: CustomerWallet;
  active: boolean;
  onPress: () => void;
}) {
  const { balanceHidden, toggleBalanceHidden } = useWallet();
  const shortId = String(wallet.id).slice(-4);
  const theme = cardThemes[wallet.kind];

  return (
    <Pressable onPress={onPress} style={[styles.cardOuter, active && styles.cardOuterActive, shadow.card]}>
      <LinearGradient colors={theme.gradient} style={styles.card}>
        <WalletCardArt variant="carousel" />

        <View style={styles.cardInner}>
          <View style={styles.top}>
            <PayflowLogo size={44} />
            <View style={styles.topRight}>
              <Text style={styles.kind}>{theme.label}</Text>
              <Text style={styles.digits}>··· {shortId}</Text>
            </View>
          </View>

          <BalanceToggle
            amount={wallet.balance}
            currency={wallet.currency}
            hidden={balanceHidden}
            onToggle={toggleBalanceHidden}
            variant="carousel"
          />

          <Text style={styles.tapHint}>Tap card for wallet actions</Text>

          <View style={styles.bottom}>
            <View style={styles.bottomBlock}>
              <Text style={styles.label}>Wallet</Text>
              <Text style={styles.value}>{wallet.name}</Text>
            </View>
            <View style={[styles.bottomBlock, styles.bottomRight]}>
              <Text style={styles.label}>Status</Text>
              <Text style={[styles.value, { color: theme.accent }]}>{wallet.status}</Text>
            </View>
          </View>
        </View>

        <View style={[styles.accentBar, { backgroundColor: theme.accent }]} />
      </LinearGradient>
    </Pressable>
  );
}

export function WalletListCard({ wallet }: { wallet: CustomerWallet }) {
  const { balanceHidden, toggleBalanceHidden, openWalletActions } = useWallet();
  const shortId = String(wallet.id).slice(-4);
  const theme = cardThemes[wallet.kind];

  return (
    <Pressable onPress={() => openWalletActions(wallet.id)} style={[styles.listCard, shadow.card]}>
      <View style={styles.listArtPanel}>
        <LinearGradient colors={[theme.gradient[0], theme.gradient[2]]} style={styles.listGradient}>
          <WalletCardArt variant="compact" />
          <PayflowLogo size={36} />
        </LinearGradient>
      </View>
      <View style={styles.listBody}>
        <Text style={styles.listName}>{wallet.name}</Text>
        <Text style={styles.listMeta}>
          {wallet.currency} · ··· {shortId}
        </Text>
      </View>
      <View style={styles.listRight}>
        <BalanceToggle
          amount={wallet.balance}
          currency={wallet.currency}
          hidden={balanceHidden}
          onToggle={toggleBalanceHidden}
          variant="list"
        />
        <Text style={[styles.listStatus, { color: theme.accent }]}>{wallet.status}</Text>
      </View>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  list: { paddingVertical: 4, paddingRight: 20 },
  cardOuter: {
    borderRadius: radii.xl,
    marginRight: 12,
    overflow: "hidden",
  },
  cardOuterActive: {
    borderWidth: 2,
    borderColor: brand.orange,
  },
  card: {
    width: 292,
    height: 180,
    borderRadius: radii.xl,
    overflow: "hidden",
  },
  cardInner: {
    flex: 1,
    padding: 20,
    zIndex: 1,
  },
  accentBar: {
    position: "absolute",
    bottom: 0,
    left: 0,
    right: 0,
    height: 4,
    zIndex: 2,
  },
  top: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "flex-start",
  },
  topRight: { alignItems: "flex-end" },
  kind: {
    fontSize: 10,
    fontFamily: fonts.bold,
    color: brand.orange,
    textTransform: "uppercase",
    letterSpacing: 1,
  },
  digits: {
    color: "rgba(255,255,255,0.72)",
    fontFamily: fonts.medium,
    fontSize: 13,
    marginTop: 2,
  },
  balanceRow: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    marginTop: 14,
    gap: 8,
  },
  balance: {
    color: colors.white,
    fontSize: 24,
    fontFamily: fonts.black,
    letterSpacing: -0.5,
    flex: 1,
  },
  eyeBtn: {
    width: 36,
    height: 36,
    borderRadius: 18,
    backgroundColor: "rgba(255,255,255,0.12)",
    alignItems: "center",
    justifyContent: "center",
  },
  tapHint: {
    fontSize: 10,
    fontFamily: fonts.regular,
    color: "rgba(255,255,255,0.45)",
    marginTop: 4,
  },
  bottom: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginTop: "auto",
  },
  bottomBlock: { gap: 2 },
  bottomRight: { alignItems: "flex-end" },
  label: {
    color: "rgba(255,255,255,0.5)",
    fontSize: 10,
    fontFamily: fonts.bold,
    textTransform: "uppercase",
    letterSpacing: 0.8,
  },
  value: {
    color: colors.white,
    fontFamily: fonts.medium,
    fontSize: 13,
  },
  listCard: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: colors.surface,
    borderRadius: radii.lg,
    overflow: "hidden",
    marginBottom: 12,
    borderWidth: 1,
    borderColor: colors.border,
  },
  listArtPanel: {
    width: 72,
    height: 88,
  },
  listGradient: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    overflow: "hidden",
  },
  listBody: { flex: 1, paddingVertical: 14, paddingHorizontal: 12 },
  listName: { fontSize: 16, fontFamily: fonts.bold, color: colors.slate900 },
  listMeta: { fontSize: 13, fontFamily: fonts.regular, color: colors.slate500, marginTop: 2 },
  listRight: { alignItems: "flex-end", paddingRight: 16 },
  listBalanceRow: { flexDirection: "row", alignItems: "center", gap: 6 },
  listBalance: { fontSize: 15, fontFamily: fonts.bold, color: colors.slate900 },
  listEyeBtn: { padding: 4 },
  listStatus: { fontSize: 11, fontFamily: fonts.medium, marginTop: 2 },
});
