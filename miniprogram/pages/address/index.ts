import { applyTheme } from "../../utils/theme";
import { ADDRESSES, type Address } from "../../data/address";
import { toast } from "../../utils/toast";

Page({
  data: {
    __themeClass: "theme-dark",
    list: [...ADDRESSES] as Address[],
    open: false,
    sheetTitle: "新增地址",
    editing: null as Address | null,
  },
  onLoad() { applyTheme(this); },
  onShow() { applyTheme(this); },
  startCreate() {
    this.setData({
      open: true,
      sheetTitle: "新增地址",
      editing: { id: `a${Date.now()}`, name: "", phone: "", detail: "", isDefault: false },
    });
  },
  startEdit(e: any) {
    const id = e.currentTarget.dataset.id;
    const target = this.data.list.find((a) => a.id === id);
    if (!target) return;
    this.setData({ open: true, sheetTitle: "编辑地址", editing: { ...target } });
  },
  del(e: any) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: "删除地址",
      content: "确定要删除该地址？",
      confirmColor: "#b7382e",
      success: (res) => {
        if (res.confirm) {
          this.setData({ list: this.data.list.filter((x) => x.id !== id) });
          toast("已删除");
        }
      },
    });
  },
  setDefault(e: any) {
    const id = e.currentTarget.dataset.id;
    this.setData({ list: this.data.list.map((x) => ({ ...x, isDefault: x.id === id })) });
    toast("已设为默认");
  },
  onInput(e: any) {
    const k = e.currentTarget.dataset.k;
    if (!this.data.editing) return;
    let v = e.detail.value as string;
    if (k === "phone") v = v.replace(/[^\d\s]/g, "");
    this.setData({ editing: { ...this.data.editing, [k]: v } });
  },
  toggleDefault() {
    if (!this.data.editing) return;
    this.setData({ editing: { ...this.data.editing, isDefault: !this.data.editing.isDefault } });
  },
  save() {
    const cur = this.data.editing;
    if (!cur) return;
    if (!cur.name || !cur.phone || !cur.detail) {
      toast("请填写完整");
      return;
    }
    const exists = this.data.list.find((a) => a.id === cur.id);
    let next = exists ? this.data.list.map((a) => a.id === cur.id ? cur : a) : [...this.data.list, cur];
    if (cur.isDefault) {
      next = next.map((a) => ({ ...a, isDefault: a.id === cur.id }));
    }
    this.setData({ list: next, open: false, editing: null });
    toast(exists ? "已更新" : "已添加");
  },
  onCloseSheet() {
    this.setData({ open: false, editing: null });
  },
});
