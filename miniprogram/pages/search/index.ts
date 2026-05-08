import { applyTheme } from "../../utils/theme";
import { HOT_KEYWORDS } from "../../data/music";

const HISTORY_KEY = "search_history";

function loadHist(): string[] {
  try {
    const raw = wx.getStorageSync<string[]>(HISTORY_KEY);
    return Array.isArray(raw) ? raw : [];
  } catch {
    return [];
  }
}

Page({
  data: {
    __themeClass: "theme-dark",
    q: "",
    hotKeywords: HOT_KEYWORDS,
    hist: [] as string[],
  },
  onLoad() {
    applyTheme(this);
    this.setData({ hist: loadHist() });
  },
  onShow() { applyTheme(this); },
  onInput(e: any) {
    this.setData({ q: e.detail.value });
  },
  submit(e?: any) {
    const word = (e?.detail?.value ?? this.data.q).trim();
    if (!word) return;
    const next = [word, ...this.data.hist.filter((x) => x !== word)].slice(0, 12);
    this.setData({ hist: next });
    try {
      wx.setStorageSync(HISTORY_KEY, next);
    } catch { /* ignore */ }
    wx.navigateTo({ url: `/pages/search-result/index?q=${encodeURIComponent(word)}` });
  },
  pickHot(e: any) {
    const kw = e.currentTarget.dataset.kw;
    this.setData({ q: kw });
    this.submit();
  },
  clearHist() {
    this.setData({ hist: [] });
    try {
      wx.removeStorageSync(HISTORY_KEY);
    } catch { /* ignore */ }
  },
  goBack() {
    const pages = getCurrentPages();
    if (pages.length > 1) wx.navigateBack({ delta: 1 });
    else wx.switchTab({ url: "/pages/music/index" });
  },
});
