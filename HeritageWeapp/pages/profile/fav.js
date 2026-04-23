const storage = require('../../utils/storage.js');
const { syncPageTheme } = require('../../utils/theme.js');
const { playTrack } = require('../../utils/audio.js');

Page({
  data: {
    themeClass: '',
    list: [],
  },
  onShow() {
    syncPageTheme(this);
    this.setData({ list: storage.getFavorites() });
  },
  onRemove(e) {
    const id = e.currentTarget.dataset.id;
    if (!id) return;
    storage.removeFavorite(id);
    this.setData({ list: storage.getFavorites() });
    wx.showToast({ title: '已移除', icon: 'none' });
  },
  onPlay(e) {
    const track = e.currentTarget.dataset.track;
    const title = e.currentTarget.dataset.title;
    playTrack(track, title, '收藏');
  },
});
