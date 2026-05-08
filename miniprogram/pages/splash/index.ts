import { applyTheme } from "../../utils/theme";

Page({
  data: {
    __themeClass: "theme-dark",
  },
  onLoad() {
    applyTheme(this);
    setTimeout(() => {
      wx.redirectTo({ url: "/pages/login/index" });
    }, 1600);
  },
  onShow() {
    applyTheme(this);
  },
});
