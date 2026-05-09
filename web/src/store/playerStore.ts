import { create } from "zustand";
import { assetUrl } from "../lib/assetUrl";

export interface Track {
  id: string;
  title: string;
  artist: string;
  album: string;
  durationSec: number;
  /** 与 `PlayerSyncState` 一致：线上 `/images/banner{n}_img.jpg` 轮播。 */
  coverSrc: string;
  /** 线上音频：`public/audio/classic|daily_*` 下 mp3，构建进 `dist/audio/`。 */
  audioSrc?: string;
}

/** 国风前五首：封面与 Web `public/images/banner{n}_img.jpg` 一致。 */
function classicTrackCover(i: number): string {
  const n = (i % 3) + 1;
  return assetUrl(`/images/banner${n}_img.jpg`);
}

/** 国风前五首：`public/audio/classic/classic_01.mp3` … `classic_05.mp3`。 */
function classicTrackAudio(i: number): string {
  const n = i + 1;
  const pad = n < 10 ? `0${n}` : `${n}`;
  return assetUrl(`/audio/classic/classic_${pad}.mp3`);
}

/** `data/图片/每日热门` → `tools/sync_daily_hot_covers.py` */
export function dailyHotCoverPath(slot: 0 | 1 | 2 | 3): string {
  const n = slot + 1;
  return assetUrl(`/images/daily_hot/daily_hot_cover_${n < 10 ? `0${n}` : `${n}`}.jpg`);
}

/** `data/图片/每日精选` → `tools/sync_daily_select_covers.py` */
export function dailySelectCoverPath(slot: 0 | 1 | 2): string {
  const n = slot + 1;
  return assetUrl(`/images/daily_select/daily_select_cover_${n < 10 ? `0${n}` : `${n}`}.jpg`);
}

/** `data/图片/猜你喜欢` → `tools/sync_daily_guess_covers.py` */
export function dailyGuessCoverPath(slot: 0 | 1 | 2): string {
  const n = slot + 1;
  return assetUrl(`/images/daily_guess/daily_guess_cover_${n < 10 ? `0${n}` : `${n}`}.jpg`);
}

export const TRACKS: Track[] = [
  {
    id: "t1",
    title: "高山流水",
    artist: "古筝演奏",
    album: "国风雅集",
    durationSec: 248,
    coverSrc: classicTrackCover(0),
    audioSrc: classicTrackAudio(0),
  },
  {
    id: "t2",
    title: "广陵散",
    artist: "古琴独奏",
    album: "国风雅集",
    durationSec: 312,
    coverSrc: classicTrackCover(1),
    audioSrc: classicTrackAudio(1),
  },
  {
    id: "t3",
    title: "二泉映月",
    artist: "二胡演奏",
    album: "民乐经典",
    durationSec: 270,
    coverSrc: classicTrackCover(2),
    audioSrc: classicTrackAudio(2),
  },
  {
    id: "t4",
    title: "梅花三弄",
    artist: "笛子独奏",
    album: "民乐经典",
    durationSec: 226,
    coverSrc: classicTrackCover(3),
    audioSrc: classicTrackAudio(3),
  },
  {
    id: "t5",
    title: "渔舟唱晚",
    artist: "古筝演奏",
    album: "国风雅集",
    durationSec: 295,
    coverSrc: classicTrackCover(4),
    audioSrc: classicTrackAudio(4),
  },
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

interface PlayerState {
  currentIndex: number;
  isPlaying: boolean;
  positionSec: number;
  setIndex: (i: number) => void;
  toggle: () => void;
  next: () => void;
  prev: () => void;
  seek: (sec: number) => void;
  setPositionSec: (sec: number) => void;
  tick: () => void;
}

export const usePlayerStore = create<PlayerState>((set, get) => ({
  currentIndex: 0,
  isPlaying: false,
  positionSec: 0,
  setIndex: (i) =>
    set({ currentIndex: ((i % TRACKS.length) + TRACKS.length) % TRACKS.length, positionSec: 0 }),
  toggle: () => set({ isPlaying: !get().isPlaying }),
  next: () => get().setIndex(get().currentIndex + 1),
  prev: () => get().setIndex(get().currentIndex - 1),
  seek: (sec) => set({ positionSec: sec }),
  setPositionSec: (sec) => {
    const t = TRACKS[get().currentIndex];
    set({ positionSec: Math.max(0, Math.min(sec, t.durationSec)) });
  },
  tick: () => {
    const { isPlaying, positionSec, currentIndex } = get();
    if (!isPlaying) return;
    const t = TRACKS[currentIndex];
    if (t.audioSrc) return;
    if (positionSec + 1 >= t.durationSec) {
      get().next();
      return;
    }
    set({ positionSec: positionSec + 1 });
  },
}));

export function currentTrack(): Track {
  return TRACKS[usePlayerStore.getState().currentIndex];
}

export function trackIndexById(id: string): number {
  const i = TRACKS.findIndex((t) => t.id === id);
  return i < 0 ? 0 : i;
}
