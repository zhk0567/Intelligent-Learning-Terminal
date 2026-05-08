import { createStore } from "../utils/store";

export interface User {
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

interface SessionState {
  user: User;
  loggedIn: boolean;
  guest: boolean;
}

export const sessionStore = createStore<
  SessionState,
  {
    setUser: (patch: Partial<User>) => void;
    loginAs: (mode: "phone" | "password" | "guest") => void;
    logout: () => void;
  }
>({
  state: {
    user: DEFAULT_USER,
    loggedIn: false,
    guest: false,
  },
  persist: {
    key: "app_session_user",
    pick: (s) => ({ user: s.user }),
    hydrate: (raw) => ({ user: { ...DEFAULT_USER, ...(raw as Partial<User>) } }),
  },
  actions: (set, get) => ({
    setUser(patch) {
      set({ user: { ...get().user, ...patch } });
    },
    loginAs(mode) {
      set({ loggedIn: mode !== "guest", guest: mode === "guest" });
    },
    logout() {
      set({ loggedIn: false, guest: false });
    },
  }),
});
