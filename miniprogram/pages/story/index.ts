import { applyTheme } from "../../utils/theme";
import { STORIES } from "../../data/story";

const TABS = ["推荐", "古琴", "敦煌", "刺绣", "皮影", "技艺"];

Page({
  data: {
    __themeClass: "theme-dark",
    tabs: TABS,
    activeIdx: 0,
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
  onPickTab(e: any) {
    this.setData({ activeIdx: e.currentTarget.dataset.i });
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
