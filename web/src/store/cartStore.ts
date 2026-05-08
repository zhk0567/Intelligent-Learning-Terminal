import { create } from "zustand";

const KEY = "shop_cart_pending_count";

function readInit(): number {
  if (typeof window === "undefined") return 0;
  try {
    const v = parseInt(localStorage.getItem(KEY) ?? "0", 10);
    return Number.isFinite(v) ? Math.max(0, v) : 0;
  } catch {
    return 0;
  }
}

interface CartState {
  pending: number;
  add: (n?: number) => void;
  clear: () => void;
}

export const useCartStore = create<CartState>((set, get) => ({
  pending: readInit(),
  add: (n = 1) => {
    const v = Math.max(0, get().pending + n);
    try {
      localStorage.setItem(KEY, String(v));
    } catch {
      /* ignore */
    }
    set({ pending: v });
  },
  clear: () => {
    try {
      localStorage.setItem(KEY, "0");
    } catch {
      /* ignore */
    }
    set({ pending: 0 });
  },
}));
