import { useState } from "react";
import { useNavigate } from "react-router-dom";
import TopBar from "../components/TopBar";
import Cover from "../components/Cover";
import { POSTS } from "../data/post";
import { Plus } from "../components/Icon";

const TABS = ["全部", "已发布", "草稿"] as const;

export default function MyWorks() {
  const [tab, setTab] = useState<(typeof TABS)[number]>("全部");
  const navigate = useNavigate();
  return (
    <div className="min-h-[100dvh] pb-6 md:min-h-0 md:pb-0 md:max-w-[820px] md:mx-auto">
      <TopBar
        title="我的作品"
        right={
          <button
            onClick={() => navigate("/create/publish")}
            className="flex items-center gap-1 text-sm text-ancient-cinnabar font-semibold px-2"
          >
            <Plus size={16} /> 新建
          </button>
        }
      />
      <div className="hidden md:flex items-center justify-between pb-3">
        <div className="flex items-center">
          <span className="inline-block w-1 h-6 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
          <h1 className="text-xl font-bold text-text-primary">我的作品</h1>
        </div>
        <button
          onClick={() => navigate("/create/publish")}
          className="btn-stamp rounded-full px-3 py-1.5 text-sm gap-1"
        >
          <Plus size={14} /> 新建作品
        </button>
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

        <div className="mt-3 grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
          {POSTS.map((p) => (
            <button
              key={p.id}
              onClick={() => navigate(`/create/work/${p.id}`)}
              className="rounded-xl overflow-hidden bg-bg-card border border-border/40 text-left card-hover"
            >
              <Cover
                seed={String(p.id)}
                src={p.coverSrc}
                alt={p.title}
                text={p.title.slice(0, 4)}
                aspect="aspect-square"
                rounded="rounded-none"
                ornate={false}
              />
              <div className="p-2 text-xs">
                <div className="line-clamp-1 text-sm font-semibold text-text-primary">{p.title}</div>
                <div className="text-text-hint mt-1"><span className="text-ancient-cinnabar">♥</span> {p.likeCount} · 评论 {p.commentCount}</div>
              </div>
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}
