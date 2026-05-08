import { useNavigate, useParams } from "react-router-dom";
import TopBar from "../components/TopBar";
import { APP_IMAGES } from "../assets/appImages";
import Cover from "../components/Cover";
import { POSTS } from "../data/post";

export default function CreatorProfile() {
  const { id } = useParams();
  const navigate = useNavigate();
  const seed = id ?? "u1";
  const works = POSTS.slice(0, 4);

  return (
    <div className="min-h-[100dvh] pb-10 md:min-h-0 md:pb-0 md:max-w-[820px] md:mx-auto">
      <TopBar title="创作者主页" />

      <div className="relative">
        <Cover
          seed={`${seed}-bg`}
          src={APP_IMAGES.banner3}
          alt="创作者封面"
          aspect="aspect-[16/8] md:aspect-[16/5]"
          rounded="rounded-none md:rounded-2xl"
          ornate={false}
        />
        <div className="absolute -bottom-10 left-4 flex items-end gap-3">
          <div className="h-20 w-20 rounded-full bg-bg-card grid place-items-center text-xl font-bold text-ancient-bronze ring-4 ring-bg-primary border-2 border-ancient-bronze/55">
            {seed[0].toUpperCase()}
          </div>
          <div className="pb-1">
            <div className="text-base font-bold text-text-primary">非遗创作人 #{seed}</div>
            <div className="text-xs text-text-secondary">古琴 / 古筝 / 改编</div>
          </div>
        </div>
      </div>

      <div className="mt-12 px-4 md:px-0">
        <div className="flex items-center justify-around rounded-card border border-border/60 bg-bg-card py-3">
          <Stat label="作品" value="42" />
          <Stat label="粉丝" value="1.2 万" />
          <Stat label="点赞" value="8.5 万" />
        </div>

        <div className="mt-3 flex gap-2">
          <button className="btn-stamp flex-1 rounded-full py-2 text-sm">关注</button>
          <button className="btn-ghost flex-1 rounded-full py-2 text-sm">私信</button>
        </div>

        <div className="mt-4 flex items-center">
          <span className="inline-block w-1 h-4 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
          <div className="text-sm font-bold text-text-primary">作品</div>
        </div>
        <div className="mt-2 grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
          {works.map((p) => (
            <button
              key={p.id}
              onClick={() => navigate(`/create/work/${p.id}`)}
              className="rounded-xl overflow-hidden border border-border/40 bg-bg-card text-left card-hover"
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
                <div className="line-clamp-1 font-semibold text-text-primary">{p.title}</div>
                <div className="text-text-hint mt-0.5"><span className="text-ancient-cinnabar">♥</span> {p.likeCount}</div>
              </div>
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}

function Stat({ label, value }: { label: string; value: string }) {
  return (
    <div className="text-center">
      <div className="text-base font-bold text-text-primary">{value}</div>
      <div className="text-xs text-text-secondary">{label}</div>
    </div>
  );
}
