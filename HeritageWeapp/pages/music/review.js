const { syncPageTheme } = require('../../utils/theme.js');

Page({
  data: {
    themeClass: '',
    file: '',
    focusOpts: ['节奏', '音准', '表现力'],
    focusIdx: 0,
  },
  onShow() {
    syncPageTheme(this);
  },
  onFile(e) {
    this.setData({ file: e.detail.value });
  },
  onFocusPick(e) {
    this.setData({ focusIdx: Number(e.detail.value) || 0 });
  },
  onGen() {
    wx.showToast({
      title: '规则引擎点评：演示版',
      icon: 'none',
      duration: 2200,
    });
  },
});
