import { useEffect, useRef } from "react";
import { assetUrl } from "../lib/assetUrl";
import { globalPlayerAudio } from "../lib/globalPlayerAudio";
import { TRACKS, usePlayerStore } from "../store/playerStore";

/**
 * 迷你条 / 首页在未打开 `/player` 时也需要可播放的 `<audio>`；
 * 全屏页与共用此逻辑，避免重复实例。
 */
export default function GlobalAudioSink() {
  const currentIndex = usePlayerStore((s) => s.currentIndex);
  const isPlaying = usePlayerStore((s) => s.isPlaying);
  const tick = usePlayerStore((s) => s.tick);
  const audioRef = useRef<HTMLAudioElement | null>(null);

  const t = TRACKS[currentIndex];

  useEffect(() => {
    if (t.audioSrc) return;
    if (!isPlaying) return;
    const id = setInterval(tick, 1000);
    return () => clearInterval(id);
  }, [isPlaying, tick, t.audioSrc, currentIndex]);

  useEffect(() => {
    const tr = TRACKS[currentIndex];
    if (!tr.audioSrc) {
      const prev = audioRef.current;
      if (prev) {
        prev.pause();
        prev.src = "";
        audioRef.current = null;
      }
      globalPlayerAudio.current = null;
      return;
    }
    const prev = audioRef.current;
    prev?.pause();
    const a = new Audio(assetUrl(tr.audioSrc));
    audioRef.current = a;
    globalPlayerAudio.current = a;
    const onTime = () => usePlayerStore.getState().setPositionSec(Math.floor(a.currentTime));
    const onEnded = () => usePlayerStore.getState().next();
    a.addEventListener("timeupdate", onTime);
    a.addEventListener("ended", onEnded);
    return () => {
      a.removeEventListener("timeupdate", onTime);
      a.removeEventListener("ended", onEnded);
      a.pause();
      a.src = "";
      if (audioRef.current === a) {
        audioRef.current = null;
        globalPlayerAudio.current = null;
      }
    };
  }, [currentIndex]);

  useEffect(() => {
    const a = audioRef.current;
    const tr = TRACKS[currentIndex];
    if (!a || !tr.audioSrc) return;
    if (isPlaying) void a.play().catch(() => {});
    else a.pause();
  }, [isPlaying, currentIndex]);

  return null;
}
