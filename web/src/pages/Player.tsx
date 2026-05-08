import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  ArrowLeft,
  Download,
  Heart,
  HeartFilled,
  ListIcon,
  Pause,
  Play,
  Repeat,
  Share,
  Shuffle,
  SkipBack,
  SkipForward,
} from "../components/Icon";
import Cover from "../components/Cover";
import Sheet from "../components/Sheet";
import { TRACKS, usePlayerStore } from "../store/playerStore";
import { toast } from "../components/Toast";

function fmt(s: number) {
  const m = Math.floor(s / 60);
  const r = Math.floor(s % 60);
  return `${String(m).padStart(2, "0")}:${String(r).padStart(2, "0")}`;
}

export default function Player() {
  const { currentIndex, isPlaying, positionSec, toggle, next, prev, seek, setIndex, tick } =
    usePlayerStore();
  const t = TRACKS[currentIndex];
  const [favList, setFavList] = useState<string[]>([]);
  const [listOpen, setListOpen] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (!isPlaying) return;
    const id = setInterval(tick, 1000);
    return () => clearInterval(id);
  }, [isPlaying, tick]);

  const fav = favList.includes(t.id);

  return (
    <div className="min-h-[100dvh] bg-player-screen text-player-control flex flex-col md:min-h-0 md:bg-transparent md:grid md:grid-cols-[1fr_360px] md:gap-6">
      <div
        className="flex items-center gap-2 px-3 py-2 md:hidden"
        style={{ paddingTop: "max(0.5rem, env(safe-area-inset-top))" }}
      >
        <button className="h-10 w-10 grid place-items-center" onClick={() => navigate(-1)}>
          <ArrowLeft size={22} />
        </button>
        <div className="flex-1 text-center">
          <div className="text-sm font-semibold text-text-primary">{t.title}</div>
          <div className="text-xs text-text-secondary">{t.artist} · {t.album}</div>
        </div>
        <button
          className="h-10 w-10 grid place-items-center"
          onClick={() => setListOpen(true)}
        >
          <ListIcon size={22} />
        </button>
      </div>

      <div className="flex-1 flex flex-col items-center justify-center px-6 md:bg-bg-card md:rounded-card md:border md:border-border/40 md:p-6">
        <Cover
          seed={t.id}
          src={t.coverSrc}
          alt={t.title}
          text={t.title}
          aspect="aspect-square"
          rounded="rounded-full"
          className="w-[78%] max-w-[320px] shadow-card ring-1 ring-ancient-bronze/60"
          ornate={false}
        />
        <div className="mt-6 text-center">
          <div className="text-xl font-bold text-text-primary">{t.title}</div>
          <div className="mt-1 text-xs text-text-secondary">{t.artist} · {t.album}</div>
        </div>

        <div className="mt-6 w-full">
          <input
            type="range"
            min={0}
            max={t.durationSec}
            value={positionSec}
            onChange={(e) => seek(Number(e.target.value))}
            className="w-full h-1"
            style={{ accentColor: "#B7382E" }}
          />
          <div className="flex justify-between text-[11px] text-text-secondary">
            <span className="tabular-nums">{fmt(positionSec)}</span>
            <span className="tabular-nums">{fmt(t.durationSec)}</span>
          </div>
        </div>

        <div className="mt-6 flex items-center justify-between w-full">
          <button className="h-10 w-10 grid place-items-center text-text-secondary hover:text-ancient-bronze transition-colors"><Shuffle size={20} /></button>
          <button className="h-12 w-12 grid place-items-center" onClick={prev}><SkipBack size={26} /></button>
          <button
            onClick={toggle}
            className="h-16 w-16 grid place-items-center rounded-full bg-ancient-cinnabar text-white shadow-stampInset hover:-translate-y-[1px] active:translate-y-0 transition-transform"
            aria-label={isPlaying ? "暂停" : "播放"}
          >
            {isPlaying ? <Pause size={28} /> : <Play size={28} />}
          </button>
          <button className="h-12 w-12 grid place-items-center" onClick={next}><SkipForward size={26} /></button>
          <button className="h-10 w-10 grid place-items-center text-text-secondary hover:text-ancient-bronze transition-colors"><Repeat size={20} /></button>
        </div>

        <div className="mt-6 flex items-center justify-around w-full">
          <button
            className="flex flex-col items-center gap-1 text-xs"
            onClick={() => {
              setFavList((cur) =>
                cur.includes(t.id) ? cur.filter((x) => x !== t.id) : [...cur, t.id],
              );
              toast(fav ? "已取消收藏" : "已加入我喜欢的音乐");
            }}
          >
            {fav ? <HeartFilled size={20} className="text-ancient-cinnabar" /> : <Heart size={20} />}
            <span>{fav ? "已收藏" : "收藏"}</span>
          </button>
          <button className="flex flex-col items-center gap-1 text-xs" onClick={() => toast("已加入下载队列")}>
            <Download size={20} />
            <span>下载</span>
          </button>
          <button className="flex flex-col items-center gap-1 text-xs" onClick={() => toast("分享链接已复制")}>
            <Share size={20} />
            <span>分享</span>
          </button>
        </div>
      </div>

      {/* 桌面版：常驻播放列表 */}
      <aside className="hidden md:block bg-bg-card rounded-card border border-border/40 p-4 max-h-[calc(100dvh-220px)] overflow-y-auto">
        <div className="flex items-center pb-2 px-1">
          <span className="inline-block w-1 h-4 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
          <div className="text-sm font-bold text-text-primary leading-tight">当前播放列表</div>
        </div>
        <div className="flex flex-col gap-1">
          {TRACKS.map((m, idx) => {
            const active = idx === currentIndex;
            return (
              <button
                key={m.id}
                onClick={() => setIndex(idx)}
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
      </aside>

      {/* 移动版：BottomSheet 播放列表 */}
      <div className="md:hidden">
        <Sheet open={listOpen} onClose={() => setListOpen(false)} title="当前播放列表">
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
                    active ? "bg-neon-teal/15 text-neon-teal" : "text-text-primary"
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
    </div>
  );
}
