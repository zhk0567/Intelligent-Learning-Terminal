import { applyTheme } from "../../utils/theme";
import { sessionStore } from "../../stores/sessionStore";
import { APP_IMAGES } from "../../data/appImages";
import { toast } from "../../utils/toast";

Page({
  data: {
    __themeClass: "theme-dark",
    avatar: APP_IMAGES.banner1,
    form: { name: "", phone: "", bio: "" },
  },
  onLoad() {
    applyTheme(this);
    const u = sessionStore.get().user;
    this.setData({ form: { name: u.name, phone: u.phone, bio: u.bio } });
  },
  onShow() { applyTheme(this); },
  onInput(e: any) {
    const k = e.currentTarget.dataset.k;
    this.setData({ form: { ...this.data.form, [k]: e.detail.value } });
  },
  onChangeAvatar() {
    wx.chooseImage({
      count: 1,
      success: () => toast("演示版本不会保存头像"),
      fail: () => {},
    });
  },
  save() {
    sessionStore.actions.setUser({ ...this.data.form });
    toast("已保存");
    setTimeout(() => wx.navigateBack({ delta: 1 }), 400);
  },
});
