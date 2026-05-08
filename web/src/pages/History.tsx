import { useState } from "react";
import TopBar from "../components/TopBar";
import { HISTORY } from "../data/history";
import Cover from "../components/Cover";
import { Trash } from "../components/Icon";
import { toast } from "../components/Toast";
import EmptyState from "../components/EmptyState";

export default function History() {
  const [list, setList] = useState(HISTORY);

  // 按日期分组
  const groups: Record<string, typeof HISTORY> = {};
  list.forEach((h) => {
    const day = h.browseTime.startsWith("今")
      ? "今日"
      : h.browseTime.startsWith("昨")
      ? "昨日"
      : "更早";
    (groups[day] ??= []).push(h);
  });

  return (
    <div className="min-h-[100dvh] md:min-h-0 md:max-w-[820px] md:mx-auto">
      <TopBar
        title="浏览历史"
        right={
          <button
            onClick={() => {
              setList([]);
              toast("已清空");
            }}
            className="text-text-hint flex items-center gap-1 text-xs"
          >
            <Trash size={14} /> 清空
          </button>
        }
      />
      <div className="hidden md:flex items-center justify-between pb-3">
        <div className="flex items-center">
          <span className="inline-block w-1 h-6 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
          <h1 className="text-xl font-bold text-text-primary">浏览历史</h1>
        </div>
        <button
          onClick={() => {
            setList([]);
            toast("已清空");
          }}
          className="btn-ghost rounded-full px-3 py-1.5 text-xs gap-1"
        >
          <Trash size={12} /> 清空记录
        </button>
      </div>
      <div className="px-3 pt-2 pb-6 md:px-0 md:pt-0 md:pb-0 space-y-4">
        {list.length === 0 && (
          <EmptyState text="还没有浏览记录" hint="去逛逛感兴趣的内容吧" icon="scroll" />
        )}
        {Object.entries(groups).map(([day, items]) => (
          <div key={day}>
            <div className="px-1 pb-2 flex items-center">
              <span className="inline-block w-1 h-3 rounded-sm bg-ancient-bronze mr-2" aria-hidden />
              <div className="text-xs text-text-secondary font-semibold">{day}</div>
            </div>
            <div className="space-y-2 md:grid md:grid-cols-2 md:gap-3 md:space-y-0">
              {items.map((h) => (
                <div key={h.id} className="flex min-w-0 items-start gap-3 rounded-xl bg-bg-card border border-border/40 p-2 card-hover">
                  <Cover
                    seed={h.id}
                    src={h.coverSrc}
                    alt={h.title}
                    text={h.title.slice(0, 2)}
                    aspect="aspect-square"
                    rounded="rounded-lg"
                    className="h-14 w-14 shrink-0"
                    ornate={false}
                  />
                  <div className="min-w-0 flex-1 self-stretch text-left">
                    <div className="truncate text-sm font-semibold text-text-primary">{h.title}</div>
                    <div className="text-xs text-text-secondary">{h.desc}</div>
                    <div className="text-[11px] text-text-hint">{h.type} · {h.browseTime}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
