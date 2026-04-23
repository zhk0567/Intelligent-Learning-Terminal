const { syncPageTheme } = require('../../utils/theme.js');

Page({
  data: {
    themeClass: '',
  },
  onShow() {
    syncPageTheme(this);
  },
});
