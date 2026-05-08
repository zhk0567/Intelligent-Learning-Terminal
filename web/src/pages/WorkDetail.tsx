import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import TopBar from "../components/TopBar";
import Cover from "../components/Cover";
import { POSTS } from "../data/post";
import { Heart, HeartFilled, Send, Share, Star } from "../components/Icon";
import { toast } from "../components/Toast";

export default function WorkDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const post = POSTS.find((p) => String(p.id) === id) ?? POSTS[0];
  const [liked, setLiked] = useState(false);
  const [saved, setSaved] = useState(false);
  const [comment, setComment] = useState("");
  const [comments, setComments] = useState<{ name: string; text: string }[]>([
    { name: "山雀", text: "好喜欢这个改编版本，能教教我吗？" },
    { name: "南风", text: "顶一下！" },
  ]);

  return (
    <div className="min-h-[100dvh] pb-24 md:min-h-0 md:pb-0 md:max-w-[760px] md:mx-auto">
      <TopBar title="作品详情" right={<button className="text-text-primary"><Share size={20} /></button>} />
      <Cover
        seed={String(post.id)}
        src={post.coverSrc}
        alt={post.title}
        text={post.title}
        aspect="aspect-square md:aspect-[16/7]"
        rounded="rounded-none md:rounded-2xl"
        className="md:max-h-[300px]"
        ornate={false}
      />

      <div className="px-4 pt-3 md:px-0 md:pt-4">
        <button
          onClick={() => navigate(`/create/creator/u${post.id}`)}
          className="flex items-center gap-2"
        >
          <div className="h-9 w-9 rounded-full bg-bg-cardElevated grid place-items-center text-xs font-bold text-ancient-bronze ring-1 ring-ancient-bronze/55">
            {post.author[0]}
          </div>
          <div>
            <div className="text-sm font-semibold text-text-primary">{post.author}</div>
            <div className="text-[11px] text-text-hint">{post.timeText} · {post.category}</div>
          </div>
        </button>

        <h1 className="mt-3 text-lg font-bold text-text-primary">{post.title}</h1>
        <p className="mt-2 text-sm text-text-secondary leading-7">{post.content}</p>

        <div className="mt-4 flex items-center gap-3 text-xs text-text-hint">
          <span><span className="text-ancient-cinnabar">♥</span> {post.likeCount + (liked ? 1 : 0)}</span>
          <span>评论 {post.commentCount + comments.length - 2}</span>
        </div>

        <div className="mt-5 rounded-2xl border border-border/50 bg-bg-card p-3">
          <div className="flex items-center mb-2">
            <span className="inline-block w-1 h-4 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
            <div className="text-sm font-bold text-text-primary">评论</div>
          </div>
          <div className="space-y-2">
            {comments.map((c, i) => (
              <div key={i} className="flex gap-2">
                <div className="h-8 w-8 grid place-items-center rounded-full bg-bg-cardElevated text-ancient-bronze text-xs font-bold ring-1 ring-ancient-bronze/50">{c.name[0]}</div>
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
        <button onClick={() => { setLiked((v) => !v); toast(liked ? "已取消" : "已点赞"); }} className="h-10 w-10 grid place-items-center text-text-primary">
          {liked ? <HeartFilled className="text-ancient-cinnabar" /> : <Heart />}
        </button>
        <button onClick={() => { setSaved((v) => !v); toast(saved ? "已移除收藏" : "已收藏"); }} className="h-10 w-10 grid place-items-center text-text-primary">
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
