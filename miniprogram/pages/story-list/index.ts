import { applyTheme } from "../../utils/theme";
import { STORIES } from "../../data/story";

Page({
  data: {
    __themeClass: "theme-dark",
    stories: STORIES,
  },
  onLoad() { applyTheme(this); },
  onShow() { applyTheme(this); },
  goDetail(e: any) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/story-detail/index?id=${id}` });
  },
});
