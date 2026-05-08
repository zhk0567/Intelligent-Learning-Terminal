import { applyTheme } from "../../utils/theme";
import { STORIES } from "../../data/story";
import { toast } from "../../utils/toast";

const PARAGRAPH = `非遗的魅力，并非陈列在博物馆里的标本，而是流淌在每一个还在使用这门技艺的人手心里。
我们用现代的方式重新打开它——让它被听见、被看见、被分享、被延续。
你也许不会成为传承人，但只要你愿意成为「知道的人」，就已经是它的延续。`;

Page({
  data: {
    __themeClass: "theme-dark",
    story: STORIES[0],
    paragraph: PARAGRAPH,
    liked: false,
    saved: false,
    comment: "",
    comments: [
      { name: "蓝栀", text: "看哭了，蜀绣大师那段写得太好。" },
      { name: "白露", text: "已经把故事分享给妈妈了。" },
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
