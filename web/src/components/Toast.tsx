import { create } from "zustand";
import { useEffect } from "react";

interface ToastState {
  message: string;
  visible: boolean;
  show: (m: string) => void;
  hide: () => void;
}

const store = create<ToastState>((set) => ({
  message: "",
  visible: false,
  show: (m) => set({ message: m, visible: true }),
  hide: () => set({ visible: false }),
}));

export function toast(message: string) {
  store.getState().show(message);
}

export function ToastHost() {
  const { message, visible, hide } = store();
  useEffect(() => {
    if (!visible) return;
    const t = setTimeout(hide, 1800);
    return () => clearTimeout(t);
  }, [visible, hide]);

  if (!visible) return null;
  return (
    <div className="pointer-events-none fixed inset-x-0 bottom-32 z-[60] flex justify-center px-6">
      <div className="rounded-full bg-black/80 px-5 py-2 text-sm text-white shadow-cardDark animate-fadeIn">
        {message}
      </div>
    </div>
  );
}
