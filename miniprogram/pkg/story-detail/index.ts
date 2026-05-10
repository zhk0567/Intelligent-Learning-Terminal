import { applyTheme } from "../../utils/theme";
import { toast } from "../../utils/toast";
import { STORIES_FULL, storyByIdFull } from "../data/storyBodies";

const PARAGRAPH = `非遗的魅力，并非只陈列在展柜里的标本，而是仍活跃在传承人、观众与一方水土之间的共同记忆。
我们用记录与传播的方式重新打开它——让它被听见、被看见、被讨论、被续写。
你也许不会成为传承人，但只要你愿意多了解一分，便已参与它的延续。`;

Page({
  data: {
    __themeClass: "theme-dark",
    story: STORIES_FULL[0],
    paragraph: PARAGRAPH,
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
