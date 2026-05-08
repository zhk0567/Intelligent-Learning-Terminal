Component({
  options: {
    multipleSlots: true,
    addGlobalClass: true,
  },
  properties: {
    title: { type: String, value: "" },
    subtitle: { type: String, value: "" },
    back: { type: Boolean, value: true },
    transparent: { type: Boolean, value: false },
  },
  data: {
    statusBarPadding: 0,
  },
  lifetimes: {
    attached() {
      try {
        const sys = wx.getSystemInfoSync();
        const w = sys.windowWidth || 375;
        const toRpx = (px: number) => Math.ceil(Math.max(0, px) * (750 / w));
        let topPx = 0;
        try {
          const menu = wx.getMenuButtonBoundingClientRect();
          if (menu && typeof menu.top === "number" && menu.top > 0) {
            topPx = menu.top;
          }
        } catch (_) {
          /* 基础库过低时无胶囊 API */
        }
        if (topPx <= 0) {
          topPx = Math.max(
            (sys.safeArea && sys.safeArea.top) || 0,
            sys.statusBarHeight || 0,
            24
          );
        }
        const padRpx = Math.max(96, toRpx(topPx) + 8);
        this.setData({ statusBarPadding: padRpx });
      } catch {
        this.setData({ statusBarPadding: 96 });
      }
    },
  },
  methods: {
    goBack() {
      this.triggerEvent("back", {});
      const pages = getCurrentPages();
      if (pages.length > 1) {
        wx.navigateBack({ delta: 1 });
      } else {
        wx.reLaunch({ url: "/pages/music/index" });
      }
    },
  },
});
