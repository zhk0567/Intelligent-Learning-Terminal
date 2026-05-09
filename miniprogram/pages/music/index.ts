import { applyTheme } from "../../utils/theme";
import { ALBUMS, HOT_BANNERS } from "../../data/music";
import { BASIC_LESSONS } from "../../data/lessons";
import { TRACKS } from "../../data/tracks";
import { playerStore } from "../../stores/playerStore";
import { fmtTime } from "../../utils/format";

Page({
  data: {
    __themeClass: "theme-dark",
    banners: HOT_BANNERS,
    bannerIdx: 0,
    hot: ALBUMS.filter((a) => a.category === "hot"),
    select: ALBUMS.filter((a) => a.category === "select"),
    guess: ALBUMS.filter((a) => a.category === "guess"),
    basic: BASIC_LESSONS.map((l) => ({ ...l, durationStr: fmtTime(l.durationSec) })),
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
    const albumId = e.currentTarget.dataset.id as string;
    const album = ALBUMS.find((a) => a.id === albumId);
    const firstId = album?.trackIds[0];
    const startIdx = firstId != null ? TRACKS.findIndex((t) => t.id === firstId) : 0;
    playerStore.actions.setIndex(startIdx < 0 ? 0 : startIdx);
    wx.navigateTo({ url: "/pages/player/index" });
  },
  openLesson(e: any) {
    const id = e.currentTarget.dataset.id as string;
    wx.navigateTo({ url: `/pages/lesson/index?id=${id}` });
  },
});
