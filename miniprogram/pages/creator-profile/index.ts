import { applyTheme } from "../../utils/theme";
import { POSTS } from "../../data/post";
import { APP_IMAGES } from "../../data/appImages";
import { toast } from "../../utils/toast";

Page({
  data: {
    __themeClass: "theme-dark",
    seed: "u1",
    initial: "U",
    bannerSrc: APP_IMAGES.banner3,
    works: POSTS.slice(0, 4),
  },
  onLoad(query: any) {
    applyTheme(this);
    const seed = query?.id || "u1";
    this.setData({ seed, initial: seed[0].toUpperCase() });
  },
  onShow() { applyTheme(this); },
  follow() { toast("已关注"); },
  message() { toast("私信功能即将开放"); },
  goWork(e: any) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/work-detail/index?id=${id}` });
  },
});
