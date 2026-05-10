import { useNavigate } from "react-router-dom";
import { Bell, Play, Search } from "../components/Icon";
import Cover from "../components/Cover";
import Section from "../components/Section";
import { ALBUMS, HOT_BANNERS } from "../data/music";
import { BASIC_LESSONS, type BasicLesson } from "../data/lessons";
import { useEffect, useState } from "react";
import { trackIndexById, usePlayerStore } from "../store/playerStore";

function fmtDur(sec: number) {
  const m = Math.floor(sec / 60);
  const s = sec % 60;
  return `${m}:${String(s).padStart(2, "0")}`;
}

export default function MusicLib() {
  const navigate = useNavigate();
  const setIndex = usePlayerStore((s) => s.setIndex);
  const [bannerIdx, setBannerIdx] = useState(0);

  useEffect(() => {
    const id = setInterval(
      () => setBannerIdx((v) => (v + 1) % HOT_BANNERS.length),
      4000,
    );
    return () => clearInterval(id);
  }, []);

  const hot = ALBUMS.filter((a) => a.category === "hot");
  const select = ALBUMS.filter((a) => a.category === "select");
  const guess = ALBUMS.filter((a) => a.category === "guess");

  const playAlbumStart = (album: (typeof ALBUMS)[number]) => {
    const tid = album.trackIds[0];
    setIndex(tid ? trackIndexById(tid) : 0);
    navigate("/player");
  };

  return (
    <div className="px-3 pt-2 md:px-0 md:pt-0">
      <div
        className="flex items-center gap-2 py-1 md:hidden"
        style={{ paddingTop: "max(0.25rem, env(safe-area-inset-top))" }}
      >
        <div
          className="flex-1 flex items-center gap-2 rounded-full bg-bg-card border border-border/60 px-3 py-2 text-text-secondary"
          onClick={() => navigate("/search")}
        >
          <Search size={18} />
          <span className="text-sm">搜索曲目、艺人或专辑</span>
        </div>
        <button className="h-10 w-10 grid place-items-center rounded-full bg-bg-card border border-border/60 text-text-primary">
          <Bell size={20} />
        </button>
      </div>

      {/* Banner */}
      <div className="mt-3 md:mt-0 w-full overflow-hidden rounded-2xl border border-ancient-bronze/40 dark:border-ancient-bronze/30">
        <Cover
          seed={HOT_BANNERS[bannerIdx].id}
          src={HOT_BANNERS[bannerIdx].imageSrc}
          alt={HOT_BANNERS[bannerIdx].title}
          aspect="aspect-[16/9]"
          imgFit="contain"
          rounded="rounded-none"
          className="w-full border-0"
          ornate={false}
        />
        <div className="mt-2 text-center text-xs text-text-secondary">
          {HOT_BANNERS[bannerIdx].subtitle}
        </div>
        <div className="mt-2 flex justify-center gap-1">
          {HOT_BANNERS.map((_, i) => (
            <span
              key={i}
              className={`h-1.5 rounded-full transition-all ${
                i === bannerIdx ? "w-5 bg-neon-teal" : "w-1.5 bg-border/50"
              }`}
            />
          ))}
        </div>
      </div>

      <Section title="每日热门" eyebrow="Trending Today" action={<More onClick={() => navigate("/detail/hot")} />}>
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3 md:gap-4">
          {hot.map((a) => (
            <AlbumCard key={a.id} album={a} onPlay={() => playAlbumStart(a)} showDesc={false} />
          ))}
        </div>
      </Section>
      <Section title="每日精选" eyebrow="Editor's Picks" action={<More onClick={() => navigate("/detail/select")} />}>
        {/* 仅 3 张专辑：大屏勿用 4 列，避免右侧空一列 */}
        <div className="grid grid-cols-2 md:grid-cols-3 gap-3 md:gap-4">
          {select.map((a) => (
            <AlbumCard key={a.id} album={a} onPlay={() => playAlbumStart(a)} />
          ))}
        </div>
      </Section>
      <Section title="猜你喜欢" eyebrow="For You" action={<More onClick={() => navigate("/detail/guess")} />}>
        <div className="space-y-2 md:grid md:grid-cols-2 md:gap-3 md:space-y-0">
          {guess.map((a) => (
            <AlbumRow key={a.id} album={a} onPlay={() => playAlbumStart(a)} />
          ))}
        </div>
      </Section>
      <Section title="基础学习" eyebrow="Learn the Basics">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3 md:gap-4">
          {BASIC_LESSONS.map((l) => (
            <LessonCard key={l.id} lesson={l} onOpen={() => navigate(`/lesson/${l.id}`)} />
          ))}
        </div>
      </Section>
    </div>
  );
}

function LessonCard({ lesson, onOpen }: { lesson: BasicLesson; onOpen: () => void }) {
  return (
    <button
      type="button"
      onClick={onOpen}
      className="flex w-full min-w-0 items-stretch gap-3 rounded-xl bg-bg-cardElevated/60 border border-border/40 p-2 text-left card-hover"
    >
      <div className="relative w-32 md:w-40 shrink-0 overflow-hidden rounded-lg">
        <Cover
          seed={lesson.id}
          src={lesson.coverSrc}
          alt={lesson.title}
          aspect="aspect-video"
          rounded="rounded-lg"
          className="w-full"
          ornate={false}
        />
        <span className="absolute right-1.5 bottom-1.5 inline-flex items-center gap-1 rounded-full bg-black/60 text-white text-[11px] px-2 py-0.5">
          <Play size={10} />
          {fmtDur(lesson.durationSec)}
        </span>
      </div>
      <div className="flex-1 min-w-0 self-stretch flex flex-col gap-1">
        <div className="truncate text-sm font-semibold text-text-primary">{lesson.title}</div>
        <div className="truncate text-xs text-text-secondary">{lesson.artist}</div>
        <div className="text-xs text-text-secondary line-clamp-2">{lesson.desc}</div>
      </div>
    </button>
  );
}

function More({ onClick }: { onClick: () => void }) {
  return (
    <button onClick={onClick} className="text-xs text-text-hint">查看更多 ›</button>
  );
}

function AlbumCard({
  album,
  onPlay,
  showDesc = true,
}: {
  album: (typeof ALBUMS)[number];
  onPlay: () => void;
  /** 「每日热门」首页只展示曲名；其他位置默认带乐器/流派副标题。 */
  showDesc?: boolean;
}) {
  return (
    <button
      className="group flex flex-col gap-2 text-left transition-transform hover:-translate-y-[2px]"
      onClick={onPlay}
    >
      <Cover
        seed={album.id}
        src={album.coverSrc}
        alt={album.title}
        text={album.title}
        aspect="aspect-square"
        rounded="rounded-xl"
        className="group-hover:ring-1 group-hover:ring-ancient-bronze/60 transition-all"
        ornate={false}
      />
      <div>
        <div className="truncate text-sm font-semibold text-text-primary group-hover:text-ancient-bronze transition-colors">{album.title}</div>
        {showDesc && (
          <div className="truncate text-xs text-text-secondary">{album.desc}</div>
        )}
      </div>
    </button>
  );
}

function AlbumRow({ album, onPlay }: { album: (typeof ALBUMS)[number]; onPlay: () => void }) {
  return (
    <button
      type="button"
      onClick={onPlay}
      className="flex w-full min-w-0 items-start gap-3 rounded-xl bg-bg-cardElevated/60 border border-border/40 p-2 text-left card-hover"
    >
      <Cover
        seed={album.id}
        src={album.coverSrc}
        alt={album.title}
        aspect="aspect-square"
        rounded="rounded-lg"
        className="h-14 w-14 shrink-0"
        text={album.title.slice(0, 1)}
        ornate={false}
      />
      <div className="flex-1 min-w-0 text-left">
        <div className="truncate text-sm font-semibold text-text-primary">{album.title}</div>
        <div className="truncate text-xs text-text-secondary">{album.desc}</div>
      </div>
      <span className="text-xs text-ancient-cinnabar font-semibold">立即播放</span>
    </button>
  );
}
