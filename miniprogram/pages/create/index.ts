import { applyTheme } from "../../utils/theme";
import { CHALLENGES, POSTS } from "../../data/post";

Page({
  data: {
    __themeClass: "theme-dark",
    challenges: CHALLENGES,
    posts: POSTS,
  },
  onLoad() { applyTheme(this); },
  onShow() {
    applyTheme(this);
    if (typeof this.getTabBar === "function" && this.getTabBar()) {
      this.getTabBar().setData({ selected: 2 });
    }
  },
  goPublish() {
    wx.navigateTo({ url: "/pages/publish-work/index" });
  },
  goChallenge() {
    wx.navigateTo({ url: "/pages/challenge/index" });
  },
  goCreator() {
    wx.navigateTo({ url: "/pages/creator-profile/index?id=u1" });
  },
  goWork(e: any) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/work-detail/index?id=${id}` });
  },
});
