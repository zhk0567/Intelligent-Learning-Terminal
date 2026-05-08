import { create } from "zustand";
import { APP_IMAGES } from "../assets/appImages";

export interface Track {
  id: string;
  title: string;
  artist: string;
  album: string;
  durationSec: number;
  /** 与 `PlayerSyncState` / `MusicLibActivity` 曲目缩略图一致，轮用 banner。 */
  coverSrc: string;
}

const B = [APP_IMAGES.banner1, APP_IMAGES.banner2, APP_IMAGES.banner3] as const;
const tc = (i: number) => B[i % 3];

export const TRACKS: Track[] = [
  {
    id: "t1",
    title: "高山流水",
    artist: "古筝演奏",
    album: "国风雅集",
    durationSec: 248,
    coverSrc: tc(0),
  },
  {
    id: "t2",
    title: "广陵散",
    artist: "古琴独奏",
    album: "国风雅集",
    durationSec: 312,
    coverSrc: tc(1),
  },
  {
    id: "t3",
    title: "二泉映月",
    artist: "二胡演奏",
    album: "民乐经典",
    durationSec: 270,
    coverSrc: tc(2),
  },
  {
    id: "t4",
    title: "梅花三弄",
    artist: "笛子独奏",
    album: "民乐经典",
    durationSec: 226,
    coverSrc: tc(3),
  },
  {
    id: "t5",
    title: "渔舟唱晚",
    artist: "古筝演奏",
    album: "国风雅集",
    durationSec: 295,
    coverSrc: tc(4),
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
  tick: () => {
    const { isPlaying, positionSec, currentIndex } = get();
    if (!isPlaying) return;
    const t = TRACKS[currentIndex];
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
