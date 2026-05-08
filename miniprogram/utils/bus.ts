/**
 * 极简 EventBus：
 *  - on/off 注册监听
 *  - emit 触发
 * 用于跨页面通信（toast/dialog 等已用 wx 原生 API 时不必使用）。
 */

type Handler = (...args: any[]) => void;

const map = new Map<string, Set<Handler>>();

export function on(event: string, fn: Handler): () => void {
  let set = map.get(event);
  if (!set) {
    set = new Set();
    map.set(event, set);
  }
  set.add(fn);
  return () => off(event, fn);
}

export function off(event: string, fn: Handler) {
  map.get(event)?.delete(fn);
}

export function emit(event: string, ...args: any[]) {
  map.get(event)?.forEach((fn) => fn(...args));
}
