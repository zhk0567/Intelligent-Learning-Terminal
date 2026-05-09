import { applyTheme } from "../../utils/theme";
import { assetUrl } from "../../utils/assetUrl";

Page({
  data: {
    __themeClass: "theme-dark",
    logoUrl: assetUrl("/images/logo.jpg"),
  },
  onLoad() {
    applyTheme(this);
    setTimeout(() => {
      wx.redirectTo({ url: "/pkg/login/index" });
    }, 1600);
  },
  onShow() {
    applyTheme(this);
  },
});
