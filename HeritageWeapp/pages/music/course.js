const data = require('../../utils/mock/courses.js');
const { syncPageTheme } = require('../../utils/theme.js');
Page({
  data: {
    themeClass: '',
    courses: [],
  },
  onShow() {
    syncPageTheme(this);
    this.setData({ courses: data.courses || [] });
  },
  onCourseOpen(e) {
    const id = e.currentTarget.dataset.id;
    if (!id) return;
    wx.navigateTo({ url: '/pages/music/course-detail?id=' + encodeURIComponent(id) });
  },
});
