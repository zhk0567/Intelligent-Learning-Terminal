import { useNavigate } from "react-router-dom";
import { Search, Filter } from "../components/Icon";
import { STORIES } from "../data/story";
import Cover from "../components/Cover";
import Section from "../components/Section";

export default function StoryTab() {
  const navigate = useNavigate();
  return (
    <div className="px-3 pt-2 md:px-0 md:pt-0">
      <div
        className="flex items-center gap-2 py-1 md:hidden"
        style={{ paddingTop: "max(0.25rem, env(safe-area-inset-top))" }}
      >
        <div className="text-xl font-bold text-text-primary px-1">非遗故事</div>
        <div className="flex-1" />
        <button className="h-10 w-10 grid place-items-center rounded-full bg-bg-card border border-border/60 text-text-primary"
          onClick={() => navigate("/story/list")}>
          <Filter size={18} />
        </button>
        <button className="h-10 w-10 grid place-items-center rounded-full bg-bg-card border border-border/60 text-text-primary"
          onClick={() => navigate("/search")}>
          <Search size={18} />
        </button>
      </div>

      <Section title="精选阅读" eyebrow="Featured Stories" framed={false}>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3 md:gap-4">
          {STORIES.slice(0, 2).map((s) => (
            <button key={s.id} onClick={() => navigate(`/story/${s.id}`)} className="text-left transition-transform hover:-translate-y-[2px] group">
              <Cover
                seed={s.id}
                src={s.coverSrc}
                alt={s.title}
                text={s.title}
                aspect="aspect-[16/9]"
                coverWidth={s.coverWidth}
                coverHeight={s.coverHeight}
                rounded="rounded-2xl"
                className="group-hover:ring-1 group-hover:ring-ancient-bronze/60"
                ornate={false}
              />
              <div className="mt-2 px-1">
                <div className="text-base font-bold text-text-primary group-hover:text-ancient-bronze transition-colors">{s.title}</div>
                <div className="mt-1 text-xs text-text-secondary line-clamp-2">{s.excerpt}</div>
                <div className="mt-1 flex items-center gap-2 text-xs text-text-hint">
                  <span>{s.author}</span>
                  <span>·</span>
                  <span>{s.publishTime}</span>
                  <span>·</span>
                  <span>{s.readCount} 阅读</span>
                </div>
              </div>
            </button>
          ))}
        </div>
      </Section>

      <Section title="更多故事" eyebrow="More Inspiration" framed={false}>
        <div className="space-y-3 md:grid md:grid-cols-2 lg:grid-cols-3 md:gap-4 md:space-y-0">
          {STORIES.slice(2).map((s) => (
            <button
              key={s.id}
              type="button"
              onClick={() => navigate(`/story/${s.id}`)}
              className="flex w-full min-w-0 items-start gap-3 rounded-xl bg-bg-card border border-border/40 p-3 text-left card-hover"
            >
              <Cover
                seed={s.id}
                src={s.coverSrc}
                alt={s.title}
                text={s.title.slice(0, 2)}
                aspect="aspect-square"
                coverWidth={s.coverWidth}
                coverHeight={s.coverHeight}
                rounded="rounded-lg"
                className="w-20 shrink-0"
                ornate={false}
              />
              <div className="min-w-0 flex-1 self-stretch">
                <div className="truncate text-sm font-semibold text-text-primary">{s.title}</div>
                <div className="line-clamp-2 mt-1 text-xs text-text-secondary">{s.excerpt}</div>
                <div className="mt-1 text-[11px] text-text-hint">{s.author} · {s.publishTime}</div>
              </div>
            </button>
          ))}
        </div>
      </Section>
    </div>
  );
}
