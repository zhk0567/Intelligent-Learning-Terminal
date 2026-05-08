Component({
  options: { addGlobalClass: true },
  properties: {
    /** 形状：line / bar / square / circle */
    shape: { type: String, value: "line" },
    width: { type: String, value: "" },
    height: { type: String, value: "" },
    extraClass: { type: String, value: "" },
  },
  data: { computedStyle: "" },
  observers: {
    "width, height": function (w: string, h: string) {
      const parts: string[] = [];
      if (w) parts.push(`width:${w};`);
      if (h) parts.push(`height:${h};`);
      this.setData({ computedStyle: parts.join("") });
    },
  },
});
