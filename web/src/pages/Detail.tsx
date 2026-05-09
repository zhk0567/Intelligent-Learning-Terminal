import { useNavigate, useParams } from "react-router-dom";
import TopBar from "../components/TopBar";
import { ALBUMS } from "../data/music";
import { TRACKS, trackIndexById, usePlayerStore } from "../store/playerStore";
import Cover from "../components/Cover";
import { Play } from "../components/Icon";

const CATEGORY_LABEL: Record<string, string> = {
  hot: "每日热门",
  select: "每日精选",
  guess: "猜你喜欢",
};

export default function Detail() {
  const { id = "hot" } = useParams();
  const navigate = useNavigate();
  const setIndex = usePlayerStore((s) => s.setIndex);
  const list = ALBUMS.filter((a) => a.category === id) || ALBUMS;
  const title = CATEGORY_LABEL[id] ?? "专辑详情";

  const cover = list[0] ?? ALBUMS[0];

  return (
    <div className="min-h-[100dvh] md:min-h-0 md:max-w-[820px] md:mx-auto">
      <TopBar title={title} />
      <div className="hidden md:flex items-center pb-3 px-1">
        <span className="inline-block w-1 h-6 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
        <h1 className="text-xl font-bold text-text-primary">{title}</h1>
      </div>
      <div className="px-4 pt-2 md:px-0 md:pt-0">
        <div className="rounded-2xl overflow-hidden border border-ancient-bronze/40 bg-bg-card">
          <Cover
            seed={cover.id}
            src={cover.coverSrc}
            alt={title}
            text={title}
            aspect="aspect-[16/9]"
            rounded="rounded-none"
            ornate={false}
          />
          <div className="px-3 pt-2 pb-1">
            <div className="text-lg font-bold text-text-primary">{title}</div>
            <div className="text-xs text-text-secondary">古韵薪传 · 编辑精选</div>
          </div>
          <div className="p-3 flex items-center justify-between border-t border-border/40">
            <div className="text-xs text-text-secondary">{list.length} 张专辑 · {list.reduce((n, a) => n + a.trackIds.length, 0)} 首</div>
            <button
              onClick={() => {
                const tid = list[0]?.trackIds[0];
                setIndex(tid ? trackIndexById(tid) : 0);
                navigate("/player");
              }}
              className="btn-stamp rounded-full px-3 py-1.5 text-xs gap-1"
            >
              <Play size={14} /> 播放全部
            </button>
          </div>
        </div>

        <div className="mt-3 space-y-2 pb-6">
          {list.flatMap((a) =>
            a.trackIds.map((tid, i) => {
              const tk = TRACKS.find((x) => x.id === tid)!;
              if (!tk) return null;
              return (
                <button
                  key={`${a.id}-${tid}-${i}`}
                  onClick={() => {
                    const idx = TRACKS.findIndex((x) => x.id === tid);
                    setIndex(idx);
                    navigate("/player");
                  }}
                  className="flex w-full min-w-0 items-start gap-3 rounded-xl bg-bg-card border border-border/40 p-2 text-left card-hover"
                >
                  <Cover
                    seed={tk.id}
                    src={tk.coverSrc}
                    alt={tk.title}
                    text={tk.title.slice(0, 2)}
                    aspect="aspect-square"
                    rounded="rounded-lg"
                    className="h-12 w-12 shrink-0"
                    ornate={false}
                  />
                  <div className="min-w-0 flex-1 self-stretch text-left">
                    <div className="truncate text-sm font-semibold text-text-primary">{tk.title}</div>
                    <div className="truncate text-xs text-text-secondary">{tk.artist} · {a.title}</div>
                  </div>
                  <Play size={18} className="shrink-0 self-center text-ancient-cinnabar" />
                </button>
              );
            }),
          )}
        </div>
      </div>
    </div>
  );
}
