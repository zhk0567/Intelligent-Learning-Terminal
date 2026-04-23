const FAV_KEY = 'heritageMpFav';
const HIST_KEY = 'heritageMpHist';

function readJson(key, def) {
  try {
    const s = wx.getStorageSync(key);
    if (s) return JSON.parse(s);
  } catch (e) {}
  return def;
}

function writeJson(key, obj) {
  try {
    wx.setStorageSync(key, JSON.stringify(obj));
  } catch (e) {}
}

function pushHistory(entry) {
  const lh = readJson(HIST_KEY, []);
  lh.unshift({
    id: 'h' + Date.now(),
    title: entry.title || '未命名',
    sub: entry.sub || '音乐馆 · 试听',
    trackId: entry.trackId || '',
    ts: Date.now(),
  });
  if (lh.length > 30) lh.length = 30;
  writeJson(HIST_KEY, lh);
}

function addFavorite(trackId, title) {
  if (!trackId) return { ok: false, reason: 'no-track' };
  const list = readJson(FAV_KEY, []);
  if (list.some(function (x) { return x.trackId === trackId; })) {
    return { ok: false, reason: 'exists' };
  }
  list.unshift({
    id: 'f' + Date.now(),
    trackId: trackId,
    title: title || '未命名',
    ts: Date.now(),
  });
  if (list.length > 100) list.length = 100;
  writeJson(FAV_KEY, list);
  return { ok: true };
}

function removeFavorite(id) {
  const list = readJson(FAV_KEY, []);
  const next = list.filter(function (x) { return x.id !== id; });
  writeJson(FAV_KEY, next);
}

function clearHistory() {
  writeJson(HIST_KEY, []);
}

module.exports = {
  getFavorites: function () {
    return readJson(FAV_KEY, []);
  },
  getHistory: function () {
    return readJson(HIST_KEY, []);
  },
  addFavorite: addFavorite,
  removeFavorite: removeFavorite,
  pushHistory: pushHistory,
  clearHistory: clearHistory,
};
