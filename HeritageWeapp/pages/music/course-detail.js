const data = require('../../utils/mock/courses.js');
const { syncPageTheme } = require('../../utils/theme.js');
const { playTrack } = require('../../utils/audio.js');

Page({
  data: {
    themeClass: '',
    course: null,
  },
  onShow() {
    syncPageTheme(this);
  },
  onLoad(options) {
    const id = decodeURIComponent(options.id || '');
    const course = (data.courses || []).find(function (x) {
      return x.id === id;
    });
    this.setData({ course: course || null });
  },
  onPlay() {
    const c = this.data.course;
    if (!c || !c.relatedTrackId) {
      wx.showToast({ title: '暂无关联曲目', icon: 'none' });
      return;
    }
    playTrack(c.relatedTrackId, c.title, '课程');
  },
});
