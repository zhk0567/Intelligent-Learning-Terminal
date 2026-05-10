import { applyTheme } from "../../utils/theme";
import { toast } from "../../utils/toast";
import { STORIES_FULL, storyByIdFull } from "../data/storyBodies";

Page({
  data: {
    __themeClass: "theme-dark",
    story: STORIES_FULL[0],
    liked: false,
    saved: false,
  },
  onLoad(query: any) {
    applyTheme(this);
    const id = query?.id;
    const story = storyByIdFull(id) || STORIES_FULL[0];
    this.setData({ story });
  },
  onShow() { applyTheme(this); },
  toggleLike() {
    const liked = !this.data.liked;
    this.setData({ liked });
    toast(liked ? "感谢点赞" : "已取消");
  },
  toggleSave() {
    const saved = !this.data.saved;
    this.setData({ saved });
    toast(saved ? "已加入收藏" : "已移除收藏");
  },
  onShare() {
    toast("分享链接已复制");
  },
  previewCover() {
    const u = this.data.story?.coverSrc;
    if (!u) return;
    wx.previewImage({ urls: [u] });
  },
});
