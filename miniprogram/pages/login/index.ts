import { applyTheme } from "../../utils/theme";
import { sessionStore } from "../../stores/sessionStore";
import { toast } from "../../utils/toast";

Page({
  data: {
    __themeClass: "theme-dark",
    account: "",
    pwd: "",
    show: false,
  },
  onLoad() {
    applyTheme(this);
  },
  onShow() {
    applyTheme(this);
  },
  onAccount(e: any) {
    this.setData({ account: e.detail.value });
  },
  onPwd(e: any) {
    this.setData({ pwd: e.detail.value });
  },
  toggleEye() {
    this.setData({ show: !this.data.show });
  },
  submit() {
    if (!this.data.account.trim() || !this.data.pwd) {
      toast("请输入账号和密码");
      return;
    }
    sessionStore.actions.loginAs("password");
    wx.switchTab({ url: "/pages/music/index" });
  },
  loginGuest() {
    sessionStore.actions.loginAs("guest");
    wx.switchTab({ url: "/pages/music/index" });
  },
  goPhone() {
    wx.navigateTo({ url: "/pages/phone-login/index" });
  },
  goRegister() {
    wx.navigateTo({ url: "/pages/register/index" });
  },
  goForgot() {
    wx.navigateTo({ url: "/pages/change-password/index" });
  },
  goAbout() {
    wx.navigateTo({ url: "/pages/about/index" });
  },
});
