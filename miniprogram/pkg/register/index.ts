import { applyTheme } from "../../utils/theme";
import { sessionStore } from "../../stores/sessionStore";
import { toast } from "../../utils/toast";

function scorePwd(p: string): number {
  let s = 0;
  if (p.length >= 8) s++;
  if (/[a-z]/.test(p) && /[A-Z]/.test(p)) s++;
  if (/\d/.test(p)) s++;
  if (/[^a-zA-Z0-9]/.test(p)) s++;
  return s;
}

Page({
  data: {
    __themeClass: "theme-dark",
    name: "",
    phone: "",
    pwd: "",
    confirm: "",
    show: false,
    strength: 0,
    bars: ["auth-bar-rose", "auth-bar-amber", "auth-bar-emerald", "auth-bar-emerald"],
  },
  onLoad() {
    applyTheme(this);
  },
  onShow() {
    applyTheme(this);
  },
  onName(e: any) {
    this.setData({ name: e.detail.value });
  },
  onPhone(e: any) {
    this.setData({ phone: (e.detail.value || "").replace(/\D/g, "").slice(0, 11) });
  },
  onPwd(e: any) {
    const v = e.detail.value;
    this.setData({ pwd: v, strength: scorePwd(v) });
  },
  onConfirm(e: any) {
    this.setData({ confirm: e.detail.value });
  },
  toggleEye() {
    this.setData({ show: !this.data.show });
  },
  submit() {
    if (!this.data.name || !this.data.phone || !this.data.pwd) {
      toast("请完整填写信息");
      return;
    }
    if (this.data.pwd !== this.data.confirm) {
      toast("两次密码不一致");
      return;
    }
    if (this.data.strength < 2) {
      toast("密码强度过低，请使用大小写+数字");
      return;
    }
    sessionStore.actions.setUser({ name: this.data.name, phone: this.data.phone });
    sessionStore.actions.loginAs("password");
    wx.switchTab({ url: "/pages/music/index" });
  },
});
