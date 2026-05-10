import { CSSProperties } from "react";
import { assetUrl } from "../lib/assetUrl";
import { CornerPattern } from "./Ornament";
import { useThemeStore } from "../store/themeStore";

interface Props {
  seed: string;
  text?: string;
  /** 与 App `drawable` 同步的静态图 URL；有图时不叠大字占位，仅作装饰边框与角章。 */
  src?: string;
  alt?: string;
  className?: string;
  rounded?: string;
  aspect?: string;
  style?: CSSProperties;
  /** 是否在右下角叠回纹角章。默认 true，小尺寸场景可关掉。 */
  ornate?: boolean;
  /** 有图时：`cover` 铺满裁切；`contain` 完整显示（轮播等横图易裁切时用）。 */
  imgFit?: "cover" | "contain";
  /** 与源封面图一致的像素宽；与 coverHeight 同时传入时容器按该比例占位，避免裁成 16:9 / 正方形 */
  coverWidth?: number;
  coverHeight?: number;
}

interface Palette {
  bg: string;
  text: string;
  bgDark: string;
  textDark: string;
}

/** 5 套素胚配色：米白/月白/胭脂淡/烟青/鹅黄；暗色映射 5 套夜窑。 */
const PALETTES: Palette[] = [
  { bg: "#F2EAD9", text: "#6B4A2B", bgDark: "#2A2418", textDark: "#D9BE8A" },
  { bg: "#D9E5DD", text: "#355E50", bgDark: "#15241F", textDark: "#9CC9B5" },
  { bg: "#E9D5C8", text: "#8C3F3F", bgDark: "#2A1E1B", textDark: "#D89A85" },
  { bg: "#D6DDE5", text: "#2F3E55", bgDark: "#161E2A", textDark: "#9CB2CC" },
  { bg: "#EFE4C9", text: "#7C5A19", bgDark: "#241E13", textDark: "#D9BC73" },
];

/** 扁平占位封面：5 套素胚色 + 右下角回纹角章。无渐变。有 `src` 时为真实图片铺满。 */
export default function Cover({
  seed,
  text,
  src,
  alt,
  className = "",
  rounded = "rounded-xl",
  aspect = "aspect-square",
  style,
  ornate = true,
  imgFit = "cover",
  coverWidth,
  coverHeight,
}: Props) {
  const mode = useThemeStore((s) => s.mode);
  const idx = hash(seed) % PALETTES.length;
  const p = PALETTES[idx];
  const bg = mode === "dark" ? p.bgDark : p.bg;
  const fg = mode === "dark" ? p.textDark : p.text;
  const rawSrc = src?.trim() ?? "";
  const imgSrc = rawSrc ? assetUrl(rawSrc) : "";
  const hasImg = Boolean(rawSrc);
  const label = alt ?? text ?? seed;
  const useIntrinsic =
    hasImg &&
    typeof coverWidth === "number" &&
    typeof coverHeight === "number" &&
    coverWidth > 0 &&
    coverHeight > 0;
  /** 不用 `w-full`：否则会盖掉调用方传入的 `w-20` 等固定宽（故事列表横排缩略图会撑满一行、文字排布错乱）。块级 + `width:auto` 在父级内仍会占满可用宽度。 */
  const boxAspectClass = useIntrinsic ? "block min-w-0 max-w-full" : aspect;
  const boxAspectStyle: CSSProperties | undefined =
    useIntrinsic && coverWidth && coverHeight
      ? { aspectRatio: `${coverWidth} / ${coverHeight}` }
      : undefined;
  /** 有图时不用 flex，避免与横排列表里的固定宽高冲突；无图时 flex 居中占位字。 */
  const layout = hasImg
    ? "block"
    : "flex min-h-0 items-center justify-center";

  const fitContain = hasImg && imgFit === "contain";
  return (
    <div
      className={`${boxAspectClass} ${rounded} relative min-w-0 overflow-hidden ${fitContain ? "bg-bg-card" : ""} ${layout} border border-ancient-bronze/40 dark:border-ancient-bronze/30 ${className}`}
      style={
        hasImg
          ? { ...boxAspectStyle, ...style }
          : { background: bg, color: fg, ...style }
      }
    >
      {hasImg && (
        <img
          src={imgSrc}
          alt={label}
          className={`absolute inset-0 z-0 h-full w-full ${imgFit === "contain" ? "object-contain" : "object-cover"}`}
          loading="lazy"
        />
      )}
      {!hasImg && (
        <span className="relative z-[1] px-3 text-center text-sm font-semibold line-clamp-3">
          {text ?? seed}
        </span>
      )}
      {ornate && (
        <CornerPattern
          size={30}
          opacity={hasImg ? 0.55 : 0.32}
          className={`absolute right-1 bottom-1 z-[2] ${hasImg ? "text-white drop-shadow-[0_1px_2px_rgba(0,0,0,0.45)]" : "text-current"}`}
        />
      )}
    </div>
  );
}

function hash(seed: string): number {
  let h = 0;
  for (let i = 0; i < seed.length; i++) h = (h * 31 + seed.charCodeAt(i)) >>> 0;
  return h;
}
