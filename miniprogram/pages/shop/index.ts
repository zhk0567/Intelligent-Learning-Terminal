import { applyTheme } from "../../utils/theme";
import {
  ALL_PRODUCTS,
  filterByCategory,
  SHOP_BANNERS,
  SHOP_CATEGORY_TABS,
  ShopCategoryId,
} from "../../data/shop";
import { cartStore } from "../../stores/cartStore";
import { toast } from "../../utils/toast";

const PAGE_SIZE = 8;

Page({
  data: {
    __themeClass: "theme-dark",
    banners: SHOP_BANNERS,
    bannerIdx: 0,
    categoryTabs: SHOP_CATEGORY_TABS,
    active: "all" as ShopCategoryId,
    page: 1,
    list: [] as any[],
    hasMore: false,
    skeletonOn: true,
    cart: 0,
  },
  _unsubCart: null as any,
  _skTimer: null as any,
  onLoad() {
    applyTheme(this);
    this._unsubCart = cartStore.bind(this, (s) => ({ cart: s.pending }));
    this.recompute();
  },
  onShow() {
    applyTheme(this);
    if (typeof this.getTabBar === "function" && this.getTabBar()) {
      this.getTabBar().setData({ selected: 3 });
    }
  },
  onUnload() {
    this._unsubCart?.();
    if (this._skTimer) clearTimeout(this._skTimer);
  },
  recompute() {
    this.setData({ skeletonOn: true });
    if (this._skTimer) clearTimeout(this._skTimer);
    this._skTimer = setTimeout(() => {
      const filtered = filterByCategory(ALL_PRODUCTS, this.data.active);
      const list = filtered.slice(0, this.data.page * PAGE_SIZE);
      this.setData({ list, hasMore: list.length < filtered.length, skeletonOn: false });
    }, 300);
  },
  onBannerChange(e: any) {
    this.setData({ bannerIdx: e.detail.current });
  },
  onPickCat(e: any) {
    const id = e.currentTarget.dataset.id as ShopCategoryId;
    if (id === this.data.active) return;
    this.setData({ active: id, page: 1 });
    this.recompute();
  },
  loadMore() {
    this.setData({ page: this.data.page + 1 });
    this.recompute();
  },
  goSearch() {
    wx.navigateTo({ url: "/pkg/search/index" });
  },
  goCart() {
    wx.navigateTo({ url: "/pkg/cart/index" });
  },
  goDetail(e: any) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pkg/shop-detail/index?id=${id}` });
  },
  addCart() {
    cartStore.actions.add(1);
    toast("已加入购物车");
  },
});
