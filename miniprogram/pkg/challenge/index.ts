import { applyTheme } from "../../utils/theme";
import { CHALLENGES } from "../../data/post";
import { toast } from "../../utils/toast";

Page({
  data: {
    __themeClass: "theme-dark",
    challenges: CHALLENGES,
  },
  onLoad() { applyTheme(this); },
  onShow() { applyTheme(this); },
  join() { toast("已报名"); },
});
