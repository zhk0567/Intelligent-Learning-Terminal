import { useState } from "react";
import { useParams } from "react-router-dom";
import TopBar from "../components/TopBar";
import Cover from "../components/Cover";
import { STORIES } from "../data/story";
import { Heart, HeartFilled, Share, Star, Send } from "../components/Icon";
import { toast } from "../components/Toast";

const PARAGRAPH = `非遗的魅力，并非只陈列在展柜里的标本，而是仍活跃在传承人、观众与一方水土之间的共同记忆。
我们用记录与传播的方式重新打开它——让它被听见、被看见、被讨论、被续写。
你也许不会成为传承人，但只要你愿意多了解一分，便已参与它的延续。`;

export default function StoryDetail() {
  const { id } = useParams();
  const story = STORIES.find((s) => s.id === id) ?? STORIES[0];
  const [liked, setLiked] = useState(!!story.isLiked);
  const [saved, setSaved] = useState(!!story.isSaved);
  const [comment, setComment] = useState("");
  const [comments, setComments] = useState<{ name: string; text: string }[]>([
    { name: "蓝栀", text: "看哭了，蜀绣大师那段写得太好。" },
    { name: "白露", text: "已经把故事分享给妈妈了。" },
  ]);

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
        <div className="mt-1 text-xs text-text-hint flex items-center gap-2">
          <span>{story.author}</span>
          <span>·</span>
          <span>{story.publishTime}</span>
          <span>·</span>
          <span>{story.readCount} 阅读</span>
        </div>
        <div className="mt-2 flex flex-wrap gap-1">
          {story.tags.map((t) => (
            <span key={t} className="rounded-full border border-ancient-bronze/40 text-ancient-bronze text-[10px] px-2 py-0.5">{t}</span>
          ))}
        </div>

        <article className="mt-4 text-sm leading-7 text-text-primary whitespace-pre-line">
          {story.body}
        </article>
        <p className="mt-4 text-xs text-text-secondary leading-6 whitespace-pre-line border-t border-border/40 pt-4">
          {PARAGRAPH}
        </p>

        <div className="mt-5 rounded-2xl border border-border/50 bg-bg-card p-3">
          <div className="flex items-center mb-2">
            <span className="inline-block w-1 h-4 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
            <div className="text-sm font-bold text-text-primary">评论 {comments.length}</div>
          </div>
          <div className="space-y-2">
            {comments.map((c, i) => (
              <div key={i} className="flex gap-2">
                <div className="h-8 w-8 grid place-items-center rounded-full bg-bg-cardElevated text-ancient-bronze text-xs font-bold ring-1 ring-ancient-bronze/50">
                  {c.name[0]}
                </div>
                <div className="flex-1">
                  <div className="text-sm text-text-primary">{c.name}</div>
                  <div className="text-xs text-text-secondary">{c.text}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div
        className="fixed bottom-0 left-1/2 -translate-x-1/2 w-full max-w-phone bg-bg-card border-t border-border/50 px-3 py-2 flex items-center gap-2 md:static md:translate-x-0 md:left-auto md:max-w-none md:mt-4 md:rounded-card md:border md:border-border/50 md:px-4 md:py-3"
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
        <input
          value={comment}
          onChange={(e) => setComment(e.target.value)}
          placeholder="写下你的想法…"
          className="flex-1 rounded-full bg-bg-cardElevated/60 border border-border/50 px-3 py-2 text-sm text-text-primary placeholder:text-text-hint outline-none"
        />
        <button
          onClick={() => {
            if (!comment.trim()) return;
            setComments((cs) => [{ name: "我", text: comment.trim() }, ...cs]);
            setComment("");
          }}
          className="btn-primary h-10 w-10 grid place-items-center rounded-full"
          aria-label="发送"
        >
          <Send size={18} />
        </button>
      </div>
    </div>
  );
}
