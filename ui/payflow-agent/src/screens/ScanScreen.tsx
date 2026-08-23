import { useCallback, useState } from "react";
import { Platform, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { CameraView, useCameraPermissions } from "expo-camera";
import { Ionicons } from "@expo/vector-icons";
import { ScreenTitle } from "../components/AppHeader";
import { AppInput, Field, FormError, PrimaryButton } from "../components/ui";
import { useAgent } from "../context/AgentContext";
import { brand } from "../constants/brand";
import { parseAgentQr } from "../utils/agentQr";
import { colors, fonts, radii, spacing } from "../theme";

export function ScanScreen() {
  const { lookupReference, lookupQr } = useAgent();
  const [permission, requestPermission] = useCameraPermissions();
  const [reference, setReference] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [scanning, setScanning] = useState(Platform.OS !== "web");

  const handleReference = useCallback(async () => {
    setError(null);
    const result = await lookupReference(reference);
    if (!result.ok) setError(result.error ?? null);
  }, [lookupReference, reference]);

  const handleBarcode = useCallback(
    async (raw: string) => {
      setError(null);
      const payload = parseAgentQr(raw);
      if (!payload) {
        setError("Unrecognized PayFlow QR code.");
        return;
      }
      const result = await lookupQr(payload);
      if (!result.ok) setError(result.error ?? null);
    },
    [lookupQr],
  );

  return (
    <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false} keyboardShouldPersistTaps="handled">
      <ScreenTitle title="Scan operation" subtitle="Scan a customer QR or enter a reference manually" />

      {scanning && Platform.OS !== "web" ? (
        <View style={styles.cameraWrap}>
          {!permission?.granted ? (
            <View style={styles.cameraPlaceholder}>
              <Ionicons name="camera-outline" size={40} color={colors.slate400} />
              <Text style={styles.cameraHint}>Camera access is needed to scan QR codes</Text>
              <PrimaryButton label="Allow camera" onPress={requestPermission} color={brand.orange} />
            </View>
          ) : (
            <CameraView
              style={styles.camera}
              facing="back"
              barcodeScannerSettings={{ barcodeTypes: ["qr"] }}
              onBarcodeScanned={({ data }) => handleBarcode(data)}
            />
          )}
          <Pressable onPress={() => setScanning(false)} style={styles.toggleBtn}>
            <Text style={styles.toggleText}>Enter reference instead</Text>
          </Pressable>
        </View>
      ) : (
        <View style={styles.manual}>
          <Field label="Operation reference" hint="Example: dep_1301 or wd_901">
            <AppInput
              value={reference}
              onChangeText={setReference}
              placeholder="dep_1301"
              autoCapitalize="none"
              autoCorrect={false}
            />
          </Field>
          <FormError message={error} />
          <PrimaryButton label="Look up operation" onPress={handleReference} color={brand.orange} />
          {Platform.OS !== "web" ? (
            <Pressable onPress={() => setScanning(true)} style={styles.toggleBtn}>
              <Text style={styles.toggleText}>Open camera scanner</Text>
            </Pressable>
          ) : null}
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  content: { padding: spacing.screen, paddingBottom: 120, gap: spacing.gap },
  cameraWrap: { gap: 12 },
  camera: { height: 280, borderRadius: radii.lg, overflow: "hidden" },
  cameraPlaceholder: {
    height: 280,
    borderRadius: radii.lg,
    backgroundColor: colors.surfaceMuted,
    alignItems: "center",
    justifyContent: "center",
    gap: 12,
    padding: 24,
    borderWidth: 1,
    borderColor: colors.border,
  },
  cameraHint: { fontSize: 14, fontFamily: fonts.regular, color: colors.slate500, textAlign: "center" },
  manual: { gap: 12 },
  toggleBtn: { alignItems: "center", paddingVertical: 8 },
  toggleText: { fontSize: 14, fontFamily: fonts.bold, color: colors.primary },
});
