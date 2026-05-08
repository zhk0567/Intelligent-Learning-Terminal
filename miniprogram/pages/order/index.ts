import { applyTheme } from "../../utils/theme";
import { ALL_PRODUCTS } from "../../data/shop";
import { ADDRESSES } from "../../data/address";
import { toast } from "../../utils/toast";

const ORDER_RAW = [
  { productId: "s1", count: 2 },
  { productId: "s3", count: 1 },
];

function buildItems() {
  return ORDER_RAW.map((it) => ({ ...it, product: ALL_PRODUCTS.find((p) => p.id === it.productId)! }));
}

Page({
  data: {
    __themeClass: "theme-dark",
    items: buildItems(),
    addr: ADDRESSES.find((a) => a.isDefault) || ADDRESSES[0],
    pay: "alipay",
    methods: [
      { id: "alipay", label: "支付宝", short: "支", color: "#1677FF" },
      { id: "wechat", label: "微信支付", short: "微", color: "#10B981" },
      { id: "card", label: "银行卡", short: "银", color: "#A57C2C" },
    ],
    subtotalText: "0.00",
    shipText: "免运费",
    totalText: "0.00",
  },
  onLoad() {
    applyTheme(this);
    const subtotal = ORDER_RAW.reduce((s, i) => {
      const p = ALL_PRODUCTS.find((x) => x.id === i.productId)!;
      return s + p.price * i.count;
    }, 0);
    const ship = subtotal > 99 ? 0 : 8;
    this.setData({
      subtotalText: subtotal.toFixed(2),
      shipText: ship === 0 ? "免运费" : `¥${ship}`,
      totalText: (subtotal + ship).toFixed(2),
    });
  },
  onShow() { applyTheme(this); },
  pickPay(e: any) {
    this.setData({ pay: e.currentTarget.dataset.id });
  },
  goAddr() {
    wx.navigateTo({ url: "/pages/address/index" });
  },
  submit() {
    toast("订单已提交（演示）");
    setTimeout(() => wx.switchTab({ url: "/pages/me/index" }), 600);
  },
});
