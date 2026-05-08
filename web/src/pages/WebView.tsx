import { useSearchParams } from "react-router-dom";
import TopBar from "../components/TopBar";

export default function WebView() {
  const [params] = useSearchParams();
  const title = params.get("title") ?? "网页";
  const url = params.get("url") ?? "";

  const article = title.includes("协议")
    ? AGREEMENT
    : title.includes("隐私")
    ? PRIVACY
    : ABOUT;

  return (
    <div className="min-h-[100dvh] md:max-w-[760px] md:mx-auto md:pt-[6vh]">
      <TopBar title={title} />
      <div className="hidden md:flex items-center px-5 pb-3">
        <span className="inline-block w-1 h-6 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
        <h1 className="text-xl font-bold text-text-primary">{title}</h1>
      </div>
      <div className="px-5 pb-10 pt-3 md:pt-0">
        {url && (
          <div className="rounded-xl bg-bg-card border border-border/50 px-3 py-2 text-xs text-text-hint break-all">
            外链：{url}
          </div>
        )}
        <article className="mt-3 space-y-3 text-sm leading-7 text-text-primary">
          {article.split("\n\n").map((p, i) => (
            <p key={i}>{p}</p>
          ))}
        </article>
      </div>
    </div>
  );
}

const AGREEMENT = `欢迎使用「古韵薪传」客户端。\n\n本协议示例文本仅用于演示。请遵守相关法律法规，文明使用，不得发布违法、侵权及不良内容。\n\n您一旦完成注册即视为同意本协议。如有疑问，可在「设置 - 关于」页面查看联系方式。`;
const PRIVACY = `古韵薪传重视您的个人信息保护。本演示版本不会上传任何手机号、密码或地址数据，所有交互均存储在本地浏览器。\n\n生产环境会按《个人信息保护法》规定告知收集范围、用途及保留期限。`;
const ABOUT = `古韵薪传 · 科技赋能版本 v0.1\n\n本页面供示例：在 Android 端用于展示 H5 内容。Web 端使用相同布局以保持视觉一致性。`;
