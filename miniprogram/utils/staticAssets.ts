import { STATIC_ASSET_BASE } from "../config/static";

export function assetUrl(path: string): string {
  if (!path) return path;
  if (/^https?:\/\//i.test(path)) return path;
  let p = path.startsWith("/") ? path : `/${path}`;
  const b = (STATIC_ASSET_BASE || "").trim().replace(/\/+$/, "");
  if (!b) return p;
  return `${b}${p}`;
}
