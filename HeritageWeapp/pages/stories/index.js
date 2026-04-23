const feed = require('../../utils/mock/stories-feed.js');
const { syncPageTheme } = require('../../utils/theme.js');

function splitColumns(items) {
  const left = [];
  const right = [];
  let lh = 0;
  let rh = 0;
  items.forEach(function (it) {
    const h = it.minHeight || 180;
    if (lh <= rh) {
      left.push(it);
      lh += h;
    } else {
      right.push(it);
      rh += h;
    }
  });
  return { leftCol: left, rightCol: right };
}

function filterItems(items, kw) {
  const k = (kw || '').trim().toLowerCase();
  if (!k) return items;
  return items.filter(function (it) {
    const idHit = (it.id || '').toLowerCase().indexOf(k) >= 0;
    const ov = (it.overlay || '').toLowerCase().indexOf(k) >= 0;
    return idHit || ov;
  });
}

Page({
  data: {
    themeClass: '',
    storyTab: 'recommend',
    kw: '',
    leftCol: [],
    rightCol: [],
    noHit: false,
  },
  onShow() {
    syncPageTheme(this);
    this.refresh();
  },
  refresh() {
    const tab = this.data.storyTab;
    const items = feed[tab === 'following' ? 'following' : 'recommend'] || [];
    const filtered = filterItems(items, this.data.kw);
    if (!filtered.length) {
      this.setData({ leftCol: [], rightCol: [], noHit: true });
      return;
    }
    const cols = splitColumns(filtered);
    cols.noHit = false;
    this.setData(cols);
  },
  onStoryTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ storyTab: tab });
    this.refresh();
  },
  onKwInput(e) {
    this.setData({ kw: e.detail.value || '' });
    this.refresh();
  },
});
