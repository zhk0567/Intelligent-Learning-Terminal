import { create } from "zustand";

const KEY = "app_session_user";

interface User {
  name: string;
  phone: string;
  bio: string;
  vip: boolean;
}

const DEFAULT_USER: User = {
  name: "非遗文化爱好者",
  phone: "138****8888",
  bio: "已关注 4 | 粉丝 12",
  vip: true,
};

function readInit(): User {
  if (typeof window === "undefined") return DEFAULT_USER;
  try {
    const raw = localStorage.getItem(KEY);
    if (raw) return { ...DEFAULT_USER, ...JSON.parse(raw) };
  } catch {
    /* ignore */
  }
  return DEFAULT_USER;
}

interface SessionState {
  user: User;
  loggedIn: boolean;
  guest: boolean;
  setUser: (patch: Partial<User>) => void;
  loginAs: (mode: "phone" | "password" | "guest") => void;
  logout: () => void;
}

export const useSessionStore = create<SessionState>((set, get) => ({
  user: readInit(),
  loggedIn: false,
  guest: false,
  setUser: (patch) => {
    const next = { ...get().user, ...patch };
    try {
      localStorage.setItem(KEY, JSON.stringify(next));
    } catch {
      /* ignore */
    }
    set({ user: next });
  },
  loginAs: (mode) =>
    set({ loggedIn: mode !== "guest", guest: mode === "guest" }),
  logout: () => set({ loggedIn: false, guest: false }),
}));
