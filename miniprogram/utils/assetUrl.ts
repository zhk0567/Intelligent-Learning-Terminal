import { STATIC_ORIGIN } from "../config/staticOrigin";

/** 与 Web `assetUrl` 行为一致：相对根路径拼到 `STATIC_ORIGIN`。 */
export function assetUrl(path: string | undefined | null): string {
  if (path == null || path === "") return "";
  const p0 = String(path).trim();
  if (/^https?:\/\//i.test(p0)) return p0;
  const origin = STATIC_ORIGIN.replace(/\/$/, "");
  const p = p0.startsWith("/") ? p0 : `/${p0}`;
  return origin ? `${origin}${p}` : p;
}
