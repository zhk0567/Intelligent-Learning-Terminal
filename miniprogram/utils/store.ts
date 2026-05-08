/**
 * 轻量订阅式 store，类似 zustand：
 *
 *   const useFoo = createStore({
 *     state: { count: 0 },
 *     actions: (set, get) => ({ inc: () => set({ count: get().count + 1 }) }),
 *   });
 *
 *   // Page 中：
 *   onLoad() {
 *     this.unsubFoo = useFoo.bind(this, (s) => ({ count: s.count }));
 *   }
 *   onUnload() { this.unsubFoo?.(); }
 *
 *   // 修改：
 *   useFoo.actions.inc();
 *   // 读取：
 *   useFoo.get().count;
 */

type Listener<T> = (state: T) => void;

export interface Store<TState, TActions> {
  get: () => TState;
  set: (patch: Partial<TState> | ((s: TState) => Partial<TState>)) => void;
  subscribe: (listener: Listener<TState>) => () => void;
  /** 在 Page/Component 上绑定 selector，自动 setData，返回 unsubscribe。 */
  bind: <K extends string>(
    target: WechatMiniprogram.IAnyObject,
    selector: (s: TState) => Record<K, any>,
  ) => () => void;
  actions: TActions;
}

export interface CreateOptions<TState, TActions> {
  state: TState;
  actions: (
    set: (patch: Partial<TState> | ((s: TState) => Partial<TState>)) => void,
    get: () => TState,
  ) => TActions;
  /** 持久化到 wx.storage：传入 key + 序列化状态选择器。 */
  persist?: {
    key: string;
    pick: (s: TState) => Partial<TState>;
    hydrate?: (raw: any) => Partial<TState>;
  };
}

export function createStore<TState, TActions>(
  opt: CreateOptions<TState, TActions>,
): Store<TState, TActions> {
  let state: TState = opt.state;

  if (opt.persist) {
    try {
      const raw = wx.getStorageSync(opt.persist.key);
      if (raw) {
        const patch = opt.persist.hydrate ? opt.persist.hydrate(raw) : (raw as Partial<TState>);
        state = { ...state, ...patch };
      }
    } catch {
      /* ignore */
    }
  }

  const listeners = new Set<Listener<TState>>();

  const get = (): TState => state;
  const set = (patch: Partial<TState> | ((s: TState) => Partial<TState>)) => {
    const next = typeof patch === "function" ? (patch as (s: TState) => Partial<TState>)(state) : patch;
    state = { ...state, ...next };
    if (opt.persist) {
      try {
        const slice = opt.persist.pick(state);
        wx.setStorageSync(opt.persist.key, slice);
      } catch {
        /* ignore */
      }
    }
    listeners.forEach((cb) => cb(state));
  };
  const subscribe = (cb: Listener<TState>) => {
    listeners.add(cb);
    return () => listeners.delete(cb);
  };

  const bind = <K extends string>(
    target: WechatMiniprogram.IAnyObject,
    selector: (s: TState) => Record<K, any>,
  ) => {
    const apply = (s: TState) => {
      const slice = selector(s);
      const data = (target.data || {}) as Record<K, any>;
      const patch: Record<string, any> = {};
      let changed = false;
      for (const k in slice) {
        if (slice[k] !== data[k]) {
          patch[k] = slice[k];
          changed = true;
        }
      }
      if (changed) {
        target.setData(patch);
      }
    };
    apply(state);
    return subscribe(apply);
  };

  const actions = opt.actions(set, get);

  return { get, set, subscribe, bind, actions };
}
