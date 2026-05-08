import { useState } from "react";
import { useNavigate } from "react-router-dom";
import TopBar from "../components/TopBar";
import { Search as SearchIcon, Trash } from "../components/Icon";
import { HOT_KEYWORDS } from "../data/music";
import EmptyState from "../components/EmptyState";

const HISTORY_KEY = "search_history";

function loadHist(): string[] {
  try {
    return JSON.parse(localStorage.getItem(HISTORY_KEY) ?? "[]");
  } catch {
    return [];
  }
}

export default function Search() {
  const [q, setQ] = useState("");
  const [hist, setHist] = useState<string[]>(loadHist);
  const navigate = useNavigate();

  const submit = (kw?: string) => {
    const word = (kw ?? q).trim();
    if (!word) return;
    const next = [word, ...hist.filter((x) => x !== word)].slice(0, 12);
    setHist(next);
    localStorage.setItem(HISTORY_KEY, JSON.stringify(next));
    navigate(`/search/result?q=${encodeURIComponent(word)}`);
  };

  return (
    <div className="min-h-[100dvh] md:min-h-0 md:max-w-[720px] md:mx-auto">
      <div
        className="sticky top-0 z-30 flex items-center gap-2 px-3 py-2 bg-bg-primary/85 backdrop-blur-md border-b border-border/40 md:hidden"
        style={{ paddingTop: "max(0.5rem, env(safe-area-inset-top))" }}
      >
        <button onClick={() => navigate(-1)} className="text-text-primary text-base">‹</button>
        <div className="flex-1 flex items-center gap-2 rounded-full bg-bg-card border border-border/60 px-3 py-2">
          <SearchIcon size={18} className="text-text-hint" />
          <input
            autoFocus
            className="flex-1 outline-none bg-transparent text-sm text-text-primary placeholder:text-text-hint"
            placeholder="搜索曲目、艺人或关键词"
            value={q}
            onChange={(e) => setQ(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && submit()}
          />
        </div>
        <button onClick={() => submit()} className="text-sm text-ancient-cinnabar font-semibold px-1">搜索</button>
      </div>

      {/* 桌面版搜索框（移动版隐藏） */}
      <div className="hidden md:flex items-center gap-2 pb-3">
        <div className="flex-1 flex items-center gap-2 rounded-full bg-bg-card border border-border/60 px-4 py-2.5 focus-within:border-ancient-bronze/70 transition-colors">
          <SearchIcon size={18} className="text-text-hint" />
          <input
            autoFocus
            className="flex-1 outline-none bg-transparent text-sm text-text-primary placeholder:text-text-hint"
            placeholder="搜索曲目、艺人或关键词"
            value={q}
            onChange={(e) => setQ(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && submit()}
          />
        </div>
        <button onClick={() => submit()} className="btn-stamp rounded-full px-5 py-2 text-sm">搜索</button>
      </div>

      <div className="px-4 pt-4 md:px-0 md:pt-0">
        <div className="flex items-center mb-2">
          <span className="inline-block w-1 h-4 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
          <div className="text-sm font-bold text-text-primary">热门搜索</div>
        </div>
        <div className="flex flex-wrap gap-2">
          {HOT_KEYWORDS.map((k) => (
            <button
              key={k}
              onClick={() => submit(k)}
              className="rounded-full border border-border/60 bg-bg-card px-3 py-1 text-xs text-text-secondary hover:border-ancient-bronze/60 hover:text-ancient-bronze transition-colors"
            >
              {k}
            </button>
          ))}
        </div>

        <div className="mt-6 flex items-center justify-between">
          <div className="flex items-center">
            <span className="inline-block w-1 h-4 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
            <div className="text-sm font-bold text-text-primary">最近搜索</div>
          </div>
          {hist.length > 0 && (
            <button
              onClick={() => {
                setHist([]);
                localStorage.removeItem(HISTORY_KEY);
              }}
              className="text-text-hint flex items-center gap-1 text-xs hover:text-ancient-cinnabar transition-colors"
            >
              <Trash size={14} /> 清空
            </button>
          )}
        </div>
        {hist.length === 0 ? (
          <EmptyState text="暂无搜索记录" hint="试试热门关键词" icon="scroll" />
        ) : (
          <div className="mt-2 flex flex-wrap gap-2">
            {hist.map((h) => (
              <button
                key={h}
                onClick={() => submit(h)}
                className="rounded-full bg-bg-cardElevated/60 border border-border/40 px-3 py-1 text-xs text-text-primary hover:border-ancient-bronze/60 transition-colors"
              >
                {h}
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
