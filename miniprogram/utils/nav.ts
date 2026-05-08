/**
 * 路由帮助：参数序列化 + 区分 tab / 普通页。
 */

const TAB_PATHS = new Set<string>([
  "/pages/music/index",
  "/pages/story/index",
  "/pages/create/index",
  "/pages/shop/index",
  "/pages/me/index",
]);

function buildUrl(path: string, params?: Record<string, string | number | boolean | undefined>): string {
  if (!params) return path;
  const qs = Object.entries(params)
    .filter(([, v]) => v !== undefined && v !== null && v !== "")
    .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(String(v))}`)
    .join("&");
  return qs ? `${path}?${qs}` : path;
}

export function go(
  path: string,
  params?: Record<string, string | number | boolean | undefined>,
) {
  const url = buildUrl(path, params);
  const base = path.split("?")[0];
  if (TAB_PATHS.has(base)) {
    wx.switchTab({ url: base });
    return;
  }
  wx.navigateTo({ url });
}

export function replace(
  path: string,
  params?: Record<string, string | number | boolean | undefined>,
) {
  const url = buildUrl(path, params);
  const base = path.split("?")[0];
  if (TAB_PATHS.has(base)) {
    wx.switchTab({ url: base });
    return;
  }
  wx.redirectTo({ url });
}

export function back(delta = 1) {
  wx.navigateBack({ delta });
}

export function reLaunch(
  path: string,
  params?: Record<string, string | number | boolean | undefined>,
) {
  wx.reLaunch({ url: buildUrl(path, params) });
}

/** 把 onLoad 回调中的 query 转成稳定 record。 */
export function parseQuery<T = Record<string, string>>(query: WechatMiniprogram.IAnyObject | undefined): T {
  return ((query as unknown) ?? {}) as T;
}
