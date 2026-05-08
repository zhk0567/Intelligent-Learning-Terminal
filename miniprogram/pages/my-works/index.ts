import { applyTheme } from "../../utils/theme";
import { POSTS } from "../../data/post";

Page({
  data: {
    __themeClass: "theme-dark",
    tabs: ["全部", "已发布", "草稿"],
    tab: "全部",
    posts: POSTS,
  },
  onLoad() { applyTheme(this); },
  onShow() { applyTheme(this); },
  pickTab(e: any) {
    this.setData({ tab: e.currentTarget.dataset.t });
  },
  goPublish() {
    wx.navigateTo({ url: "/pages/publish-work/index" });
  },
  goWork(e: any) {
    wx.navigateTo({ url: `/pages/work-detail/index?id=${e.currentTarget.dataset.id}` });
  },
});
