import { applyTheme } from "../../utils/theme";
import { sessionStore } from "../../stores/sessionStore";
import { toast } from "../../utils/toast";

Page({
  data: {
    __themeClass: "theme-dark",
    phone: "",
    code: "",
    seconds: 0,
  },
  _timer: null as any,
  onLoad() {
    applyTheme(this);
  },
  onShow() {
    applyTheme(this);
  },
  onUnload() {
    if (this._timer) clearInterval(this._timer);
  },
  onPhone(e: any) {
    this.setData({ phone: (e.detail.value || "").replace(/\D/g, "").slice(0, 11) });
  },
  onCode(e: any) {
    this.setData({ code: (e.detail.value || "").replace(/\D/g, "").slice(0, 6) });
  },
  sendCode() {
    if (this.data.seconds > 0) return;
    if (!/^1\d{10}$/.test(this.data.phone)) {
      toast("请输入 11 位有效手机号");
      return;
    }
    this.setData({ seconds: 60 });
    toast("验证码已发送（123456）");
    if (this._timer) clearInterval(this._timer);
    this._timer = setInterval(() => {
      const next = this.data.seconds - 1;
      if (next <= 0) {
        clearInterval(this._timer);
        this._timer = null;
        this.setData({ seconds: 0 });
      } else {
        this.setData({ seconds: next });
      }
    }, 1000);
  },
  submit() {
    if (this.data.code !== "123456") {
      toast("验证码错误，演示请输入 123456");
      return;
    }
    sessionStore.actions.loginAs("phone");
    wx.switchTab({ url: "/pages/music/index" });
  },
  goAgreement() {
    wx.navigateTo({ url: "/pkg/webview/index?title=用户协议" });
  },
  goPrivacy() {
    wx.navigateTo({ url: "/pkg/webview/index?title=隐私政策" });
  },
});
