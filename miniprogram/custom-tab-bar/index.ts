import { applyTheme, subscribeTheme, getTheme } from "../utils/theme";
import { playerStore } from "../stores/playerStore";
import { cartStore } from "../stores/cartStore";
import { TRACKS } from "../data/tracks";

const LIST = [
  { pagePath: "pages/music/index", text: "音乐", icon: "music" },
  { pagePath: "pages/story/index", text: "故事", icon: "book-open" },
  { pagePath: "pages/create/index", text: "创作", icon: "edit" },
  { pagePath: "pages/shop/index", text: "商城", icon: "shop" },
  { pagePath: "pages/me/index", text: "我的", icon: "user" },
];

Component({
  options: { addGlobalClass: true },
  data: {
    __themeClass: "theme-dark",
    selected: 0,
    list: LIST,
    track: TRACKS[0],
    isPlaying: false,
    cart: 0,
  },
  lifetimes: {
    attached() {
      this.setData({ __themeClass: `theme-${getTheme()}` });
      (this as any)._unsubTheme = subscribeTheme((m) => {
        this.setData({ __themeClass: `theme-${m}` });
      });
      (this as any)._unsubPlayer = playerStore.bind(this, (s) => ({
        track: TRACKS[s.currentIndex],
        isPlaying: s.isPlaying,
      }));
      (this as any)._unsubCart = cartStore.bind(this, (s) => ({ cart: s.pending }));
    },
    detached() {
      (this as any)._unsubTheme?.();
      (this as any)._unsubPlayer?.();
      (this as any)._unsubCart?.();
    },
  },
  methods: {
    onTap(e: any) {
      const i = e.currentTarget.dataset.i as number;
      const path = "/" + LIST[i].pagePath;
      if (this.data.selected === i) return;
      wx.switchTab({
        url: path,
        success: () => this.setData({ selected: i }),
      });
    },
    goPlayer() {
      wx.navigateTo({ url: "/pages/player/index" });
    },
    onToggle() {
      playerStore.actions.toggle();
    },
    onPrev() {
      playerStore.actions.prev();
    },
    onNext() {
      playerStore.actions.next();
    },
  },
});
