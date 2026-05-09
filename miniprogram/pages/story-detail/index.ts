import { applyTheme } from "../../utils/theme";
import { STORIES } from "../../data/story";
import { toast } from "../../utils/toast";

const PARAGRAPH = `非遗的魅力，并非只陈列在展柜里的标本，而是仍活跃在传承人、观众与一方水土之间的共同记忆。
我们用记录与传播的方式重新打开它——让它被听见、被看见、被讨论、被续写。
你也许不会成为传承人，但只要你愿意多了解一分，便已参与它的延续。`;

Page({
  data: {
    __themeClass: "theme-dark",
    story: STORIES[0],
    paragraph: PARAGRAPH,
    liked: false,
    saved: false,
    comment: "",
    comments: [
      { name: "青禾", text: "史料与传承脉络写得很清楚，读完对这门非遗更有敬意。" },
      { name: "白露", text: "已转发给家人，希望更多人能看到地方曲艺与古乐。" },
    ] as any[],
  },
  onLoad(query: any) {
    applyTheme(this);
    const id = query?.id;
    const story = STORIES.find((s) => s.id === id) || STORIES[0];
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
  onInput(e: any) {
    this.setData({ comment: e.detail.value });
  },
  submitComment() {
    const c = this.data.comment.trim();
    if (!c) return;
    this.setData({
      comment: "",
      comments: [{ name: "我", text: c }, ...this.data.comments],
    });
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
