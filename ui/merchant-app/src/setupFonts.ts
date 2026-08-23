import { Text, TextInput, type TextInputProps, type TextProps } from "react-native";
import { fonts } from "./theme";

export function applyOpenSansDefaults() {
  const baseStyle = { fontFamily: fonts.regular };
  const TextComponent = Text as typeof Text & { defaultProps?: Partial<TextProps> };
  TextComponent.defaultProps = { ...TextComponent.defaultProps, style: baseStyle };
  const InputComponent = TextInput as typeof TextInput & { defaultProps?: Partial<TextInputProps> };
  InputComponent.defaultProps = { ...InputComponent.defaultProps, style: baseStyle };
}
