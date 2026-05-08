import { create } from "zustand";

export type ThemeMode = "light" | "dark";

const STORAGE_KEY = "heritage_ui_prefs";

function readInitial(): ThemeMode {
  if (typeof window === "undefined") return "dark";
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw === "light" || raw === "dark") return raw;
  } catch {
    // ignore
  }
  return "dark";
}

interface ThemeState {
  mode: ThemeMode;
  setMode: (mode: ThemeMode) => void;
  toggle: () => void;
}

export const useThemeStore = create<ThemeState>((set, get) => ({
  mode: readInitial(),
  setMode: (mode) => {
    try {
      localStorage.setItem(STORAGE_KEY, mode);
    } catch {
      // ignore
    }
    set({ mode });
  },
  toggle: () => {
    const next = get().mode === "dark" ? "light" : "dark";
    get().setMode(next);
  },
}));
