import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { ListIcon, Pause, Play, SkipForward } from "../components/Icon";
import { TRACKS, usePlayerStore } from "../store/playerStore";
import Cover from "../components/Cover";

interface Props {
  onListClick?: () => void;
}

export default function MiniPlayer({ onListClick }: Props) {
  const { currentIndex, isPlaying, positionSec, toggle, next, tick } = usePlayerStore();
  const navigate = useNavigate();
  const t = TRACKS[currentIndex];
  const ratio = Math.max(0, Math.min(1, positionSec / t.durationSec));

  useEffect(() => {
    if (!isPlaying) return;
    const id = setInterval(tick, 1000);
    return () => clearInterval(id);
  }, [isPlaying, tick]);

  return (
    <div className="fixed bottom-16 left-1/2 -translate-x-1/2 z-30 w-full max-w-phone px-3">
      <div
        className="flex min-w-0 items-center gap-3 rounded-2xl bg-mini-surface border border-border/60 px-3 py-2 shadow-card"
        onClick={() => navigate("/player")}
      >
        <Cover
          seed={t.id}
          src={t.coverSrc}
          alt={t.title}
          text={t.title.slice(0, 2)}
          aspect="aspect-square"
          rounded="rounded-xl"
          className="h-11 w-11 shrink-0"
          ornate={false}
        />
        <div className="flex-1 min-w-0">
          <div className="truncate text-sm font-semibold text-text-primary">{t.title}</div>
          <div className="truncate text-xs text-text-secondary">{t.artist}</div>
          <div className="mt-1 relative h-0.5 w-full bg-border/40 rounded-full overflow-visible">
            <div
              className="h-full bg-ancient-cinnabar rounded-full"
              style={{ width: `${ratio * 100}%` }}
            />
            <span
              aria-hidden
              className="absolute -translate-y-1/2 top-1/2 h-2 w-2 rounded-full bg-ancient-bronze ring-2 ring-bg-card"
              style={{ left: `calc(${ratio * 100}% - 4px)` }}
            />
          </div>
        </div>
        <button
          aria-label={isPlaying ? "暂停" : "播放"}
          className="h-9 w-9 grid place-items-center rounded-full text-player-control"
          onClick={(e) => {
            e.stopPropagation();
            toggle();
          }}
        >
          {isPlaying ? <Pause size={22} /> : <Play size={22} />}
        </button>
        <button
          aria-label="下一首"
          className="h-9 w-9 grid place-items-center text-player-control"
          onClick={(e) => {
            e.stopPropagation();
            next();
          }}
        >
          <SkipForward size={20} />
        </button>
        <button
          aria-label="播放列表"
          className="h-9 w-9 grid place-items-center text-player-control"
          onClick={(e) => {
            e.stopPropagation();
            onListClick?.();
          }}
        >
          <ListIcon size={20} />
        </button>
      </div>
    </div>
  );
}
