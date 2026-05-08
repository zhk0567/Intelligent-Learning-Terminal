/**
 * 主题：与 web/src/store/themeStore.ts 对齐。
 *  - light / dark 两套
 *  - 通过 wx 持久化在本地
 *  - 在 onShow 时由各 Page 调用 applyTheme() 同步 page class
 */

const KEY = "heritage_ui_prefs";

export type ThemeMode = "light" | "dark";

let cached: ThemeMode | null = null;
const listeners = new Set<(mode: ThemeMode) => void>();

export function getTheme(): ThemeMode {
  if (cached) return cached;
  try {
    const raw = wx.getStorageSync<string>(KEY);
    if (raw === "light" || raw === "dark") {
      cached = raw;
      return raw;
    }
  } catch {
    /* ignore */
  }
  cached = "dark";
  return cached;
}

export function setTheme(mode: ThemeMode) {
  cached = mode;
  try {
    wx.setStorageSync(KEY, mode);
  } catch {
    /* ignore */
  }
  listeners.forEach((cb) => cb(mode));
}

export function toggleTheme(): ThemeMode {
  const next: ThemeMode = getTheme() === "dark" ? "light" : "dark";
  setTheme(next);
  return next;
}

export function subscribeTheme(cb: (mode: ThemeMode) => void): () => void {
  listeners.add(cb);
  return () => listeners.delete(cb);
}

/** 在 App.onLaunch 中调用一次。 */
export function initTheme(): ThemeMode {
  const mode = getTheme();
  return mode;
}

/**
 * 在 Page 的 onShow 中调用：
 *   applyTheme(this);
 * 会把 page-class 同步成 theme-light / theme-dark，触发 wxss 变量切换。
 */
export function applyTheme(page: WechatMiniprogram.IAnyObject): void {
  const mode = getTheme();
  if (page.data && page.data.__themeClass !== `theme-${mode}`) {
    page.setData({ __themeClass: `theme-${mode}` });
  } else if (!page.data || !page.data.__themeClass) {
    page.setData({ __themeClass: `theme-${mode}` });
  }
}
