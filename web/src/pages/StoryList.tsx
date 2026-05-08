import { useNavigate } from "react-router-dom";
import TopBar from "../components/TopBar";
import Cover from "../components/Cover";
import { STORIES } from "../data/story";

export default function StoryList() {
  const navigate = useNavigate();
  return (
    <div className="min-h-[100dvh] md:min-h-0 md:max-w-[820px] md:mx-auto">
      <TopBar title="故事列表" subtitle={`共 ${STORIES.length} 篇`} />
      <div className="hidden md:flex items-center pb-3 px-1">
        <span className="inline-block w-1 h-6 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
        <h1 className="text-xl font-bold text-text-primary">
          故事列表
          <span className="text-sm text-text-hint font-normal"> · 共 {STORIES.length} 篇</span>
        </h1>
      </div>
      <div className="px-3 pt-2 pb-6 md:px-0 md:pt-0 md:pb-0 space-y-3 md:grid md:grid-cols-2 lg:grid-cols-3 md:gap-4 md:space-y-0">
        {STORIES.map((s) => (
          <button
            key={s.id}
            onClick={() => navigate(`/story/${s.id}`)}
            className="w-full rounded-2xl border border-border/50 bg-bg-card overflow-hidden text-left card-hover group"
          >
            <Cover
              seed={s.id}
              src={s.coverSrc}
              alt={s.title}
              text={s.title}
              aspect="aspect-[16/9]"
              rounded="rounded-none"
              ornate={false}
            />
            <div className="p-3">
              <div className="text-base font-bold text-text-primary group-hover:text-ancient-bronze transition-colors">{s.title}</div>
              <div className="mt-1 line-clamp-2 text-xs text-text-secondary">{s.excerpt}</div>
              <div className="mt-2 flex items-center justify-between text-[11px] text-text-hint">
                <span>{s.author} · {s.publishTime}</span>
                <span><span className="text-ancient-cinnabar">♥</span> {s.likeCount} · 评论 {s.commentCount}</span>
              </div>
              <div className="mt-2 flex flex-wrap gap-1">
                {s.tags.map((t) => (
                  <span
                    key={t}
                    className="rounded-full border border-ancient-bronze/40 text-ancient-bronze text-[10px] px-2 py-0.5"
                  >
                    {t}
                  </span>
                ))}
              </div>
            </div>
          </button>
        ))}
      </div>
    </div>
  );
}
