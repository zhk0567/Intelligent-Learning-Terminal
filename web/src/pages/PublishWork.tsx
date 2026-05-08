import { useState } from "react";
import { useNavigate } from "react-router-dom";
import TopBar from "../components/TopBar";
import { Camera, Image, Music, Plus, Tag } from "../components/Icon";
import { toast } from "../components/Toast";

const CATS = ["音乐", "舞蹈", "技艺", "故事", "改编", "教学"];

export default function PublishWork() {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [cat, setCat] = useState("音乐");
  const [tagText, setTagText] = useState("");
  const [tags, setTags] = useState<string[]>(["古风"]);
  const navigate = useNavigate();

  const submit = () => {
    if (!title.trim()) return toast("请填写标题");
    if (content.length < 5) return toast("正文至少 5 个字");
    toast("作品已提交审核");
    navigate(-1);
  };

  return (
    <div className="min-h-[100dvh] pb-10 md:min-h-0 md:pb-0 md:max-w-[720px] md:mx-auto">
      <TopBar
        title="发布作品"
        right={
          <button onClick={submit} className="text-sm text-ancient-cinnabar font-semibold px-2">发布</button>
        }
      />
      <div className="hidden md:flex items-center justify-between pb-3">
        <div className="flex items-center">
          <span className="inline-block w-1 h-6 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
          <h1 className="text-xl font-bold text-text-primary">发布作品</h1>
        </div>
        <button onClick={submit} className="btn-stamp rounded-full px-4 py-1.5 text-sm">
          发布
        </button>
      </div>
      <div className="px-4 pt-2 md:px-0 md:pt-0 space-y-3">
        <div className="rounded-2xl border border-border/50 bg-bg-card p-3">
          <div className="grid grid-cols-3 gap-2">
            <Picker icon={<Image size={20} />} label="图片" />
            <Picker icon={<Camera size={20} />} label="视频" />
            <Picker icon={<Music size={20} />} label="音频" />
          </div>
        </div>

        <input
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="给作品起个标题"
          className="w-full rounded-xl border border-border/50 bg-bg-card px-3 py-3 text-sm text-text-primary placeholder:text-text-hint outline-none"
        />
        <textarea
          rows={6}
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="说说你的创作灵感、过程或感受…"
          className="w-full rounded-xl border border-border/50 bg-bg-card px-3 py-3 text-sm text-text-primary placeholder:text-text-hint outline-none resize-none"
        />

        <div>
          <div className="mb-2 text-sm font-semibold text-text-primary">分类</div>
          <div className="flex flex-wrap gap-2">
            {CATS.map((c) => (
              <button
                key={c}
                onClick={() => setCat(c)}
                className={`px-3 py-1 rounded-full text-xs border transition-colors ${
                  cat === c
                    ? "bg-ancient-cinnabar text-white border-ancient-cinnabar shadow-stampInset"
                    : "bg-bg-card text-text-secondary border-border/50 hover:border-ancient-bronze/60"
                }`}
              >
                {c}
              </button>
            ))}
          </div>
        </div>

        <div>
          <div className="mb-2 text-sm font-semibold text-text-primary">标签</div>
          <div className="flex flex-wrap gap-2 items-center">
            {tags.map((t) => (
              <button
                key={t}
                onClick={() => setTags(tags.filter((x) => x !== t))}
                className="rounded-full border border-ancient-bronze/40 text-ancient-bronze hover:bg-ancient-bronze/10 text-xs px-2 py-1 transition-colors"
              >
                #{t} ✕
              </button>
            ))}
            <div className="flex items-center gap-1">
              <Tag size={14} className="text-text-hint" />
              <input
                value={tagText}
                onChange={(e) => setTagText(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === "Enter" && tagText.trim()) {
                    setTags([...tags, tagText.trim()]);
                    setTagText("");
                  }
                }}
                placeholder="添加并回车"
                className="text-xs outline-none bg-transparent text-text-primary placeholder:text-text-hint"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

function Picker({ icon, label }: { icon: React.ReactNode; label: string }) {
  return (
    <button className="aspect-square rounded-xl border-2 border-dashed border-border/60 grid place-items-center text-text-hint">
      <div className="flex flex-col items-center gap-1">
        {icon}
        <span className="text-xs">{label}</span>
        <Plus size={12} />
      </div>
    </button>
  );
}
