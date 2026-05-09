import { applyTheme } from "../../utils/theme";
import { STORIES } from "../../data/story";

Page({
  data: {
    __themeClass: "theme-dark",
    features: STORIES.slice(0, 2),
    rest: STORIES.slice(2),
  },
  onLoad() {
    applyTheme(this);
  },
  onShow() {
    applyTheme(this);
    if (typeof this.getTabBar === "function" && this.getTabBar()) {
      this.getTabBar().setData({ selected: 1 });
    }
  },
  goList() {
    wx.navigateTo({ url: "/pages/story-list/index" });
  },
  goSearch() {
    wx.navigateTo({ url: "/pages/search/index" });
  },
  goDetail(e: any) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/story-detail/index?id=${id}` });
  },
});
