import { applyTheme } from "../../utils/theme";
import { toast } from "../../utils/toast";

const CATS = ["音乐", "舞蹈", "技艺", "故事", "改编", "教学"];

Page({
  data: {
    __themeClass: "theme-dark",
    title: "",
    content: "",
    cat: "音乐",
    tagText: "",
    tags: ["古风"],
    cats: CATS,
  },
  onLoad() { applyTheme(this); },
  onShow() { applyTheme(this); },
  onTitle(e: any) { this.setData({ title: e.detail.value }); },
  onContent(e: any) { this.setData({ content: e.detail.value }); },
  pickCat(e: any) { this.setData({ cat: e.currentTarget.dataset.c }); },
  onTagInput(e: any) { this.setData({ tagText: e.detail.value }); },
  addTag() {
    const t = this.data.tagText.trim();
    if (!t) return;
    if (this.data.tags.indexOf(t) >= 0) {
      this.setData({ tagText: "" });
      return;
    }
    this.setData({ tags: [...this.data.tags, t], tagText: "" });
  },
  removeTag(e: any) {
    const t = e.currentTarget.dataset.t;
    this.setData({ tags: this.data.tags.filter((x) => x !== t) });
  },
  pickImage() {
    wx.chooseImage?.({ count: 9, success: () => toast("图片已选择") });
  },
  pickVideo() {
    toast("视频选择 - 演示");
  },
  pickAudio() {
    toast("音频选择 - 演示");
  },
  submit() {
    if (!this.data.title.trim()) return toast("请填写标题");
    if (this.data.content.length < 5) return toast("正文至少 5 个字");
    toast("作品已提交审核");
    setTimeout(() => wx.navigateBack({ delta: 1 }), 600);
  },
});
