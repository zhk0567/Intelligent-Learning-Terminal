/** 与 app/app/src/main/res/values{,-night}/colors.xml 对齐 + 非遗扁平点缀色。 */
export const tokens = {
  ancient: {
    gold: "#D4AF37",
    red: "#C91F37",
    jade: "#00A86B",
    cinnabar: "#B7382E",
    bronze: "#A57C2C",
    celadon: "#7DA9A0",
    rice: "#F4EFE6",
    ink: "#1B2230",
  },
  neon: {
    cyan: "#00FFFF",
    teal: "#2EC4B6",
    tealDim: "#26A89C",
    cyanLight: "#5AB8B8",
  },
  light: {
    bgPrimary: "#F4EFE6",
    bgSecondary: "#EBE3D3",
    bgCard: "#FFFFFF",
    bgCardElevated: "#FBF7EE",
    textPrimary: "#1F1B16",
    textSecondary: "#5C5346",
    textHint: "#8C7E6A",
    border: "#B5A99A",
    divider: "#D6CDB8",
    playerScreen: "#EBE3D3",
    miniSurface: "#FFFFFF",
  },
  dark: {
    bgPrimary: "#0A0E17",
    bgSecondary: "#111827",
    bgCard: "#1A2332",
    bgCardElevated: "#222E3F",
    textPrimary: "#F2EFEA",
    textSecondary: "#B7B0A4",
    textHint: "#7A8290",
    border: "rgba(212,212,212,0.22)",
    divider: "rgba(212,212,212,0.12)",
    playerScreen: "#0A101F",
    miniSurface: "#1A2332",
  },
} as const;

export type ThemeMode = "light" | "dark";
