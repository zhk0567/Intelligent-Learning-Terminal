import { applyTheme, getTheme, setTheme, type ThemeMode } from "../../utils/theme";
import { sessionStore } from "../../stores/sessionStore";
import { toast } from "../../utils/toast";

const THEME_OPTIONS = [
  { id: "dark", label: "深色", desc: "默认主题，护眼且更具沉浸感" },
  { id: "light", label: "浅色", desc: "明亮风格，适合白天与户外" },
];

Page({
  data: {
    __themeClass: "theme-dark",
    themeOptions: THEME_OPTIONS,
    themeLabel: "深色",
    themeOpen: false,
    tempMode: "dark" as ThemeMode,
  },
  onLoad() {
    applyTheme(this);
    const mode = getTheme();
    this.setData({ themeLabel: mode === "dark" ? "深色" : "浅色", tempMode: mode });
  },
  onShow() {
    applyTheme(this);
    const mode = getTheme();
    this.setData({ themeLabel: mode === "dark" ? "深色" : "浅色" });
  },
  goEdit() { wx.navigateTo({ url: "/pages/edit-profile/index" }); },
  goChangePwd() { wx.navigateTo({ url: "/pages/change-password/index" }); },
  goAgreement() { wx.navigateTo({ url: "/pages/webview/index?title=用户协议" }); },
  goPrivacy() { wx.navigateTo({ url: "/pages/webview/index?title=隐私政策" }); },
  goAbout() { wx.navigateTo({ url: "/pages/about/index" }); },
  onNotifyTip() { toast("演示版本暂未开放"); },
  onLangTip() { toast("仅支持简体中文"); },
  openTheme() {
    this.setData({ themeOpen: true, tempMode: getTheme() });
  },
  closeTheme() { this.setData({ themeOpen: false }); },
  noop() {},
  pickTheme(e: any) {
    this.setData({ tempMode: e.currentTarget.dataset.id });
  },
  confirmTheme() {
    const next = this.data.tempMode;
    if (next !== getTheme()) {
      setTheme(next);
      applyTheme(this);
      toast(`已切换到「${next === "dark" ? "深色" : "浅色"}」`);
    }
    this.setData({
      themeOpen: false,
      themeLabel: next === "dark" ? "深色" : "浅色",
    });
  },
  onLogout() {
    wx.showModal({
      title: "确认退出？",
      content: "退出后将清除当前会话，下次需重新登录。",
      confirmText: "退出",
      confirmColor: "#b7382e",
      success: (res) => {
        if (res.confirm) {
          sessionStore.actions.logout();
          wx.reLaunch({ url: "/pages/login/index" });
        }
      },
    });
  },
});
