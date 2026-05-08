Component({
  options: { addGlobalClass: true },
  properties: {
    text: { type: String, value: "" },
    /** 单位 rpx，默认 56 ≈ web 28px */
    size: { type: Number, value: 56 },
    /** solid / outline */
    tone: { type: String, value: "solid" },
    extraClass: { type: String, value: "" },
  },
  data: { fontSize: 28 },
  observers: {
    size(v: number) {
      this.setData({ fontSize: Math.max(20, Math.floor(v * 0.5)) });
    },
  },
});
