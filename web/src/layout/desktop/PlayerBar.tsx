import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Heart,
  HeartFilled,
  ListIcon,
  Pause,
  Play,
  Repeat,
  Shuffle,
  SkipBack,
  SkipForward,
} from "../../components/Icon";
import Cover from "../../components/Cover";
import Sheet from "../../components/Sheet";
import { TRACKS, usePlayerStore } from "../../store/playerStore";

function fmt(s: number) {
  const m = Math.floor(s / 60);
  const r = Math.floor(s % 60);
  return `${String(m).padStart(2, "0")}:${String(r).padStart(2, "0")}`;
}

export default function PlayerBar() {
  const { currentIndex, isPlaying, positionSec, toggle, next, prev, seek, setIndex, tick } =
    usePlayerStore();
  const t = TRACKS[currentIndex];
  const [fav, setFav] = useState<string[]>([]);
  const [listOpen, setListOpen] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (!isPlaying) return;
    const id = setInterval(tick, 1000);
    return () => clearInterval(id);
  }, [isPlaying, tick]);

  const isFav = fav.includes(t.id);

  return (
    <div
      className="hidden md:grid grid-cols-[1fr_2fr_1fr] items-center gap-4 h-20 px-5 border-t border-border/40 bg-bg-card"
      style={{ gridArea: "player" }}
    >
      <div className="flex min-w-0 items-center gap-3">
        <Cover
          seed={t.id}
          src={t.coverSrc}
          alt={t.title}
          text={t.title.slice(0, 2)}
          aspect="aspect-square"
          rounded="rounded-lg"
          className="h-12 w-12 shrink-0 cursor-pointer"
          style={{ cursor: "pointer" }}
          ornate={false}
        />
        <button
          onClick={() => navigate("/player")}
          className="min-w-0 flex-1 text-left"
        >
          <div className="truncate text-sm font-semibold text-text-primary">{t.title}</div>
          <div className="truncate text-xs text-text-secondary">{t.artist} · {t.album}</div>
        </button>
        <button
          type="button"
          onClick={() => setFav((cur) => (cur.includes(t.id) ? cur.filter((x) => x !== t.id) : [...cur, t.id]))}
          className="grid h-9 w-9 shrink-0 place-items-center"
          aria-label="收藏"
        >
          {isFav ? <HeartFilled size={18} className="text-ancient-cinnabar" /> : <Heart size={18} className="text-text-hint" />}
        </button>
      </div>

      <div className="flex flex-col items-center gap-1.5">
        <div className="flex items-center gap-3">
          <button className="h-8 w-8 grid place-items-center text-text-hint hover:text-text-primary"><Shuffle size={16} /></button>
          <button onClick={prev} className="h-9 w-9 grid place-items-center text-text-primary"><SkipBack size={20} /></button>
          <button
            onClick={toggle}
            className="h-10 w-10 grid place-items-center rounded-full bg-ancient-cinnabar text-white shadow-stampInset hover:-translate-y-[1px] active:translate-y-0 transition-transform"
            aria-label={isPlaying ? "暂停" : "播放"}
          >
            {isPlaying ? <Pause size={20} /> : <Play size={20} />}
          </button>
          <button onClick={next} className="h-9 w-9 grid place-items-center text-text-primary"><SkipForward size={20} /></button>
          <button className="h-8 w-8 grid place-items-center text-text-hint hover:text-text-primary"><Repeat size={16} /></button>
        </div>
        <div className="flex items-center gap-2 w-full max-w-[420px]">
          <span className="text-[10px] text-text-hint w-9 text-right tabular-nums">{fmt(positionSec)}</span>
          <input
            type="range"
            min={0}
            max={t.durationSec}
            value={positionSec}
            onChange={(e) => seek(Number(e.target.value))}
            className="flex-1 h-1 accent-ancient-cinnabar"
            style={{ accentColor: "#B7382E" }}
          />
          <span className="text-[10px] text-text-hint w-9 tabular-nums">{fmt(t.durationSec)}</span>
        </div>
      </div>

      <div className="flex items-center justify-end gap-2">
        <button
          onClick={() => setListOpen(true)}
          className="h-9 w-9 grid place-items-center rounded-full text-text-primary hover:bg-bg-cardElevated/60"
          aria-label="播放列表"
        >
          <ListIcon size={18} />
        </button>
        <button
          onClick={() => navigate("/player")}
          className="btn-ghost rounded-full px-3 py-1.5 text-xs"
        >
          打开播放器
        </button>
      </div>

      <Sheet open={listOpen} onClose={() => setListOpen(false)} title="播放列表">
        <div className="flex flex-col gap-1">
          {TRACKS.map((m, idx) => {
            const active = idx === currentIndex;
            return (
              <button
                key={m.id}
                onClick={() => {
                  setIndex(idx);
                  setListOpen(false);
                }}
                className={`flex items-center gap-3 rounded-xl px-3 py-2 text-left ${
                  active ? "bg-neon-teal/15 text-neon-teal" : "text-text-primary hover:bg-bg-cardElevated/60"
                }`}
              >
                <span className="w-6 text-center text-xs">{idx + 1}</span>
                <div className="flex-1 min-w-0">
                  <div className="truncate text-sm font-semibold">{m.title}</div>
                  <div className="truncate text-xs opacity-70">{m.artist}</div>
                </div>
                <span className="text-xs">{fmt(m.durationSec)}</span>
              </button>
            );
          })}
        </div>
      </Sheet>
    </div>
  );
}
