const catalog = require('../../utils/mock/mall-catalog.js');
const { syncPageTheme } = require('../../utils/theme.js');

function filterByKw(products, kw) {
  const k = (kw || '').trim().toLowerCase();
  if (!k) return products.slice();
  return products.filter(function (p) {
    return (p.title || '').toLowerCase().indexOf(k) >= 0;
  });
}

Page({
  data: {
    themeClass: '',
    sections: [],
    kw: '',
    displayProducts: [],
    filteredCount: 0,
  },
  onShow() {
    syncPageTheme(this);
    this.setData({ sections: catalog.sections || [] });
    this.applyProducts();
  },
  applyProducts() {
    const all = catalog.products || [];
    const displayProducts = filterByKw(all, this.data.kw);
    this.setData({
      displayProducts: displayProducts,
      filteredCount: displayProducts.length,
    });
  },
  onKwInput(e) {
    this.setData({ kw: e.detail.value || '' });
    this.applyProducts();
  },
  onZoneTap(e) {
    const sid = e.currentTarget.dataset.section;
    if (!sid) return;
    wx.navigateTo({
      url: '/pages/mall/section?section=' + encodeURIComponent(sid),
    });
  },
});
