import { createStore } from "../utils/store";
import { TRACKS, Track } from "../data/tracks";

interface PlayerState {
  currentIndex: number;
  isPlaying: boolean;
  positionSec: number;
}

export const playerStore = createStore<
  PlayerState,
  {
    setIndex: (i: number) => void;
    toggle: () => void;
    next: () => void;
    prev: () => void;
    seek: (sec: number) => void;
    setPositionSec: (sec: number) => void;
    tick: () => void;
  }
>({
  state: {
    currentIndex: 0,
    isPlaying: false,
    positionSec: 0,
  },
  actions: (set, get) => ({
    setIndex(i) {
      const len = TRACKS.length;
      const idx = ((i % len) + len) % len;
      set({ currentIndex: idx, positionSec: 0 });
    },
    toggle() {
      set({ isPlaying: !get().isPlaying });
    },
    next() {
      this.setIndex(get().currentIndex + 1);
    },
    prev() {
      this.setIndex(get().currentIndex - 1);
    },
    seek(sec) {
      set({ positionSec: Math.max(0, sec) });
    },
    setPositionSec(sec) {
      const t = TRACKS[get().currentIndex];
      const max = t.durationSec;
      set({ positionSec: Math.max(0, Math.min(sec, max)) });
    },
    tick() {
      const { isPlaying, positionSec, currentIndex } = get();
      if (!isPlaying) return;
      const t = TRACKS[currentIndex];
      if (t.audioSrc) return;
      if (positionSec + 1 >= t.durationSec) {
        this.next();
        return;
      }
      set({ positionSec: positionSec + 1 });
    },
  }),
});

/** 启动一个全局 1s tick：与 web 同步进度。 */
let timer: any = null;
export function startPlayerTicker() {
  if (timer) return;
  timer = setInterval(() => playerStore.actions.tick(), 1000);
}
export function stopPlayerTicker() {
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
}

export function currentTrack(): Track {
  return TRACKS[playerStore.get().currentIndex];
}
