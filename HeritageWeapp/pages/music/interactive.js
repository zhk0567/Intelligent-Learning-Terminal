const hub = require('../../utils/mock/interactive.js');
const { syncPageTheme } = require('../../utils/theme.js');

Page({
  data: {
    themeClass: '',
    hub: { title: '', subtitle: '', modules: [] },
  },
  onShow() {
    syncPageTheme(this);
    this.setData({ hub: hub });
  },
  onModuleTap(e) {
    const id = e.currentTarget.dataset.id;
    if (id === 'compose') {
      wx.navigateTo({ url: '/pages/music/compose' });
    } else if (id === 'review') {
      wx.navigateTo({ url: '/pages/music/review' });
    } else {
      wx.showToast({ title: '演示', icon: 'none' });
    }
  },
});
