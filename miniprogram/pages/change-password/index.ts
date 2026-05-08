import { applyTheme } from "../../utils/theme";
import { toast } from "../../utils/toast";

Page({
  data: {
    __themeClass: "theme-dark",
    oldP: "",
    newP: "",
    confirm: "",
    show: false,
  },
  onLoad() { applyTheme(this); },
  onShow() { applyTheme(this); },
  onOld(e: any) { this.setData({ oldP: e.detail.value }); },
  onNew(e: any) { this.setData({ newP: e.detail.value }); },
  onConfirm(e: any) { this.setData({ confirm: e.detail.value }); },
  toggleEye() { this.setData({ show: !this.data.show }); },
  submit() {
    if (!this.data.oldP || !this.data.newP) return toast("请填写完整");
    if (this.data.newP.length < 8) return toast("新密码至少 8 位");
    if (this.data.newP !== this.data.confirm) return toast("两次密码不一致");
    if (this.data.newP === this.data.oldP) return toast("新密码不能与旧密码相同");
    toast("密码已更新");
    setTimeout(() => wx.navigateBack({ delta: 1 }), 600);
  },
});
