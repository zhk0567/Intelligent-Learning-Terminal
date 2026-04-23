const catalog = require('../../utils/mock/mall-catalog.js');
const { syncPageTheme } = require('../../utils/theme.js');

Page({
  data: {
    themeClass: '',
    section: null,
    products: [],
  },
  onShow() {
    syncPageTheme(this);
  },
  onLoad(options) {
    const sid = decodeURIComponent(options.section || '');
    const section = (catalog.sections || []).find(function (s) {
      return s.id === sid;
    });
    const products = (catalog.products || []).filter(function (p) {
      return p.section === sid;
    });
    this.setData({
      section: section || null,
      products: products,
    });
    if (section) {
      wx.setNavigationBarTitle({ title: section.title });
    }
  },
});
