import { ICONS } from "./icons";

Component({
  options: {
    multipleSlots: false,
  },
  externalClasses: ["icon-class"],
  properties: {
    name: { type: String, value: "" },
    /** 单位 rpx；未传 size 时略放大便于点击与阅读 */
    size: { type: Number, value: 52 },
    /** 颜色：CSS 变量或 hex；空串则用 var(--text-primary)。 */
    color: { type: String, value: "" },
  },
  data: {
    computedStyle: "",
  },
  observers: {
    "name, size, color": function (name: string, size: number, color: string) {
      const url = ICONS[name] || ICONS.close;
      const c = color || "var(--text-primary)";
      const style =
        `width:${size}rpx;height:${size}rpx;` +
        `-webkit-mask-image:${url};mask-image:${url};` +
        `background-color:${c};`;
      this.setData({ computedStyle: style });
    },
  },
});
