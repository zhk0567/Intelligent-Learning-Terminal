import { APP_IMAGES, homeSwiperBySlot } from "./appImages";
import { dailyGuessCoverPath, dailyHotCoverPath, dailySelectCoverPath, TRACKS, Track } from "./tracks";

export interface Album {
  id: string;
  title: string;
  desc: string;
  trackIds: string[];
  /** `basic` 已迁移到 `data/lessons.ts` 的视频课，不再放在 ALBUMS 中。 */
  category: "hot" | "select" | "guess";
  coverSrc: string;
}

const B = [APP_IMAGES.banner1, APP_IMAGES.banner2, APP_IMAGES.banner3] as const;
const ac = (i: number) => B[i % 3];

export const ALBUMS: Album[] = [
  { id: "hot_a1", title: "哑女告状", desc: "四平调", trackIds: ["hot_t1"], category: "hot", coverSrc: dailyHotCoverPath(0) },
  { id: "hot_a2", title: "抬花轿", desc: "沁阳唢呐", trackIds: ["hot_t2"], category: "hot", coverSrc: dailyHotCoverPath(1) },
  { id: "hot_a3", title: "美美与共", desc: "箜篌艺术", trackIds: ["hot_t3"], category: "hot", coverSrc: dailyHotCoverPath(2) },
  { id: "hot_a4", title: "醉美玉见", desc: "九莲灯", trackIds: ["hot_t4"], category: "hot", coverSrc: dailyHotCoverPath(3) },
  { id: "sel_a1", title: "杨家将",        desc: "河南坠子",             trackIds: ["select_t1"],       category: "select", coverSrc: dailySelectCoverPath(0) },
  { id: "sel_a2", title: "杨府挑将",      desc: "濮阳大弦戏",           trackIds: ["select_t2"],       category: "select", coverSrc: dailySelectCoverPath(1) },
  { id: "sel_a3", title: "湖畔枫吟",      desc: "古琴",                 trackIds: ["select_t3"],       category: "select", coverSrc: dailySelectCoverPath(2) },
  { id: "guess_a1", title: "湘妃竹",      desc: "箜篌艺术",             trackIds: ["guess_t1"],        category: "guess",  coverSrc: dailyGuessCoverPath(0) },
  { id: "guess_a2", title: "火龙阵",      desc: "濮阳大弦戏",           trackIds: ["guess_t2"],        category: "guess",  coverSrc: dailyGuessCoverPath(1) },
  { id: "guess_a3", title: "神人畅",      desc: "古琴",                 trackIds: ["guess_t3"],        category: "guess",  coverSrc: dailyGuessCoverPath(2) },
];

export const HOT_BANNERS = [
  { id: "hb1", title: "丝路天籁", subtitle: "敦煌古乐 · 听见千年", imageSrc: homeSwiperBySlot(0) },
  { id: "hb2", title: "二胡新韵", subtitle: "传承人 × 青年艺术家", imageSrc: homeSwiperBySlot(1) },
  { id: "hb3", title: "古琴雅集", subtitle: "夏夜山林·云端听琴", imageSrc: homeSwiperBySlot(2) },
];

export const HOT_KEYWORDS = [
  "哑女告状", "抬花轿", "美美与共", "醉美玉见", "四平调", "唢呐", "箜篌", "九莲灯",
  "杨家将", "杨府挑将", "湖畔枫吟", "河南坠子", "濮阳大弦戏", "古琴",
  "湘妃竹", "火龙阵", "神人畅", "箜篌艺术",
];

export function albumTracks(album: Album): Track[] {
  return TRACKS.filter((t) => album.trackIds.includes(t.id));
}
