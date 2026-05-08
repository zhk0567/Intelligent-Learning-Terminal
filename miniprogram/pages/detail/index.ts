import { applyTheme } from "../../utils/theme";
import { ALBUMS, Album } from "../../data/music";
import { TRACKS } from "../../data/tracks";
import { playerStore } from "../../stores/playerStore";

const CATEGORY_LABEL: Record<string, string> = {
  hot: "每日热门",
  select: "每日精选",
  guess: "猜你喜欢",
  basic: "基础学习",
};

Page({
  data: {
    __themeClass: "theme-dark",
    id: "hot",
    title: "每日热门",
    list: [] as Album[],
    cover: ALBUMS[0],
    trackTotal: 0,
    trackList: [] as any[],
  },
  onLoad(query: any) {
    applyTheme(this);
    const id = query?.id || "hot";
    const list = ALBUMS.filter((a) => a.category === id);
    const cover = list[0] || ALBUMS[0];
    const trackTotal = list.reduce((n, a) => n + a.trackIds.length, 0);
    const trackList: any[] = [];
    list.forEach((a) =>
      a.trackIds.forEach((tid, i) => {
        const tk = TRACKS.find((x) => x.id === tid);
        if (!tk) return;
        const idx = TRACKS.findIndex((x) => x.id === tid);
        trackList.push({
          key: `${a.id}-${tid}-${i}`,
          idx,
          id: tk.id,
          title: tk.title,
          artist: tk.artist,
          coverSrc: tk.coverSrc,
          albumTitle: a.title,
        });
      }),
    );
    this.setData({
      id,
      title: CATEGORY_LABEL[id] || "专辑详情",
      list,
      cover,
      trackTotal,
      trackList,
    });
  },
  onShow() { applyTheme(this); },
  playAll() {
    playerStore.actions.setIndex(0);
    wx.navigateTo({ url: "/pages/player/index" });
  },
  playTrack(e: any) {
    const i = e.currentTarget.dataset.i;
    playerStore.actions.setIndex(i);
    wx.navigateTo({ url: "/pages/player/index" });
  },
});
