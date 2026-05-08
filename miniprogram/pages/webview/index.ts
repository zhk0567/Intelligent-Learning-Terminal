import { applyTheme } from "../../utils/theme";

const AGREEMENT = `欢迎使用「古韵薪传」客户端。\n\n本协议示例文本仅用于演示。请遵守相关法律法规，文明使用，不得发布违法、侵权及不良内容。\n\n您一旦完成注册即视为同意本协议。如有疑问，可在「设置 - 关于」页面查看联系方式。`;
const PRIVACY = `古韵薪传重视您的个人信息保护。本演示版本不会上传任何手机号、密码或地址数据，所有交互均存储在本地。\n\n生产环境会按《个人信息保护法》规定告知收集范围、用途及保留期限。`;
const ABOUT = `古韵薪传 · 科技赋能版本 v0.1\n\n本页面供示例：用于展示 H5 内容。小程序与 Web/Android 端使用相同布局以保持视觉一致性。`;

Page({
  data: {
    __themeClass: "theme-dark",
    title: "网页",
    url: "",
    paragraphs: [] as string[],
  },
  onLoad(query: any) {
    applyTheme(this);
    const title = query?.title || "网页";
    const url = query?.url || "";
    const article = title.indexOf("协议") >= 0 ? AGREEMENT
      : title.indexOf("隐私") >= 0 ? PRIVACY
      : ABOUT;
    this.setData({
      title,
      url,
      paragraphs: article.split("\n\n"),
    });
  },
  onShow() { applyTheme(this); },
});
