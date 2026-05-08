import { ReactNode, useEffect } from "react";
import { useThemeStore } from "../store/themeStore";

export function ThemeProvider({ children }: { children: ReactNode }) {
  const mode = useThemeStore((s) => s.mode);

  useEffect(() => {
    document.documentElement.dataset.theme = mode;
    document
      .querySelector('meta[name="theme-color"]')
      ?.setAttribute("content", mode === "dark" ? "#0A0E17" : "#EEF2F7");
  }, [mode]);

  return <>{children}</>;
}
