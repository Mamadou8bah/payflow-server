import { useState } from "react";
import { FlatList, KeyboardAvoidingView, Platform, Pressable, StyleSheet, Text, TextInput, View } from "react-native";
import { BackHeader } from "../components/AppHeader";
import { useWallet } from "../context/WalletContext";
import type { ChatMessage } from "../types";
import { colors, radii, spacing } from "../theme";

const WELCOME: ChatMessage = {
  id: "welcome",
  from: "agent",
  text: "Hi — how can PayFlow support help you today?",
  time: "Now",
};

export function SupportScreen() {
  const { setTab } = useWallet();
  const [messages, setMessages] = useState<ChatMessage[]>([WELCOME]);
  const [draft, setDraft] = useState("");

  function send() {
    if (!draft.trim()) return;
    setMessages((prev) => [
      ...prev,
      { id: `m${prev.length + 1}`, from: "user" as const, text: draft.trim(), time: "Now" },
    ]);
    setDraft("");
  }

  return (
    <KeyboardAvoidingView
      style={styles.flex}
      behavior={Platform.OS === "ios" ? "padding" : undefined}
      keyboardVerticalOffset={80}
    >
      <BackHeader title="Support" onBack={() => setTab("more")} />
      <FlatList
        data={messages}
        keyExtractor={(item) => item.id}
        contentContainerStyle={styles.list}
        renderItem={({ item }) => (
          <View style={[styles.bubble, item.from === "user" ? styles.userBubble : styles.supportBubble]}>
            <Text style={[styles.bubbleText, item.from === "user" && styles.userText]}>{item.text}</Text>
            <Text style={styles.time}>{item.time}</Text>
          </View>
        )}
      />
      <View style={styles.composer}>
        <TextInput
          style={styles.input}
          value={draft}
          onChangeText={setDraft}
          placeholder="Type a message"
          placeholderTextColor={colors.slate400}
        />
        <Pressable onPress={send} style={styles.sendBtn}>
          <Text style={styles.sendLabel}>Send</Text>
        </Pressable>
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  flex: { flex: 1, backgroundColor: colors.background },
  list: { padding: spacing.screen, gap: 10, paddingBottom: 16 },
  bubble: { maxWidth: "82%", borderRadius: radii.lg, padding: 12, gap: 4 },
  supportBubble: { alignSelf: "flex-start", backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.border },
  userBubble: { alignSelf: "flex-end", backgroundColor: colors.primary },
  bubbleText: { fontSize: 14, color: colors.slate900 },
  userText: { color: "#fff" },
  time: { fontSize: 11, color: colors.slate400 },
  composer: {
    flexDirection: "row",
    gap: 8,
    padding: spacing.screen,
    borderTopWidth: 1,
    borderTopColor: colors.border,
    backgroundColor: colors.surface,
  },
  input: {
    flex: 1,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: radii.md,
    paddingHorizontal: 12,
    paddingVertical: 10,
    color: colors.slate900,
  },
  sendBtn: {
    backgroundColor: colors.primary,
    borderRadius: radii.md,
    paddingHorizontal: 16,
    justifyContent: "center",
  },
  sendLabel: { color: "#fff", fontWeight: "700" },
});
