const { syncPageTheme } = require('../../utils/theme.js');

Page({
  data: {
    themeClass: '',
    style: '',
    mood: '',
    bpm: '',
  },
  onShow() {
    syncPageTheme(this);
  },
  onStyle(e) {
    this.setData({ style: e.detail.value });
  },
  onMood(e) {
    this.setData({ mood: e.detail.value });
  },
  onBpm(e) {
    this.setData({ bpm: e.detail.value });
  },
  onGen() {
    wx.showToast({
      title: '模板生成：后续可接模型服务',
      icon: 'none',
      duration: 2200,
    });
  },
});
