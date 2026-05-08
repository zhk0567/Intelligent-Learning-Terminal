import { applyTheme } from "../../utils/theme";
import { TRACKS } from "../../data/tracks";
import { ALL_PRODUCTS } from "../../data/shop";
import { STORIES } from "../../data/story";

Page({
  data: {
    __themeClass: "theme-dark",
    tabs: ["音乐", "商品", "故事"],
    tab: "音乐",
    musicList: TRACKS.slice(0, 4),
    prodList: ALL_PRODUCTS.slice(0, 6),
    storyList: STORIES.slice(0, 4),
  },
  onLoad() { applyTheme(this); },
  onShow() { applyTheme(this); },
  pickTab(e: any) {
    this.setData({ tab: e.currentTarget.dataset.t });
  },
  goPlayer() {
    wx.navigateTo({ url: "/pages/player/index" });
  },
  goShop(e: any) {
    wx.navigateTo({ url: `/pages/shop-detail/index?id=${e.currentTarget.dataset.id}` });
  },
  goStory(e: any) {
    wx.navigateTo({ url: `/pages/story-detail/index?id=${e.currentTarget.dataset.id}` });
  },
});
