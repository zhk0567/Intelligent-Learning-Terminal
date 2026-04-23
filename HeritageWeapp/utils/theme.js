const CHROME = {
  tech: {
    navBg: '#0f182b',
    navFront: '#ffffff',
    tabBg: '#0f182b',
    tabColor: '#a9bed0',
    tabSelected: '#37e7ff',
    pageBg: '#050814',
  },
  paper: {
    navBg: '#fffcf4',
    navFront: '#000000',
    tabBg: '#fffcf4',
    tabColor: '#4d473c',
    tabSelected: '#2a496e',
    pageBg: '#f7f2e8',
  },
  neon: {
    navBg: '#1a042f',
    navFront: '#ffffff',
    tabBg: '#1a042f',
    tabColor: '#d1b8ee',
    tabSelected: '#67dfec',
    pageBg: '#0d0218',
  },
  forest: {
    navBg: '#182c21',
    navFront: '#ffffff',
    tabBg: '#182c21',
    tabColor: '#b4cbbb',
    tabSelected: '#e8c867',
    pageBg: '#101c16',
  },
};

function themeSuffix() {
  const t = getApp().globalData.theme || 'tech';
  if (t === 'paper' || t === 'neon' || t === 'forest') return 'theme-' + t;
  return '';
}

function applyChromeTheme() {
  const t = getApp().globalData.theme || 'tech';
  const c = CHROME[t] || CHROME.tech;
  try {
    wx.setNavigationBarColor({
      frontColor: c.navFront === '#000000' ? '#000000' : '#ffffff',
      backgroundColor: c.navBg,
    });
  } catch (e) {}
  try {
    wx.setBackgroundColor({
      backgroundColor: c.pageBg,
      backgroundColorTop: c.pageBg,
      backgroundColorBottom: c.pageBg,
    });
  } catch (e) {}
  try {
    wx.setTabBarStyle({
      color: c.tabColor,
      selectedColor: c.tabSelected,
      backgroundColor: c.tabBg,
      borderStyle: t === 'paper' ? 'white' : 'black',
    });
  } catch (e) {}
}

function syncPageTheme(page) {
  page.setData({ themeClass: themeSuffix() });
  applyChromeTheme();
}

module.exports = {
  syncPageTheme,
  themeSuffix,
  applyChromeTheme,
};
