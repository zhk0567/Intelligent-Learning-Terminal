/**
 * 简易 toast 包装：与 web/src/components/Toast.tsx 的 toast() 一致语义。
 * 短文走 wx.showToast(icon=none)；长文（含换行）自动 splice。
 */
export function toast(message: string, opt?: { icon?: "success" | "error" | "loading" | "none"; duration?: number }) {
  wx.showToast({
    title: message.length > 14 ? message.slice(0, 13) + "…" : message,
    icon: opt?.icon ?? "none",
    duration: opt?.duration ?? 1800,
    mask: false,
  });
}

export function toastSuccess(message: string) {
  wx.showToast({ title: message, icon: "success", duration: 1500 });
}

export function toastLoading(message: string) {
  wx.showToast({ title: message, icon: "loading", duration: 60_000, mask: true });
}

export function hideToast() {
  wx.hideToast();
}
