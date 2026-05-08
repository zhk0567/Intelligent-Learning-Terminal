import { applyTheme } from "../../utils/theme";
import { POSTS } from "../../data/post";
import { toast } from "../../utils/toast";

Page({
  data: {
    __themeClass: "theme-dark",
    post: POSTS[0],
    liked: false,
    saved: false,
    comment: "",
    comments: [
      { name: "山雀", text: "好喜欢这个改编版本，能教教我吗？" },
      { name: "南风", text: "顶一下！" },
    ] as any[],
    likeTotal: POSTS[0].likeCount,
    commentTotal: POSTS[0].commentCount,
  },
  onLoad(query: any) {
    applyTheme(this);
    const id = query?.id;
    const post = POSTS.find((p) => String(p.id) === String(id)) || POSTS[0];
    this.setData({ post, likeTotal: post.likeCount, commentTotal: post.commentCount });
  },
  onShow() { applyTheme(this); },
  toggleLike() {
    const liked = !this.data.liked;
    this.setData({ liked, likeTotal: this.data.post.likeCount + (liked ? 1 : 0) });
    toast(liked ? "已点赞" : "已取消");
  },
  toggleSave() {
    const saved = !this.data.saved;
    this.setData({ saved });
    toast(saved ? "已收藏" : "已移除收藏");
  },
  onInput(e: any) { this.setData({ comment: e.detail.value }); },
  submitComment() {
    const c = this.data.comment.trim();
    if (!c) return;
    this.setData({
      comment: "",
      comments: [{ name: "我", text: c }, ...this.data.comments],
      commentTotal: this.data.commentTotal + 1,
    });
  },
  goCreator() {
    wx.navigateTo({ url: `/pages/creator-profile/index?id=u${this.data.post.id}` });
  },
  onShare() { toast("分享链接已复制"); },
  previewCover() {
    const u = this.data.post?.coverSrc;
    if (!u) return;
    wx.previewImage({ urls: [u] });
  },
});
