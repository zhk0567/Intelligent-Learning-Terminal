import { Navigate, useParams } from "react-router-dom";
import TopBar from "../components/TopBar";
import { lessonById } from "../data/lessons";

function fmtDur(sec: number) {
  const m = Math.floor(sec / 60);
  const s = sec % 60;
  return `${m}:${String(s).padStart(2, "0")}`;
}

export default function Lesson() {
  const { id = "lesson_1" } = useParams();
  const lesson = lessonById(id);
  if (!lesson) return <Navigate to="/music" replace />;

  return (
    <div className="min-h-[100dvh] md:min-h-0 md:max-w-[820px] md:mx-auto">
      <TopBar title="基础学习" />
      <div className="px-4 pt-2 md:px-0 md:pt-0 pb-8">
        <div className="rounded-2xl overflow-hidden border border-border/40 bg-black">
          <video
            className="w-full aspect-video bg-black"
            controls
            playsInline
            preload="metadata"
            poster={lesson.coverSrc}
            src={lesson.videoSrc}
          />
        </div>

        <div className="mt-4 rounded-2xl border border-border/40 bg-bg-card p-4">
          <div className="text-lg font-bold text-text-primary">{lesson.title}</div>
          <div className="mt-1 text-xs text-text-secondary">
            {lesson.artist} · 时长 {fmtDur(lesson.durationSec)}
          </div>
          <span className="mt-3 inline-block rounded-full bg-ancient-bronze/15 text-ancient-bronze text-xs px-3 py-1">
            {lesson.desc}
          </span>
        </div>

        <div className="mt-4 rounded-2xl border border-border/40 bg-bg-card p-4">
          <div className="text-base font-semibold text-text-primary mb-2">课程介绍</div>
          <p className="text-sm leading-7 text-text-secondary whitespace-pre-line">
            {lesson.longDesc}
          </p>
        </div>
      </div>
    </div>
  );
}
