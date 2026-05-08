import { applyTheme } from "../../utils/theme";
import { ALL_PRODUCTS } from "../../data/shop";
import { cartStore } from "../../stores/cartStore";
import { toast } from "../../utils/toast";

interface RawItem { productId: string; count: number; selected: boolean; }
interface UIItem extends RawItem { product: any; }

const INIT: RawItem[] = [
  { productId: "s1", count: 2, selected: true },
  { productId: "s3", count: 1, selected: true },
  { productId: "s6", count: 1, selected: false },
];

function build(items: RawItem[]): UIItem[] {
  return items.map((it) => ({ ...it, product: ALL_PRODUCTS.find((p) => p.id === it.productId)! }));
}

function totalOf(items: RawItem[]): number {
  return items.filter((i) => i.selected).reduce((s, i) => {
    const p = ALL_PRODUCTS.find((x) => x.id === i.productId)!;
    return s + p.price * i.count;
  }, 0);
}

Page({
  data: {
    __themeClass: "theme-dark",
    raw: INIT as RawItem[],
    items: build(INIT) as UIItem[],
    totalText: totalOf(INIT).toFixed(2),
    allSel: INIT.length > 0 && INIT.every((i) => i.selected),
  },
  onLoad() { applyTheme(this); },
  onShow() { applyTheme(this); },
  _refresh(raw: RawItem[]) {
    this.setData({
      raw,
      items: build(raw),
      totalText: totalOf(raw).toFixed(2),
      allSel: raw.length > 0 && raw.every((i) => i.selected),
    });
  },
  toggleSel(e: any) {
    const id = e.currentTarget.dataset.id;
    const next = this.data.raw.map((x) => x.productId === id ? { ...x, selected: !x.selected } : x);
    this._refresh(next);
  },
  toggleAll() {
    const next = this.data.raw.map((x) => ({ ...x, selected: !this.data.allSel }));
    this._refresh(next);
  },
  inc(e: any) {
    const id = e.currentTarget.dataset.id;
    this._refresh(this.data.raw.map((x) => x.productId === id ? { ...x, count: x.count + 1 } : x));
  },
  dec(e: any) {
    const id = e.currentTarget.dataset.id;
    this._refresh(this.data.raw.map((x) => x.productId === id ? { ...x, count: Math.max(1, x.count - 1) } : x));
  },
  remove(e: any) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: "确认删除",
      content: "确定要从购物车移除该商品？",
      confirmColor: "#b7382e",
      success: (res) => {
        if (res.confirm) {
          this._refresh(this.data.raw.filter((x) => x.productId !== id));
        }
      },
    });
  },
  checkout() {
    const total = totalOf(this.data.raw);
    if (total === 0) {
      toast("请选择商品");
      return;
    }
    cartStore.actions.clear();
    wx.navigateTo({ url: "/pages/order/index" });
  },
});
