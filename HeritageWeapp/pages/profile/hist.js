const storage = require('../../utils/storage.js');
const { syncPageTheme } = require('../../utils/theme.js');
const { playTrack } = require('../../utils/audio.js');

function fmtTime(ts) {
  if (!ts) return '';
  const d = new Date(ts);
  const z = function (n) {
    return n < 10 ? '0' + n : '' + n;
  };
  return d.getFullYear() + '-' + z(d.getMonth() + 1) + '-' + z(d.getDate()) + ' ' + z(d.getHours()) + ':' + z(d.getMinutes());
}

Page({
  data: {
    themeClass: '',
    list: [],
  },
  onShow() {
    syncPageTheme(this);
    const raw = storage.getHistory();
    const list = raw.map(function (x) {
      return Object.assign({}, x, { timeText: fmtTime(x.ts) });
    });
    this.setData({ list: list });
  },
  onClear() {
    const page = this;
    wx.showModal({
      title: '清空观看历史',
      content: '确定清空本地演示记录？',
      success: function (res) {
        if (!res.confirm) return;
        storage.clearHistory();
        page.setData({ list: [] });
        wx.showToast({ title: '已清空', icon: 'none' });
      },
    });
  },
  onPlay(e) {
    const track = e.currentTarget.dataset.track;
    const title = e.currentTarget.dataset.title;
    if (!track) {
      wx.showToast({ title: '无关联曲目', icon: 'none' });
      return;
    }
    playTrack(track, title, '历史');
  },
});
