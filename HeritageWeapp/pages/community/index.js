const data = require('../../utils/mock/community-posts.js');
const { syncPageTheme } = require('../../utils/theme.js');

function filterByKw(posts, kw) {
  const k = (kw || '').trim().toLowerCase();
  if (!k) return posts.slice();
  return posts.filter(function (p) {
    return (
      (p.title || '').toLowerCase().indexOf(k) >= 0 ||
      (p.body || '').toLowerCase().indexOf(k) >= 0 ||
      (p.subtitle || '').toLowerCase().indexOf(k) >= 0
    );
  });
}

Page({
  data: {
    themeClass: '',
    kw: '',
    category: '',
    posts: [],
    noHit: false,
  },
  onShow() {
    syncPageTheme(this);
    this.apply();
  },
  apply() {
    let posts = data.posts || [];
    const cat = this.data.category;
    if (cat) {
      posts = posts.filter(function (p) {
        return p.category === cat;
      });
    }
    posts = filterByKw(posts, this.data.kw);
    this.setData({ posts: posts, noHit: !posts.length });
  },
  onKwInput(e) {
    this.setData({ kw: e.detail.value || '' });
    this.apply();
  },
  onCatTap(e) {
    let cat = e.currentTarget.dataset.cat;
    if (cat === 'all') cat = '';
    this.setData({ category: cat || '' });
    this.apply();
  },
  openPost(e) {
    const id = e.currentTarget.dataset.id;
    if (!id) return;
    wx.navigateTo({ url: '/pages/community/detail?id=' + encodeURIComponent(id) });
  },
});
