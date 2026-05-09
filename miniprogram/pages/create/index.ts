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
    wx.navigateTo({ url: "/pkg/publish-work/index" });
  },
  goChallenge() {
    wx.navigateTo({ url: "/pkg/challenge/index" });
  },
  goCreator() {
    wx.navigateTo({ url: "/pkg/creator-profile/index?id=u1" });
  },
  goWork(e: any) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pkg/work-detail/index?id=${id}` });
  },
});
