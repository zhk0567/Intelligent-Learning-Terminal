import { useNavigate } from "react-router-dom";
import { Compass, Edit, Plus, Trophy } from "../components/Icon";
import Cover from "../components/Cover";
import Section from "../components/Section";
import { CHALLENGES, POSTS } from "../data/post";

export default function Create() {
  const navigate = useNavigate();
  return (
    <div className="px-3 pt-2 md:px-0 md:pt-0">
      <div
        className="flex items-center justify-between py-1 md:hidden"
        style={{ paddingTop: "max(0.25rem, env(safe-area-inset-top))" }}
      >
        <div className="text-xl font-bold text-text-primary px-1">创作广场</div>
        <button
          onClick={() => navigate("/create/publish")}
          className="flex items-center gap-1 rounded-full bg-neon-teal text-white px-3 py-1.5 text-sm"
        >
          <Plus size={16} /> 发布
        </button>
      </div>

      <div className="mt-3 md:mt-0 grid grid-cols-3 md:grid-cols-6 gap-2 md:gap-3">
        <Quick label="发布作品" icon={<Edit size={20} />} onClick={() => navigate("/create/publish")} />
        <Quick label="挑战赛" icon={<Trophy size={20} />} onClick={() => navigate("/create/challenge")} />
        <Quick label="找创作者" icon={<Compass size={20} />} onClick={() => navigate("/create/creator/u1")} />
      </div>

      <Section title="进行中的挑战" eyebrow="Active Challenges" framed={false}>
        <div className="flex gap-3 overflow-x-auto no-scrollbar pb-1 md:grid md:grid-cols-3 md:overflow-visible">
          {CHALLENGES.map((c) => (
            <div
              key={c.id}
              className="shrink-0 w-64 md:w-auto rounded-2xl overflow-hidden border border-border/50"
              onClick={() => navigate("/create/challenge")}
            >
              <Cover
                seed={c.id}
                src={c.coverSrc}
                alt={c.title}
                text={c.title}
                aspect="aspect-[16/9]"
                rounded="rounded-none"
                ornate={false}
              />
              <div className="p-3 bg-bg-card">
                <div className="text-sm font-semibold text-text-primary">{c.title}</div>
                <div className="text-xs text-text-secondary line-clamp-2 mt-1">{c.desc}</div>
                <div className="mt-2 h-1.5 rounded-full bg-border/40">
                  <div
                    className="h-full rounded-full bg-neon-teal"
                    style={{ width: `${(c.progress / c.total) * 100}%` }}
                  />
                </div>
                <div className="mt-1 flex items-center justify-between text-[11px] text-text-hint">
                  <span>奖励：{c.rewardLabel}</span>
                  <span>{c.progress}/{c.total}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </Section>

      <Section title="社区作品" eyebrow="Community Works" framed={false}>
        <div className="space-y-3 md:grid md:grid-cols-2 lg:grid-cols-3 md:gap-4 md:space-y-0">
          {POSTS.map((p) => (
            <button
              key={p.id}
              onClick={() => navigate(`/create/work/${p.id}`)}
              className="block w-full text-left rounded-2xl overflow-hidden border border-border/50 bg-bg-card card-hover"
            >
              <Cover
                seed={String(p.id)}
                src={p.coverSrc}
                alt={p.title}
                text={p.title}
                aspect="aspect-[16/9]"
                rounded="rounded-none"
                ornate={false}
              />
              <div className="p-3">
                <div className="text-sm font-semibold text-text-primary">{p.title}</div>
                <div className="mt-1 line-clamp-2 text-xs text-text-secondary">{p.content}</div>
                <div className="mt-2 flex items-center justify-between text-[11px] text-text-hint">
                  <span>{p.author} · {p.timeText}</span>
                  <span>♥ {p.likeCount} · 评论 {p.commentCount}</span>
                </div>
              </div>
            </button>
          ))}
        </div>
      </Section>
    </div>
  );
}

function Quick({ label, icon, onClick }: { label: string; icon: React.ReactNode; onClick: () => void }) {
  return (
    <button
      onClick={onClick}
      className="flex flex-col items-center gap-1 rounded-2xl bg-bg-card border border-border/50 py-3 text-text-primary card-hover"
    >
      <div className="text-ancient-cinnabar">{icon}</div>
      <span className="text-xs">{label}</span>
    </button>
  );
}
