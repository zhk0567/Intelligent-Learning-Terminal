import { applyTheme } from "../../utils/theme";
import { TRACKS } from "../../data/tracks";
import { ALL_PRODUCTS } from "../../data/shop";
import { STORIES } from "../../data/story";
import { playerStore } from "../../stores/playerStore";

const TABS = ["全部", "音乐", "故事", "商品"] as const;

Page({
  data: {
    __themeClass: "theme-dark",
    q: "",
    tab: "全部" as (typeof TABS)[number],
    tabs: TABS,
    music: [] as any[],
    story: [] as any[],
    goods: [] as any[],
    total: 0,
  },
  onLoad(query: any) {
    applyTheme(this);
    const q = decodeURIComponent(query?.q || "").trim();
    this.setData({ q });
    this.recompute(q);
  },
  onShow() { applyTheme(this); },
  recompute(q: string) {
    const kw = q.toLowerCase();
    const music = TRACKS.filter(
      (t) => t.title.indexOf(q) >= 0 || t.artist.indexOf(q) >= 0 || t.album.indexOf(q) >= 0,
    );
    const story = STORIES.filter(
      (s) =>
        s.title.indexOf(q) >= 0 ||
        s.author.indexOf(q) >= 0 ||
        s.tags.some((t) => t.indexOf(q) >= 0),
    );
    const goods = ALL_PRODUCTS.filter(
      (p) =>
        p.name.indexOf(q) >= 0 ||
        p.tags.some((t) => t.indexOf(q) >= 0) ||
        p.description.toLowerCase().indexOf(kw) >= 0,
    );
    this.setData({
      music,
      story,
      goods,
      total: music.length + story.length + goods.length,
    });
  },
  pickTab(e: any) {
    this.setData({ tab: e.currentTarget.dataset.t });
  },
  playTrack(e: any) {
    const id = e.currentTarget.dataset.id;
    const idx = TRACKS.findIndex((x) => x.id === id);
    if (idx >= 0) {
      playerStore.actions.setIndex(idx);
      wx.navigateTo({ url: "/pages/player/index" });
    }
  },
  goStory(e: any) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/story-detail/index?id=${id}` });
  },
  goShop(e: any) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/shop-detail/index?id=${id}` });
  },
});
