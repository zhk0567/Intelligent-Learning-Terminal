import { applyTheme } from "../../utils/theme";
import { ALL_PRODUCTS } from "../../data/shop";
import { cartStore } from "../../stores/cartStore";
import { toast } from "../../utils/toast";

const SPECS = ["标准款", "礼盒装", "联名限定"];

Page({
  data: {
    __themeClass: "theme-dark",
    product: ALL_PRODUCTS[0],
    fav: false,
    tagsText: "",
    sheetOpen: false,
    sheetMode: "cart" as "cart" | "buy",
    sheetTitle: "选择规格",
    spec: SPECS[0],
    count: 1,
    specs: SPECS,
  },
  onLoad(query: any) {
    applyTheme(this);
    const id = query?.id;
    const product = ALL_PRODUCTS.find((p) => p.id === id) || ALL_PRODUCTS[0];
    this.setData({
      product,
      tagsText: product.tags.join(" / "),
    });
  },
  onShow() { applyTheme(this); },
  toggleFav() {
    this.setData({ fav: !this.data.fav });
  },
  goCart() {
    wx.navigateTo({ url: "/pkg/cart/index" });
  },
  openCart() {
    this.setData({ sheetOpen: true, sheetMode: "cart", sheetTitle: "选择规格" });
  },
  openBuy() {
    this.setData({ sheetOpen: true, sheetMode: "buy", sheetTitle: "确认下单" });
  },
  closeSheet() {
    this.setData({ sheetOpen: false });
  },
  pickSpec(e: any) {
    this.setData({ spec: e.currentTarget.dataset.s });
  },
  inc() {
    this.setData({ count: this.data.count + 1 });
  },
  dec() {
    this.setData({ count: Math.max(1, this.data.count - 1) });
  },
  submit() {
    if (this.data.sheetMode === "cart") {
      cartStore.actions.add(this.data.count);
      toast(`${this.data.count} 件 ${this.data.spec} 已加入购物车`);
    } else {
      toast("已下单，等待付款（演示）");
      setTimeout(() => wx.navigateTo({ url: "/pkg/order/index" }), 600);
    }
    this.setData({ sheetOpen: false });
  },
  previewMain() {
    const u = this.data.product.detailHeroSrc || this.data.product.imageSrc;
    if (!u) return;
    wx.previewImage({ urls: [u] });
  },
  previewDetail1() {
    const p = this.data.product;
    const a = p.detailSrc1 || p.imageSrc;
    const b = p.detailSrc2 || p.imageSrc;
    if (!a) return;
    wx.previewImage({ urls: [a, b].filter(Boolean) });
  },
  previewDetail2() {
    const p = this.data.product;
    const a = p.detailSrc1 || p.imageSrc;
    const b = p.detailSrc2 || p.imageSrc;
    if (!b) return;
    wx.previewImage({ urls: [b, a].filter(Boolean), current: b });
  },
  onShare() { toast("分享链接已复制"); },
});
