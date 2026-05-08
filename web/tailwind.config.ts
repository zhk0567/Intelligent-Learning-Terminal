import type { Config } from "tailwindcss";

const config: Config = {
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  darkMode: ["class", '[data-theme="dark"]'],
  theme: {
    extend: {
      colors: {
        bg: {
          primary: "var(--bg-primary)",
          secondary: "var(--bg-secondary)",
          card: "var(--bg-card)",
          cardElevated: "var(--bg-card-elevated)",
        },
        text: {
          primary: "var(--text-primary)",
          secondary: "var(--text-secondary)",
          hint: "var(--text-hint)",
        },
        border: {
          DEFAULT: "var(--border-color)",
          divider: "var(--divider-color)",
        },
        ancient: {
          gold: "#D4AF37",
          red: "#C91F37",
          jade: "#00A86B",
          cinnabar: "#B7382E",
          cinnabarSoft: "#C95147",
          bronze: "#A57C2C",
          bronzeSoft: "#C2933C",
          celadon: "#7DA9A0",
          rice: "#F4EFE6",
          ink: "#1B2230",
        },
        neon: {
          teal: "#2EC4B6",
          tealDim: "#26A89C",
          cyan: "#00FFFF",
          cyanLight: "#5AB8B8",
        },
        player: {
          screen: "var(--player-screen)",
          control: "var(--player-control)",
          chip: "var(--player-chip)",
          frame: "var(--player-frame)",
        },
        mini: {
          surface: "var(--mini-surface)",
        },
      },
      fontFamily: {
        sans: [
          "system-ui",
          "-apple-system",
          "Segoe UI",
          "Roboto",
          "Helvetica Neue",
          "Arial",
          "Noto Sans",
          "PingFang SC",
          "Microsoft YaHei",
          "sans-serif",
        ],
      },
      maxWidth: {
        phone: "480px",
      },
      borderRadius: {
        card: "14px",
        seal: "3px",
      },
      boxShadow: {
        card: "0 2px 12px rgba(31, 27, 22, 0.08)",
        cardDark: "0 2px 16px rgba(0, 0, 0, 0.4)",
        stampInset: "inset 0 0 0 1.5px rgba(255,255,255,0.55)",
        primaryInset: "inset 0 0 0 1px rgba(255,255,255,0.18)",
      },
      keyframes: {
        fadeIn: {
          "0%": { opacity: "0", transform: "translateY(6px)" },
          "100%": { opacity: "1", transform: "translateY(0)" },
        },
        fadeInUp: {
          "0%": { opacity: "0", transform: "translateY(12px)" },
          "100%": { opacity: "1", transform: "translateY(0)" },
        },
        pulseSoft: {
          "0%,100%": { opacity: "0.5" },
          "50%": { opacity: "1" },
        },
        stampPress: {
          "0%": { transform: "scale(0.92) rotate(-2deg)", opacity: "0" },
          "60%": { transform: "scale(1.06) rotate(1deg)", opacity: "1" },
          "100%": { transform: "scale(1) rotate(0)", opacity: "1" },
        },
      },
      animation: {
        fadeIn: "fadeIn 200ms ease-out",
        fadeInUp: "fadeInUp 260ms ease-out",
        pulseSoft: "pulseSoft 1.4s ease-in-out infinite",
        stampPress: "stampPress 320ms cubic-bezier(0.34, 1.56, 0.64, 1)",
      },
    },
  },
  plugins: [],
};

export default config;
