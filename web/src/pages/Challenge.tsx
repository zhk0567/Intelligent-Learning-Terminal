import TopBar from "../components/TopBar";
import { CHALLENGES } from "../data/post";
import Cover from "../components/Cover";
import { Trophy } from "../components/Icon";

export default function Challenge() {
  return (
    <div className="min-h-[100dvh] pb-10 md:min-h-0 md:pb-0 md:max-w-[820px] md:mx-auto">
      <TopBar title="挑战赛" subtitle="完成挑战 · 解锁限定奖励" />
      <div className="hidden md:flex items-center px-1 pb-1">
        <span className="inline-block w-1 h-6 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
        <div>
          <div className="text-[10px] uppercase tracking-[0.18em] text-ancient-bronze/85">Challenges</div>
          <h1 className="text-xl font-bold text-text-primary leading-tight">挑战赛</h1>
        </div>
      </div>
      <p className="hidden md:block text-xs text-text-secondary px-1 pb-3">完成挑战 · 解锁限定奖励</p>
      <div className="px-3 pt-2 md:px-0 md:pt-0 space-y-3 md:grid md:grid-cols-2 md:gap-4 md:space-y-0">
        {CHALLENGES.map((c) => (
          <div
            key={c.id}
            className="rounded-2xl border border-border/50 bg-bg-card overflow-hidden card-hover"
          >
            <Cover
              seed={c.id}
              src={c.coverSrc}
              alt={c.title}
              text={c.title}
              aspect="aspect-[16/7]"
              rounded="rounded-none"
              ornate={false}
            />
            <div className="p-3">
              <div className="flex items-center justify-between">
                <div className="text-base font-bold text-text-primary">{c.title}</div>
                <span className="rounded-full bg-ancient-bronze/15 text-ancient-bronze text-[11px] px-2 py-0.5 flex items-center gap-1">
                  <Trophy size={12} /> {c.rewardLabel}
                </span>
              </div>
              <div className="mt-1 text-xs text-text-secondary">{c.desc}</div>
              <div className="mt-2 h-1.5 rounded-full bg-border/40 overflow-hidden">
                <div
                  className="h-full rounded-full bg-ancient-cinnabar"
                  style={{ width: `${(c.progress / c.total) * 100}%` }}
                />
              </div>
              <div className="mt-2 flex items-center justify-between text-[11px] text-text-hint">
                <span>已完成 {c.progress} / {c.total}</span>
                <button className="btn-stamp rounded-full px-3 py-1">立即参与</button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
