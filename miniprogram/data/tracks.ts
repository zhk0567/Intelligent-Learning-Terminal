import { APP_IMAGES } from "./appImages";
import { assetUrl } from "../utils/assetUrl";

export interface Track {
  id: string;
  title: string;
  artist: string;
  album: string;
  durationSec: number;
  /** 与 web `PlayerStore.TRACKS` 同序：轮用三张 banner。 */
  coverSrc: string;
  /** 本地音频（`data/音频/每日热门|每日精选|猜你喜欢` → `tools/sync_daily_*_audio.py`）。 */
  audioSrc?: string;
}

const B = [APP_IMAGES.banner1, APP_IMAGES.banner2, APP_IMAGES.banner3] as const;
const tc = (i: number) => B[i % 3];

/** `data/图片/每日热门` → `tools/sync_daily_hot_covers.py`，与 `daily_hot_0n.mp3` 同序。 */
export function dailyHotCoverPath(slot: 0 | 1 | 2 | 3): string {
  const n = slot + 1;
  return assetUrl(`/images/daily_hot/daily_hot_cover_${n < 10 ? `0${n}` : `${n}`}.jpg`);
}

/** `data/图片/每日精选` → `tools/sync_daily_select_covers.py`，与 `daily_select_0n.mp3` 同序。 */
export function dailySelectCoverPath(slot: 0 | 1 | 2): string {
  const n = slot + 1;
  return assetUrl(`/images/daily_select/daily_select_cover_${n < 10 ? `0${n}` : `${n}`}.jpg`);
}

/** `data/图片/猜你喜欢` → `tools/sync_daily_guess_covers.py`，与 `daily_guess_0n.mp3` 同序。 */
export function dailyGuessCoverPath(slot: 0 | 1 | 2): string {
  const n = slot + 1;
  return assetUrl(`/images/daily_guess/daily_guess_cover_${n < 10 ? `0${n}` : `${n}`}.jpg`);
}

/** 顺序与 `data/音频/每日热门` 排序后 `daily_hot_*.mp3` 一致；曲名「-」前，乐器「-」后。 */
export const TRACKS: Track[] = [
  { id: "t1", title: "高山流水", artist: "古筝演奏", album: "国风雅集", durationSec: 248, coverSrc: tc(0) },
  { id: "t2", title: "广陵散", artist: "古琴独奏", album: "国风雅集", durationSec: 312, coverSrc: tc(1) },
  { id: "t3", title: "二泉映月", artist: "二胡演奏", album: "民乐经典", durationSec: 270, coverSrc: tc(2) },
  { id: "t4", title: "梅花三弄", artist: "笛子独奏", album: "民乐经典", durationSec: 226, coverSrc: tc(3) },
  { id: "t5", title: "渔舟唱晚", artist: "古筝演奏", album: "国风雅集", durationSec: 295, coverSrc: tc(4) },
  {
    id: "hot_t1",
    title: "哑女告状",
    artist: "四平调",
    album: "每日热门",
    durationSec: 240,
    coverSrc: dailyHotCoverPath(0),
    audioSrc: assetUrl("/audio/daily_hot/daily_hot_01.mp3"),
  },
  {
    id: "hot_t2",
    title: "抬花轿",
    artist: "沁阳唢呐",
    album: "每日热门",
    durationSec: 240,
    coverSrc: dailyHotCoverPath(1),
    audioSrc: assetUrl("/audio/daily_hot/daily_hot_02.mp3"),
  },
  {
    id: "hot_t3",
    title: "美美与共",
    artist: "箜篌艺术",
    album: "每日热门",
    durationSec: 240,
    coverSrc: dailyHotCoverPath(2),
    audioSrc: assetUrl("/audio/daily_hot/daily_hot_03.mp3"),
  },
  {
    id: "hot_t4",
    title: "醉美玉见",
    artist: "九莲灯",
    album: "每日热门",
    durationSec: 240,
    coverSrc: dailyHotCoverPath(3),
    audioSrc: assetUrl("/audio/daily_hot/daily_hot_04.mp3"),
  },
  {
    id: "select_t1",
    title: "杨家将",
    artist: "河南坠子",
    album: "每日精选",
    durationSec: 69,
    coverSrc: dailySelectCoverPath(0),
    audioSrc: assetUrl("/audio/daily_select/daily_select_01.mp3"),
  },
  {
    id: "select_t2",
    title: "杨府挑将",
    artist: "濮阳大弦戏",
    album: "每日精选",
    durationSec: 147,
    coverSrc: dailySelectCoverPath(1),
    audioSrc: assetUrl("/audio/daily_select/daily_select_02.mp3"),
  },
  {
    id: "select_t3",
    title: "湖畔枫吟",
    artist: "古琴",
    album: "每日精选",
    durationSec: 93,
    coverSrc: dailySelectCoverPath(2),
    audioSrc: assetUrl("/audio/daily_select/daily_select_03.mp3"),
  },
  {
    id: "guess_t1",
    title: "湘妃竹",
    artist: "箜篌艺术",
    album: "猜你喜欢",
    durationSec: 217,
    coverSrc: dailyGuessCoverPath(0),
    audioSrc: assetUrl("/audio/daily_guess/daily_guess_01.mp3"),
  },
  {
    id: "guess_t2",
    title: "火龙阵",
    artist: "濮阳大弦戏",
    album: "猜你喜欢",
    durationSec: 175,
    coverSrc: dailyGuessCoverPath(1),
    audioSrc: assetUrl("/audio/daily_guess/daily_guess_02.mp3"),
  },
  {
    id: "guess_t3",
    title: "神人畅",
    artist: "古琴",
    album: "猜你喜欢",
    durationSec: 162,
    coverSrc: dailyGuessCoverPath(2),
    audioSrc: assetUrl("/audio/daily_guess/daily_guess_03.mp3"),
  },
];

export function tracksByIds(ids: string[]): Track[] {
  return TRACKS.filter((t) => ids.includes(t.id));
}

export function trackIndexById(id: string): number {
  const i = TRACKS.findIndex((t) => t.id === id);
  return i < 0 ? 0 : i;
}
