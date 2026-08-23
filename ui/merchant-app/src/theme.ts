export const colors = {
  background: "#eaf0ff",
  surface: "#ffffff",
  surfaceMuted: "#f1f5f9",
  border: "#dbeafe",
  primary: "#123c91",
  primaryDark: "#0d2f76",
  primaryMid: "#1649a8",
  primaryLight: "#1e5bb8",
  orange: "#f97316",
  orangeDark: "#ea580c",
  orangeLight: "#ffedd5",
  white: "#ffffff",
  slate900: "#0f172a",
  slate800: "#1e293b",
  slate700: "#334155",
  slate500: "#64748b",
  slate400: "#94a3b8",
  slate300: "#cbd5e1",
  emerald700: "#047857",
  emerald100: "#d1fae5",
  rose500: "#f43f5e",
  rose100: "#ffe4e6",
  rose800: "#9f1239",
  blue100: "#dbeafe",
};

export const radii = {
  sm: 12,
  md: 16,
  lg: 20,
  xl: 24,
  pill: 999,
};

export const spacing = {
  screen: 20,
  gap: 16,
};

export const fonts = {
  regular: "OpenSans_400Regular",
  medium: "OpenSans_500Medium",
  bold: "OpenSans_700Bold",
  black: "OpenSans_800ExtraBold",
} as const;

export const shadow = {
  card: {
    shadowColor: "#123c91",
    shadowOpacity: 0.08,
    shadowRadius: 12,
    shadowOffset: { width: 0, height: 4 },
    elevation: 3,
  },
};
