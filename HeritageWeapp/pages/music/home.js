const rawHome = require('../../utils/mock/music-home.js');
const { syncPageTheme } = require('../../utils/theme.js');
const { playTrack } = require('../../utils/audio.js');
const storage = require('../../utils/storage.js');

function norm(s) {
  return (s || '').toLowerCase();
}

function filterHome(data, kw) {
  const k = norm(kw.trim());
  if (!k) {
    return {
      banners: data.banners,
      hotTiles: data.hotTiles,
      dailyPicks: data.dailyPicks,
      guessTags: data.guessTags,
      bottomCards: data.bottomCards,
      noHit: false,
    };
  }
  const match = function (t) {
    return norm(t).indexOf(k) >= 0;
  };
  const banners = data.banners.filter(function (b) {
    return match(b.title);
  });
  const hotTiles = data.hotTiles.filter(function (b) {
    return match(b.title);
  });
  const dailyPicks = data.dailyPicks.filter(function (b) {
    return match(b.title);
  });
  const guessTags = data.guessTags.filter(function (b) {
    return match(b.label);
  });
  const bottomCards = data.bottomCards.filter(function (b) {
    return match(b.title);
  });
  const noHit =
    !banners.length &&
    !hotTiles.length &&
    !dailyPicks.length &&
    !guessTags.length &&
    !bottomCards.length;
  return { banners, hotTiles, dailyPicks, guessTags, bottomCards, noHit };
}

Page({
  data: {
    themeClass: '',
    keyword: '',
    banners: [],
    hotTiles: [],
    dailyPicks: [],
    guessTags: [],
    bottomCards: [],
    noHit: false,
  },
  onShow() {
    syncPageTheme(this);
    this.applyFilter(this.data.keyword);
  },
  applyFilter(kw) {
    const o = filterHome(rawHome, kw || '');
    this.setData(o);
  },
  onSearchInput(e) {
    const kw = e.detail.value || '';
    this.setData({ keyword: kw });
    this.applyFilter(kw);
  },
  onEntryTap(e) {
    const entry = e.currentTarget.dataset.entry;
    const map = {
      archive: '/pages/music/archive',
      course: '/pages/music/course',
      interactive: '/pages/music/interactive',
    };
    const url = map[entry];
    if (url) wx.navigateTo({ url: url });
  },
  onBannerTap(e) {
    const track = e.currentTarget.dataset.track;
    const title = e.currentTarget.dataset.title;
    playTrack(track, title);
  },
  onPlayTap(e) {
    const track = e.currentTarget.dataset.track;
    const title = e.currentTarget.dataset.title;
    playTrack(track, title);
  },
  onFavLong(e) {
    const track = e.currentTarget.dataset.track;
    const title = e.currentTarget.dataset.title;
    const r = storage.addFavorite(track, title);
    if (r.ok) {
      wx.showToast({ title: '已加入收藏', icon: 'none' });
    } else if (r.reason === 'exists') {
      wx.showToast({ title: '已在收藏中', icon: 'none' });
    } else {
      wx.showToast({ title: '无法收藏', icon: 'none' });
    }
  },
});
