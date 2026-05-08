import { APP_IMAGES, homeSwiperBySlot } from "./appImages";
import { TRACKS, Track } from "./tracks";

export interface Album {
  id: string;
  title: string;
  desc: string;
  trackIds: string[];
  category: "hot" | "select" | "guess" | "basic";
  coverSrc: string;
}

const B = [APP_IMAGES.banner1, APP_IMAGES.banner2, APP_IMAGES.banner3] as const;
const ac = (i: number) => B[i % 3];

export const ALBUMS: Album[] = [
  { id: "a1", title: "丝路天籁",      desc: "来自敦煌的古乐回响",   trackIds: ["t1", "t2"],          category: "hot",    coverSrc: ac(0) },
  { id: "a2", title: "江南夜雨",      desc: "二胡与雨声的私语",     trackIds: ["t3", "t4"],          category: "hot",    coverSrc: ac(1) },
  { id: "a3", title: "深山林泉",      desc: "古琴与自然之声",       trackIds: ["t1", "t5"],          category: "select", coverSrc: ac(2) },
  { id: "a4", title: "京华春色",      desc: "京剧锣鼓与花腔",       trackIds: ["t2", "t3"],          category: "select", coverSrc: ac(3) },
  { id: "a5", title: "民乐入门 · 第一辑", desc: "新人友好的 5 首推荐", trackIds: ["t1", "t2", "t3"], category: "guess",  coverSrc: ac(4) },
  { id: "a6", title: "宋词曲韵",      desc: "宋代雅乐重制版",       trackIds: ["t4", "t5"],          category: "guess",  coverSrc: ac(5) },
  { id: "a7", title: "古琴入门 7 课", desc: "右手七声 + 左手按弦",  trackIds: ["t1"],                 category: "basic",  coverSrc: ac(6) },
  { id: "a8", title: "古筝乐理速成",  desc: "调式 / 节奏 / 装饰音", trackIds: ["t5"],                 category: "basic",  coverSrc: ac(7) },
];

export const HOT_BANNERS = [
  { id: "hb1", title: "丝路天籁", subtitle: "敦煌古乐 · 听见千年", imageSrc: homeSwiperBySlot(0) },
  { id: "hb2", title: "二胡新韵", subtitle: "传承人 × 青年艺术家", imageSrc: homeSwiperBySlot(1) },
  { id: "hb3", title: "古琴雅集", subtitle: "夏夜山林·云端听琴", imageSrc: homeSwiperBySlot(2) },
];

export const HOT_KEYWORDS = [
  "高山流水", "广陵散", "二泉映月", "梅花三弄", "渔舟唱晚", "敦煌", "古筝入门", "蜀绣",
];

export function albumTracks(album: Album): Track[] {
  return TRACKS.filter((t) => album.trackIds.includes(t.id));
}
