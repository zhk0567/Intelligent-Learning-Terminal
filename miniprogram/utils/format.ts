/**
 * 通用格式化工具。
 */

export function fmtTime(s: number): string {
  const m = Math.floor(s / 60);
  const r = Math.floor(s % 60);
  return `${String(m).padStart(2, "0")}:${String(r).padStart(2, "0")}`;
}

/** 简单 hash，与 web Cover 用的同一个语义。 */
export function hash(seed: string): number {
  let h = 0;
  for (let i = 0; i < seed.length; i++) {
    h = (h * 31 + seed.charCodeAt(i)) >>> 0;
  }
  return h;
}

/** Web Cover 用的 5 套素胚色（含暗色映射）。 */
export interface CoverPalette {
  bg: string;
  text: string;
  bgDark: string;
  textDark: string;
}

export const COVER_PALETTES: CoverPalette[] = [
  { bg: "#F2EAD9", text: "#6B4A2B", bgDark: "#2A2418", textDark: "#D9BE8A" },
  { bg: "#D9E5DD", text: "#355E50", bgDark: "#15241F", textDark: "#9CC9B5" },
  { bg: "#E9D5C8", text: "#8C3F3F", bgDark: "#2A1E1B", textDark: "#D89A85" },
  { bg: "#D6DDE5", text: "#2F3E55", bgDark: "#161E2A", textDark: "#9CB2CC" },
  { bg: "#EFE4C9", text: "#7C5A19", bgDark: "#241E13", textDark: "#D9BC73" },
];

export function paletteFor(seed: string): CoverPalette {
  return COVER_PALETTES[hash(seed) % COVER_PALETTES.length];
}
