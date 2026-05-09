import { COVER_PALETTES, hash } from "../../utils/format";
import { getTheme, subscribeTheme } from "../../utils/theme";
import { assetUrl } from "../../utils/assetUrl";

const ASPECT_MAP: Record<string, string> = {
  square: "cover-aspect-square",
  "16/9": "cover-aspect-16-9",
  "16/7": "cover-aspect-16-7",
  "2/1": "cover-aspect-2-1",
  "3/2": "cover-aspect-3-2",
  "4/3": "cover-aspect-4-3",
};

const ROUNDED_MAP: Record<string, string> = {
  md: "cover-rounded-md",
  lg: "cover-rounded-lg",
  xl: "cover-rounded-xl",
  "2xl": "cover-rounded-2xl",
  card: "cover-rounded-card",
  full: "cover-rounded-full",
};

Component({
  options: {
    multipleSlots: false,
    addGlobalClass: true,
  },
  externalClasses: ["extra-class"],
  properties: {
    seed: { type: String, value: "" },
    text: { type: String, value: "" },
    src: { type: String, value: "" },
    alt: { type: String, value: "" },
    aspect: { type: String, value: "square" },
    rounded: { type: String, value: "xl" },
    ornate: { type: Boolean, value: true },
    extraClass: { type: String, value: "" },
    coverWidth: { type: Number, value: 0 },
    coverHeight: { type: Number, value: 0 },
  },
  data: {
    aspectClass: "cover-aspect-square",
    roundedClass: "cover-rounded-xl",
    label: "",
    computedStyle: "",
  },
  lifetimes: {
    attached() {
      (this as any)._unsubTheme = subscribeTheme(() => this.recompute());
      this.recompute();
    },
    detached() {
      (this as any)._unsubTheme?.();
    },
  },
  observers: {
    "seed, text, src, alt, aspect, rounded, coverWidth, coverHeight": function () {
      this.recompute();
    },
  },
  methods: {
    recompute() {
      const { seed, text, src, alt, aspect, rounded, coverWidth, coverHeight } = this.data as any;
      const cw = Number(coverWidth);
      const ch = Number(coverHeight);
      const useIntrinsic = Boolean(src && cw > 0 && ch > 0);
      const aspectClass = useIntrinsic
        ? "cover-aspect-intrinsic"
        : ASPECT_MAP[aspect] || "cover-aspect-square";
      const roundedClass = ROUNDED_MAP[rounded] || "cover-rounded-xl";
      const label = alt || text || seed || "";

      let style = "";
      if (!src) {
        const idx = hash(seed || label || "x") % COVER_PALETTES.length;
        const p = COVER_PALETTES[idx];
        const isDark = getTheme() === "dark";
        const bg = isDark ? p.bgDark : p.bg;
        const fg = isDark ? p.textDark : p.text;
        style = `background:${bg};color:${fg};`;
      }
      if (useIntrinsic) {
        const pb = ((ch / cw) * 100).toFixed(6);
        style = style ? `${style};--cover-pb:${pb}%;` : `--cover-pb:${pb}%;`;
      }
      const resolvedSrc = src ? assetUrl(String(src)) : "";
      this.setData({ aspectClass, roundedClass, label, computedStyle: style, resolvedSrc });
    },
  },
});
