import { applyTheme } from "../../utils/theme";
import { sessionStore } from "../../stores/sessionStore";
import { APP_IMAGES } from "../../data/appImages";

Page({
  data: {
    __themeClass: "theme-dark",
    user: sessionStore.get().user,
    avatar: APP_IMAGES.banner1,
  },
  _unsubSession: null as any,
  onLoad() {
    applyTheme(this);
    this._unsubSession = sessionStore.bind(this, (s) => ({ user: s.user }));
  },
  onShow() {
    applyTheme(this);
    if (typeof this.getTabBar === "function" && this.getTabBar()) {
      this.getTabBar().setData({ selected: 4 });
    }
  },
  onUnload() {
    this._unsubSession?.();
  },
  goSetting() { wx.navigateTo({ url: "/pages/setting/index" }); },
  goEdit() { wx.navigateTo({ url: "/pages/edit-profile/index" }); },
  goFav() { wx.navigateTo({ url: "/pages/favorite/index" }); },
  goHistory() { wx.navigateTo({ url: "/pages/history/index" }); },
  goWorks() { wx.navigateTo({ url: "/pages/my-works/index" }); },
  goLearning() { wx.navigateTo({ url: "/pages/my-learning/index" }); },
});
