const { syncPageTheme } = require('../../utils/theme.js');

Page({
  data: {
    themeClass: '',
    activeTheme: 'tech',
  },
  onShow() {
    const app = getApp();
    const t = app.globalData.theme || 'tech';
    this.setData({ activeTheme: t });
    syncPageTheme(this);
  },
  onThemeTap(e) {
    const name = e.currentTarget.dataset.theme;
    const app = getApp();
    app.setTheme(name);
    this.setData({ activeTheme: app.globalData.theme });
    syncPageTheme(this);
    wx.showToast({ title: '已切换主题', icon: 'none' });
  },
  onProfileRow(e) {
    const pf = e.currentTarget.dataset.pf;
    if (pf === 'fav') {
      wx.navigateTo({ url: '/pages/profile/fav' });
      return;
    }
    if (pf === 'hist') {
      wx.navigateTo({ url: '/pages/profile/hist' });
      return;
    }
    if (pf === 'about') {
      wx.navigateTo({ url: '/pages/profile/about' });
      return;
    }
    if (pf === 'licenses') {
      wx.navigateTo({ url: '/pages/profile/licenses' });
      return;
    }
    const labels = {
      notif: '消息与通知',
      theme: '主题设置',
      settings: '设置',
    };
    wx.showToast({
      title: (labels[pf] || '功能') + '（演示）',
      icon: 'none',
    });
  },
});
