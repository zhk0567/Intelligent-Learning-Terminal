const data = require('../../utils/mock/community-posts.js');
const { syncPageTheme } = require('../../utils/theme.js');

Page({
  data: {
    themeClass: '',
    post: null,
  },
  onShow() {
    syncPageTheme(this);
  },
  onLoad(options) {
    const id = decodeURIComponent(options.id || '');
    const post = (data.posts || []).find(function (p) {
      return p.id === id;
    });
    this.setData({ post: post || null });
    if (post) {
      wx.setNavigationBarTitle({ title: post.title.slice(0, 12) + (post.title.length > 12 ? '…' : '') });
    }
  },
});
