import { applyTheme } from "../../utils/theme";
import { HISTORY, type HistoryItem } from "../data/history";
import { toast } from "../../utils/toast";

interface Group { day: string; items: HistoryItem[]; }

function group(items: HistoryItem[]): Group[] {
  const map: Record<string, HistoryItem[]> = {};
  items.forEach((h) => {
    const day = h.browseTime.startsWith("今") ? "今日" : h.browseTime.startsWith("昨") ? "昨日" : "更早";
    (map[day] ??= []).push(h);
  });
  return Object.entries(map).map(([day, items]) => ({ day, items }));
}

Page({
  data: {
    __themeClass: "theme-dark",
    list: HISTORY as HistoryItem[],
    groups: group(HISTORY) as Group[],
  },
  onLoad() { applyTheme(this); },
  onShow() { applyTheme(this); },
  clearAll() {
    wx.showModal({
      title: "清空浏览历史",
      content: "确定清空全部记录？",
      confirmColor: "#b7382e",
      success: (res) => {
        if (res.confirm) {
          this.setData({ list: [], groups: [] });
          toast("已清空");
        }
      },
    });
  },
});
