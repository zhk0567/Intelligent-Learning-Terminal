import { applyTheme } from "../../utils/theme";

Page({
  data: { __themeClass: "theme-dark" },
  onLoad() { applyTheme(this); },
  onShow() { applyTheme(this); },
});
