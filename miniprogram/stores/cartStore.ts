import { createStore } from "../utils/store";

interface CartState {
  pending: number;
}

export const cartStore = createStore<
  CartState,
  {
    add: (n?: number) => void;
    clear: () => void;
  }
>({
  state: { pending: 0 },
  persist: {
    key: "shop_cart_pending_count",
    pick: (s) => ({ pending: s.pending }),
    hydrate: (raw) => {
      const v = typeof raw === "object" && raw ? Number((raw as any).pending) : Number(raw);
      return { pending: Number.isFinite(v) ? Math.max(0, v) : 0 };
    },
  },
  actions: (set, get) => ({
    add(n = 1) {
      set({ pending: Math.max(0, get().pending + n) });
    },
    clear() {
      set({ pending: 0 });
    },
  }),
});
