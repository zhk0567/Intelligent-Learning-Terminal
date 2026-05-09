/**
 * 将 `/images`、`/audio`、`/video` 等路径拼到远程静态域。
 * 构建/运行前设置环境变量 `VITE_STATIC_ORIGIN`（勿尾斜杠），例：`https://assets.example.com`
 * 未设置时保持相对路径，便于本地 Vite 直接读 `public/`。
 */
export function assetUrl(path: string | undefined | null): string {
  if (path == null || path === "") return "";
  const p0 = String(path).trim();
  if (/^https?:\/\//i.test(p0)) return p0;
  const origin = String(import.meta.env.VITE_STATIC_ORIGIN ?? "")
    .trim()
    .replace(/\/$/, "");
  const p = p0.startsWith("/") ? p0 : `/${p0}`;
  return origin ? `${origin}${p}` : p;
}
