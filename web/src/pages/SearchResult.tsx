import { useMemo, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import TopBar from "../components/TopBar";
import { TRACKS, usePlayerStore } from "../store/playerStore";
import { ALL_PRODUCTS } from "../data/shop";
import { STORIES } from "../data/story";
import Cover from "../components/Cover";
import EmptyState from "../components/EmptyState";

const TABS = ["全部", "音乐", "故事", "商品"] as const;
type Tab = (typeof TABS)[number];

export default function SearchResult() {
  const [params] = useSearchParams();
  const q = (params.get("q") ?? "").trim();
  const [tab, setTab] = useState<Tab>("全部");
  const navigate = useNavigate();
  const setIndex = usePlayerStore((s) => s.setIndex);

  const matches = useMemo(() => {
    const kw = q.toLowerCase();
    const music = TRACKS.filter(
      (t) => t.title.includes(q) || t.artist.includes(q) || t.album.includes(q),
    );
    const story = STORIES.filter(
      (s) => s.title.includes(q) || s.author.includes(q) || s.tags.some((t) => t.includes(q)),
    );
    const goods = ALL_PRODUCTS.filter(
      (p) =>
        p.name.includes(q) ||
        p.tags.some((t) => t.includes(q)) ||
        p.description.toLowerCase().includes(kw),
    );
    return { music, story, goods };
  }, [q]);

  const total = matches.music.length + matches.story.length + matches.goods.length;

  return (
    <div className="min-h-[100dvh] md:min-h-0 md:max-w-[820px] md:mx-auto">
      <TopBar title={`搜索：${q || "全部"}`} subtitle={`共 ${total} 条结果`} />
      <div className="hidden md:flex items-center pb-3 px-1">
        <span className="inline-block w-1 h-6 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
        <h1 className="text-xl font-bold text-text-primary">
          搜索：{q || "全部"}
          <span className="text-sm text-text-hint font-normal"> · 共 {total} 条结果</span>
        </h1>
      </div>
      <div className="px-3 pt-2 md:px-0 md:pt-0">
        <div className="flex gap-2 overflow-x-auto no-scrollbar">
          {TABS.map((t) => (
            <button
              key={t}
              onClick={() => setTab(t)}
              className={`px-3 py-1.5 rounded-full text-sm whitespace-nowrap border transition-colors ${
                tab === t
                  ? "bg-ancient-cinnabar text-white border-ancient-cinnabar shadow-stampInset"
                  : "bg-bg-card text-text-secondary border-border/50 hover:border-ancient-bronze/60"
              }`}
            >
              {t}
            </button>
          ))}
        </div>

        {(tab === "全部" || tab === "音乐") && matches.music.length > 0 && (
          <Section title="音乐">
            <div className="space-y-2">
              {matches.music.map((m, i) => (
                <button
                  key={m.id}
                  onClick={() => {
                    setIndex(TRACKS.findIndex((x) => x.id === m.id));
                    navigate("/player");
                  }}
                  className="flex w-full min-w-0 items-start gap-3 rounded-xl bg-bg-card border border-border/40 p-2 text-left card-hover"
                >
                  <Cover
                    seed={m.id}
                    src={m.coverSrc}
                    alt={m.title}
                    text={m.title.slice(0, 2)}
                    aspect="aspect-square"
                    rounded="rounded-lg"
                    className="h-12 w-12 shrink-0"
                    ornate={false}
                  />
                  <div className="min-w-0 flex-1 self-stretch text-left">
                    <div className="truncate text-sm font-semibold text-text-primary">{m.title}</div>
                    <div className="truncate text-xs text-text-secondary">{m.artist} · {m.album}</div>
                  </div>
                  <span className="shrink-0 text-xs text-text-hint">#{i + 1}</span>
                </button>
              ))}
            </div>
          </Section>
        )}

        {(tab === "全部" || tab === "故事") && matches.story.length > 0 && (
          <Section title="故事">
            <div className="space-y-2">
              {matches.story.map((s) => (
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
          </Section>
        )}

        {(tab === "全部" || tab === "商品") && matches.goods.length > 0 && (
          <Section title="商品">
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
              {matches.goods.map((p) => (
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
                    <div className="line-clamp-1 text-sm font-semibold text-text-primary">{p.name}</div>
                    <div className="text-ancient-cinnabar text-base font-bold mt-1">¥{p.price}</div>
                  </div>
                </button>
              ))}
            </div>
          </Section>
        )}

        {total === 0 && (
          <EmptyState
            text="未找到相关内容"
            hint="换个关键词或试试热门搜索"
            icon="kite"
          />
        )}
      </div>
    </div>
  );
}

function Section({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <section className="mt-4">
      <div className="px-1 pb-2 flex items-center">
        <span className="inline-block w-1 h-4 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
        <div className="text-sm font-bold text-text-primary">{title}</div>
      </div>
      {children}
    </section>
  );
}
