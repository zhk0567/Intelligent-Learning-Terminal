const { applyChromeTheme } = require('./utils/theme.js');

App({
  globalData: {
    theme: 'tech',
  },
  _innerAudio: null,
  onLaunch() {
    try {
      const t = wx.getStorageSync('heritageMpTheme');
      if (t === 'paper' || t === 'neon' || t === 'forest' || t === 'tech') {
        this.globalData.theme = t;
      }
    } catch (e) {}
    applyChromeTheme();
  },
  getInnerAudio() {
    if (!this._innerAudio) {
      const ctx = wx.createInnerAudioContext();
      ctx.obeyMuteSwitch = true;
      ctx.onError(function () {
        wx.showToast({
          title: '播放失败（请检查网络或合法域名）',
          icon: 'none',
        });
      });
      this._innerAudio = ctx;
    }
    return this._innerAudio;
  },
  setTheme(name) {
    const v = name === 'paper' || name === 'neon' || name === 'forest' ? name : 'tech';
    this.globalData.theme = v;
    try {
      wx.setStorageSync('heritageMpTheme', v);
    } catch (e) {}
    applyChromeTheme();
  },
});
