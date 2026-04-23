const AUDIO_DEMO = require('./mock/audio-demo.js');
const storage = require('./storage.js');

function playTrack(trackId, title, sub) {
  const url = AUDIO_DEMO[trackId];
  const app = getApp();
  if (!url) {
    wx.showToast({ title: '暂无试听', icon: 'none' });
    return;
  }
  const ctx = app.getInnerAudio();
  ctx.stop();
  ctx.src = url;
  ctx.play();
  storage.pushHistory({
    trackId: trackId,
    title: title,
    sub: sub || '音乐馆 · 试听',
  });
  wx.showToast({
    title: title ? '播放：' + title : '开始播放',
    icon: 'none',
    duration: 1800,
  });
}

module.exports = {
  playTrack,
  AUDIO_DEMO,
};
