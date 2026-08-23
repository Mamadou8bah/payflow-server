import { Image, StyleSheet, type ImageStyle } from "react-native";
import { PAYFLOW_LOGO_URI } from "../constants/brand";

export function PayflowLogo({
  size = 36,
  style,
}: {
  size?: number;
  style?: ImageStyle;
}) {
  return (
    <Image
      source={{ uri: PAYFLOW_LOGO_URI }}
      style={[styles.logo, { width: size, height: size }, style]}
      resizeMode="contain"
      accessibilityLabel="Payflow"
    />
  );
}

const styles = StyleSheet.create({
  logo: {
    aspectRatio: 1,
  },
});
