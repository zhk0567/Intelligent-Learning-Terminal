const data = require('../../utils/mock/archive.js');
const { syncPageTheme } = require('../../utils/theme.js');
Page({
  data: {
    themeClass: '',
    items: [],
  },
  onShow() {
    syncPageTheme(this);
    this.setData({ items: data.items || [] });
  },
  onItemOpen(e) {
    const id = e.currentTarget.dataset.id;
    if (!id) return;
    wx.navigateTo({ url: '/pages/music/archive-detail?id=' + encodeURIComponent(id) });
  },
});
