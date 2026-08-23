import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { BackHeader } from "../components/AppHeader";
import { ListRow, SegmentedControl } from "../components/SegmentedControl";
import { PrimaryButton } from "../components/ui";
import { useAuth } from "../context/AuthContext";
import { useWallet } from "../context/WalletContext";
import { colors, radii, spacing } from "../theme";
import type { ProfileSection } from "../types";

export function ProfileScreen() {
  const { signOut } = useAuth();
  const { data, profileSection, setProfileSection, setTab } = useWallet();

  const sections: { id: ProfileSection; label: string }[] = [
    { id: "profile", label: "Profile" },
    { id: "settings", label: "Settings" },
    { id: "support", label: "Support" },
    { id: "security", label: "Security" },
  ];

  return (
    <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
      <BackHeader title="Personal account" onBack={() => setTab("more")} />

      <View style={styles.hero}>
        <Text style={styles.name}>{data.session.name}</Text>
        <Text style={styles.phone}>{data.session.phone}</Text>
      </View>

      <SegmentedControl options={sections} value={profileSection} onChange={setProfileSection} />

      <View style={styles.card}>
        {profileSection === "profile" && (
          <>
            <ListRow label="Full name" value={data.session.name} showChevron />
            <ListRow label="Phone" value={data.session.phone} showChevron />
            <ListRow label="Email" value={data.session.email} showChevron />
            <ListRow label="Address" value={data.session.address} showChevron />
          </>
        )}
        {profileSection === "settings" && (
          <>
            <ListRow label="Notifications" value="Enabled" showChevron />
            <ListRow label="Default wallet" value="Main wallet" showChevron />
            <ListRow label="Language" value="English" showChevron />
          </>
        )}
        {profileSection === "support" && (
          <>
            <ListRow label="Chat with support" onPress={() => setTab("support")} showChevron />
            <ListRow label="FAQ" value="Help center" showChevron />
            <ListRow label="Report an issue" showChevron />
          </>
        )}
        {profileSection === "security" && (
          <>
            <ListRow label="Two-factor auth" value="Off" showChevron />
            <ListRow label="Change PIN" showChevron />
            <ListRow label="Biometric login" value="On" showChevron />
          </>
        )}
      </View>

      <PrimaryButton
        label="Log out"
        color={colors.rose500}
        onPress={() => {
          void signOut();
        }}
        style={styles.logout}
      />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  content: { padding: spacing.screen, paddingBottom: 32, gap: spacing.gap },
  hero: {
    backgroundColor: colors.surface,
    borderRadius: radii.lg,
    padding: 20,
    borderWidth: 1,
    borderColor: colors.border,
  },
  name: { fontSize: 20, fontWeight: "800", color: colors.slate900 },
  phone: { fontSize: 14, color: colors.slate500, marginTop: 4 },
  card: {
    backgroundColor: colors.surface,
    borderRadius: radii.lg,
    paddingHorizontal: 16,
    borderWidth: 1,
    borderColor: colors.border,
  },
  logout: { marginTop: 8 },
});
