import { applyTheme } from "../../utils/theme";
import { ALBUMS, HOT_BANNERS } from "../../data/music";
import { playerStore } from "../../stores/playerStore";

Page({
  data: {
    __themeClass: "theme-dark",
    banners: HOT_BANNERS,
    bannerIdx: 0,
    hot: ALBUMS.filter((a) => a.category === "hot"),
    select: ALBUMS.filter((a) => a.category === "select"),
    guess: ALBUMS.filter((a) => a.category === "guess"),
    basic: ALBUMS.filter((a) => a.category === "basic"),
  },
  onLoad() {
    applyTheme(this);
  },
  onShow() {
    applyTheme(this);
    if (typeof this.getTabBar === "function" && this.getTabBar()) {
      this.getTabBar().setData({ selected: 0 });
    }
  },
  onBannerChange(e: any) {
    this.setData({ bannerIdx: e.detail.current });
  },
  goSearch() {
    wx.navigateTo({ url: "/pages/search/index" });
  },
  goDetail(e: any) {
    const cat = e.currentTarget.dataset.cat;
    wx.navigateTo({ url: `/pages/detail/index?id=${cat}` });
  },
  playAlbum(e: any) {
    const cat = e.currentTarget.dataset.cat;
    const startIdx = cat === "select" ? 1 : cat === "guess" ? 2 : 0;
    playerStore.actions.setIndex(startIdx);
    wx.navigateTo({ url: "/pages/player/index" });
  },
});
