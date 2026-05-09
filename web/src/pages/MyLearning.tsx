import { APP_IMAGES } from "../assets/appImages";
import TopBar from "../components/TopBar";
import Cover from "../components/Cover";
import { BASIC_LESSONS } from "../data/lessons";

interface Course {
  id: string;
  title: string;
  teacher: string;
  progress: number;
  total: number;
  coverSrc: string;
}

const COURSES: Course[] = [
  {
    id: "c1",
    title: BASIC_LESSONS[0].title,
    teacher: BASIC_LESSONS[0].artist,
    progress: 4,
    total: 7,
    coverSrc: BASIC_LESSONS[0].coverSrc,
  },
  {
    id: "c2",
    title: BASIC_LESSONS[1].title,
    teacher: BASIC_LESSONS[1].artist,
    progress: 2,
    total: 6,
    coverSrc: BASIC_LESSONS[1].coverSrc,
  },
  {
    id: "c3",
    title: "敦煌乐器图谱解读",
    teacher: "林之雪",
    progress: 1,
    total: 8,
    coverSrc: APP_IMAGES.banner2,
  },
];

export default function MyLearning() {
  return (
    <div className="min-h-[100dvh] pb-6 md:min-h-0 md:pb-0 md:max-w-[820px] md:mx-auto">
      <TopBar title="我的学习" subtitle={`进行中 ${COURSES.length} 门`} />
      <div className="hidden md:flex items-center pb-3 px-1">
        <span className="inline-block w-1 h-6 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
        <h1 className="text-xl font-bold text-text-primary">
          我的学习
          <span className="text-sm text-text-hint font-normal"> · 进行中 {COURSES.length} 门</span>
        </h1>
      </div>
      <div className="px-3 pt-2 md:px-0 md:pt-0 space-y-3">
        <div className="space-y-3 md:grid md:grid-cols-2 md:gap-4 md:space-y-0">
          {COURSES.map((c) => (
            <div key={c.id} className="rounded-2xl bg-bg-card border border-border/50 overflow-hidden card-hover">
              <Cover
                seed={c.id}
                src={c.coverSrc}
                alt={c.title}
                text={c.title}
                aspect="aspect-[16/9]"
                rounded="rounded-none"
                ornate={false}
              />
              <div className="p-3">
                <div className="text-sm font-semibold text-text-primary">{c.title}</div>
                <div className="text-xs text-text-secondary">主讲：{c.teacher}</div>
                <div className="mt-2 h-1.5 rounded-full bg-border/40 overflow-hidden">
                  <div
                    className="h-full rounded-full bg-ancient-cinnabar"
                    style={{ width: `${(c.progress / c.total) * 100}%` }}
                  />
                </div>
                <div className="mt-1 flex items-center justify-between text-[11px] text-text-hint">
                  <span>已学 {c.progress} / {c.total} 课</span>
                  <button className="btn-stamp rounded-full px-3 py-1 text-xs">继续学习</button>
                </div>
              </div>
            </div>
          ))}
        </div>

        <div className="mt-2 px-1 flex items-center">
          <span className="inline-block w-1 h-4 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
          <div>
            <div className="text-sm font-bold text-text-primary leading-tight">推荐课程</div>
            <div className="text-[10px] uppercase tracking-[0.18em] text-ancient-bronze/85">Recommended</div>
          </div>
        </div>
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
          {BASIC_LESSONS.map((l) => (
            <div key={l.id} className="rounded-xl bg-bg-card border border-border/40 overflow-hidden card-hover">
              <Cover
                seed={l.id}
                src={l.coverSrc}
                alt={l.title}
                text={l.title}
                aspect="aspect-square"
                rounded="rounded-none"
                ornate={false}
              />
              <div className="p-2">
                <div className="text-sm text-text-primary line-clamp-1">{l.title}</div>
                <div className="text-[11px] text-text-secondary line-clamp-1">{l.desc}</div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
