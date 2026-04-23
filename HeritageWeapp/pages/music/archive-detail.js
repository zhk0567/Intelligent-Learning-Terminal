const data = require('../../utils/mock/archive.js');
const { syncPageTheme } = require('../../utils/theme.js');
const { playTrack } = require('../../utils/audio.js');

Page({
  data: {
    themeClass: '',
    item: null,
  },
  onShow() {
    syncPageTheme(this);
  },
  onLoad(options) {
    const id = decodeURIComponent(options.id || '');
    const item = (data.items || []).find(function (x) {
      return x.id === id;
    });
    this.setData({ item: item || null });
    if (item) {
      wx.setNavigationBarTitle({ title: '资料详情' });
    }
  },
  onPlay() {
    const it = this.data.item;
    if (!it || !it.relatedTrackId) {
      wx.showToast({ title: '暂无关联曲目', icon: 'none' });
      return;
    }
    playTrack(it.relatedTrackId, it.title, '资料库');
  },
  onStory() {
    wx.showToast({ title: '关联故事：演示中', icon: 'none' });
  },
});
