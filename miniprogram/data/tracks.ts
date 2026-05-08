import { APP_IMAGES } from "./appImages";

export interface Track {
  id: string;
  title: string;
  artist: string;
  album: string;
  durationSec: number;
  /** 与 web `PlayerStore.TRACKS` 同序：轮用三张 banner。 */
  coverSrc: string;
}

const B = [APP_IMAGES.banner1, APP_IMAGES.banner2, APP_IMAGES.banner3] as const;
const tc = (i: number) => B[i % 3];

export const TRACKS: Track[] = [
  { id: "t1", title: "高山流水", artist: "古筝演奏", album: "国风雅集", durationSec: 248, coverSrc: tc(0) },
  { id: "t2", title: "广陵散",   artist: "古琴独奏", album: "国风雅集", durationSec: 312, coverSrc: tc(1) },
  { id: "t3", title: "二泉映月", artist: "二胡演奏", album: "民乐经典", durationSec: 270, coverSrc: tc(2) },
  { id: "t4", title: "梅花三弄", artist: "笛子独奏", album: "民乐经典", durationSec: 226, coverSrc: tc(3) },
  { id: "t5", title: "渔舟唱晚", artist: "古筝演奏", album: "国风雅集", durationSec: 295, coverSrc: tc(4) },
];

export function tracksByIds(ids: string[]): Track[] {
  return TRACKS.filter((t) => ids.includes(t.id));
}
