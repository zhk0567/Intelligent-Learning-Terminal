import { useState } from "react";
import { Outlet, useNavigate } from "react-router-dom";
import BottomNav from "./BottomNav";
import MiniPlayer from "./MiniPlayer";
import Sheet from "../components/Sheet";
import { TRACKS, usePlayerStore } from "../store/playerStore";
import { Play, Pause } from "../components/Icon";
import { ToastHost } from "../components/Toast";
import { useIsDesktop } from "../hooks/useIsDesktop";

export default function AppShell() {
  const isDesktop = useIsDesktop();
  const [listOpen, setListOpen] = useState(false);
  const { currentIndex, isPlaying, setIndex, toggle } = usePlayerStore();
  const navigate = useNavigate();

  if (isDesktop) {
    // 桌面壳由外层 DesktopShell 接管 Sidebar / Header / PlayerBar
    return <Outlet />;
  }

  return (
    <div className="relative pb-[136px]">
      <main>
        <Outlet />
      </main>
      <MiniPlayer onListClick={() => setListOpen(true)} />
      <BottomNav />
      <ToastHost />
      <Sheet open={listOpen} onClose={() => setListOpen(false)} title="播放列表">
        <div className="flex flex-col gap-1">
          {TRACKS.map((t, idx) => {
            const active = idx === currentIndex;
            return (
              <button
                key={t.id}
                onClick={() => {
                  setIndex(idx);
                  setListOpen(false);
                  navigate("/player");
                }}
                className={`flex items-center gap-3 rounded-xl px-3 py-2 text-left ${
                  active
                    ? "bg-neon-teal/10 text-neon-teal"
                    : "text-text-primary hover:bg-bg-cardElevated"
                }`}
              >
                <div className="flex-1 min-w-0">
                  <div className="truncate text-sm font-semibold">{t.title}</div>
                  <div className="truncate text-xs opacity-70">{t.artist} · {t.album}</div>
                </div>
                <button
                  className="h-9 w-9 grid place-items-center rounded-full text-current"
                  onClick={(e) => {
                    e.stopPropagation();
                    if (active) toggle();
                    else setIndex(idx);
                  }}
                >
                  {active && isPlaying ? <Pause size={20} /> : <Play size={20} />}
                </button>
              </button>
            );
          })}
        </div>
      </Sheet>
    </div>
  );
}
