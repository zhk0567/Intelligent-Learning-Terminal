import { useState } from "react";
import { useNavigate } from "react-router-dom";
import TopBar from "../components/TopBar";
import Cover from "../components/Cover";
import { TRACKS } from "../store/playerStore";
import { ALL_PRODUCTS } from "../data/shop";
import { STORIES } from "../data/story";

const TABS = ["音乐", "商品", "故事"] as const;

export default function Favorite() {
  const [tab, setTab] = useState<(typeof TABS)[number]>("音乐");
  const navigate = useNavigate();

  return (
    <div className="min-h-[100dvh] pb-6 md:min-h-0 md:pb-0 md:max-w-[820px] md:mx-auto">
      <TopBar title="我的收藏" />
      <div className="hidden md:flex items-center pb-3 px-1">
        <span className="inline-block w-1 h-6 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
        <h1 className="text-xl font-bold text-text-primary">我的收藏</h1>
      </div>
      <div className="px-3 pt-2 md:px-0 md:pt-0">
        <div className="flex gap-2">
          {TABS.map((t) => (
            <button
              key={t}
              onClick={() => setTab(t)}
              className={`px-3 py-1.5 rounded-full text-sm border transition-colors ${
                tab === t
                  ? "bg-ancient-cinnabar text-white border-ancient-cinnabar shadow-stampInset"
                  : "bg-bg-card text-text-secondary border-border/50 hover:border-ancient-bronze/60"
              }`}
            >
              {t}
            </button>
          ))}
        </div>

        <div className="mt-3 space-y-2">
          {tab === "音乐" &&
            TRACKS.slice(0, 4).map((t) => (
              <button
                key={t.id}
                onClick={() => navigate("/player")}
                className="flex w-full min-w-0 items-start gap-3 rounded-xl bg-bg-card border border-border/40 p-2 text-left card-hover"
              >
                <Cover
                  seed={t.id}
                  src={t.coverSrc}
                  alt={t.title}
                  text={t.title.slice(0, 2)}
                  aspect="aspect-square"
                  rounded="rounded-lg"
                  className="h-12 w-12 shrink-0"
                  ornate={false}
                />
                <div className="min-w-0 flex-1 self-stretch text-left">
                  <div className="truncate text-sm font-semibold text-text-primary">{t.title}</div>
                  <div className="truncate text-xs text-text-secondary">{t.artist}</div>
                </div>
              </button>
            ))}
          {tab === "商品" && (
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
              {ALL_PRODUCTS.slice(0, 6).map((p) => (
                <button
                  key={p.id}
                  onClick={() => navigate(`/shop/detail/${p.id}`)}
                  className="rounded-xl bg-bg-card border border-border/40 overflow-hidden text-left card-hover"
                >
                  <Cover
                    seed={p.id}
                    src={p.imageSrc}
                    alt={p.name}
                    text={p.name.slice(0, 4)}
                    aspect="aspect-square"
                    rounded="rounded-none"
                    ornate={false}
                  />
                  <div className="p-2">
                    <div className="line-clamp-1 text-sm text-text-primary">{p.name}</div>
                    <div className="text-ancient-cinnabar font-bold mt-1">¥{p.price}</div>
                  </div>
                </button>
              ))}
            </div>
          )}
          {tab === "故事" &&
            STORIES.slice(0, 4).map((s) => (
              <button
                key={s.id}
                onClick={() => navigate(`/story/${s.id}`)}
                className="flex w-full min-w-0 items-start gap-3 rounded-xl bg-bg-card border border-border/40 p-2 text-left card-hover"
              >
                <Cover
                  seed={s.id}
                  src={s.coverSrc}
                  alt={s.title}
                  text={s.title.slice(0, 2)}
                  aspect="aspect-square"
                  rounded="rounded-lg"
                  className="h-14 w-14 shrink-0"
                  ornate={false}
                />
                <div className="min-w-0 flex-1 self-stretch text-left">
                  <div className="truncate text-sm font-semibold text-text-primary">{s.title}</div>
                  <div className="line-clamp-2 text-xs text-text-secondary">{s.excerpt}</div>
                </div>
              </button>
            ))}
        </div>
      </div>
    </div>
  );
}
