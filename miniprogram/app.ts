import { initTheme, getTheme, type ThemeMode } from "./utils/theme";
import { startPlayerTicker, stopPlayerTicker } from "./stores/playerStore";

interface GlobalData {
  theme: ThemeMode;
}

App<{
  globalData: GlobalData;
}>({
  globalData: {
    theme: "dark",
  },
  onLaunch() {
    const theme = initTheme();
    this.globalData.theme = theme;
    startPlayerTicker();
  },
  onShow() {
    this.globalData.theme = getTheme();
    startPlayerTicker();
  },
  onHide() {
    stopPlayerTicker();
  },
});
