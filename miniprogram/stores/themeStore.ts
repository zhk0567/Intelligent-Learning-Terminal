/**
 * 与 web/src/store/themeStore.ts 对齐，仅暴露 set/get/toggle，
 * 实际持久化与 page class 同步在 utils/theme.ts 完成。
 */
import { getTheme, setTheme, toggleTheme, subscribeTheme, type ThemeMode } from "../utils/theme";

export const themeStore = {
  get(): ThemeMode {
    return getTheme();
  },
  setMode(mode: ThemeMode) {
    setTheme(mode);
  },
  toggle(): ThemeMode {
    return toggleTheme();
  },
  subscribe: subscribeTheme,
};
