import { useState } from "react";
import { useParams } from "react-router-dom";
import TopBar from "../components/TopBar";
import Cover from "../components/Cover";
import { STORIES } from "../data/story";
import { Heart, HeartFilled, Share, Star } from "../components/Icon";
import { toast } from "../components/Toast";

export default function StoryDetail() {
  const { id } = useParams();
  const story = STORIES.find((s) => s.id === id) ?? STORIES[0];
  const [liked, setLiked] = useState(!!story.isLiked);
  const [saved, setSaved] = useState(!!story.isSaved);

  return (
    <div className="min-h-[100dvh] pb-24 md:min-h-0 md:pb-0 md:max-w-[760px] md:mx-auto">
      <TopBar title="故事详情" right={<button className="text-text-primary"><Share size={20} /></button>} />
      <Cover
        seed={story.id}
        src={story.coverSrc}
        alt={story.title}
        text={story.title}
        aspect="aspect-[16/9] md:aspect-[16/6]"
        coverWidth={story.coverWidth}
        coverHeight={story.coverHeight}
        rounded="rounded-none md:rounded-2xl"
        ornate={false}
      />

      <div className="px-4 pt-3 md:px-0 md:pt-4">
        <h1 className="text-xl font-bold text-text-primary">{story.title}</h1>
        <div className="mt-1 text-xs text-text-hint">{story.author}</div>
        <div className="mt-2 flex flex-wrap gap-1">
          {story.tags.map((t) => (
            <span key={t} className="rounded-full border border-ancient-bronze/40 text-ancient-bronze text-[10px] px-2 py-0.5">{t}</span>
          ))}
        </div>

        <article className="mt-4 text-sm leading-7 text-text-primary whitespace-pre-line">
          {story.body}
        </article>
      </div>

      <div
        className="fixed bottom-0 left-1/2 -translate-x-1/2 w-full max-w-phone bg-bg-card border-t border-border/50 px-3 py-2 flex items-center justify-center gap-10 md:static md:translate-x-0 md:left-auto md:max-w-none md:mt-4 md:rounded-card md:border md:border-border/50 md:px-4 md:py-3"
        style={{ paddingBottom: "max(0.5rem, env(safe-area-inset-bottom))" }}
      >
        <button
          onClick={() => {
            setLiked((v) => !v);
            toast(liked ? "已取消" : "感谢点赞");
          }}
          className="h-10 w-10 grid place-items-center text-text-primary"
        >
          {liked ? <HeartFilled className="text-ancient-cinnabar" /> : <Heart />}
        </button>
        <button
          onClick={() => {
            setSaved((v) => !v);
            toast(saved ? "已移除收藏" : "已加入收藏");
          }}
          className="h-10 w-10 grid place-items-center text-text-primary"
        >
          <Star className={saved ? "text-ancient-bronze" : "text-text-hint"} />
        </button>
      </div>
    </div>
  );
}
