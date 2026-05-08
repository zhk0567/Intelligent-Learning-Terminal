import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import TopBar from "../components/TopBar";
import { ALL_PRODUCTS } from "../data/shop";
import Cover from "../components/Cover";
import { Minus, Plus, Trash } from "../components/Icon";
import { useCartStore } from "../store/cartStore";
import { toast } from "../components/Toast";
import EmptyState from "../components/EmptyState";

interface Item {
  productId: string;
  count: number;
  selected: boolean;
}

const INIT: Item[] = [
  { productId: "s1", count: 2, selected: true },
  { productId: "s3", count: 1, selected: true },
  { productId: "s6", count: 1, selected: false },
];

export default function Cart() {
  const navigate = useNavigate();
  const [items, setItems] = useState<Item[]>(INIT);
  const clearCart = useCartStore((s) => s.clear);

  const total = useMemo(
    () =>
      items
        .filter((i) => i.selected)
        .reduce((sum, i) => {
          const p = ALL_PRODUCTS.find((x) => x.id === i.productId)!;
          return sum + p.price * i.count;
        }, 0),
    [items],
  );

  const allSel = items.length > 0 && items.every((i) => i.selected);

  return (
    <div className="min-h-[100dvh] pb-24 md:min-h-0 md:pb-0 md:max-w-[720px] md:mx-auto">
      <TopBar title="购物车" subtitle={`${items.length} 件商品`} />
      <div className="hidden md:flex items-center pb-3 px-1">
        <span className="inline-block w-1 h-6 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
        <h1 className="text-xl font-bold text-text-primary">购物车 <span className="text-sm text-text-hint font-normal">· {items.length} 件商品</span></h1>
      </div>
      <div className="px-3 pt-2 md:px-0 md:pt-0 space-y-2">
        {items.length === 0 && (
          <EmptyState text="购物车空空如也" hint="去逛逛非遗好物" icon="fan" />
        )}
        {items.map((it) => {
          const p = ALL_PRODUCTS.find((x) => x.id === it.productId)!;
          return (
            <div key={it.productId} className="flex min-w-0 items-start gap-3 rounded-2xl bg-bg-card border border-border/50 p-3">
              <button
                type="button"
                onClick={() =>
                  setItems(items.map((x) => (x.productId === it.productId ? { ...x, selected: !x.selected } : x)))
                }
                className={`h-5 w-5 shrink-0 rounded-full border-2 transition-colors ${
                  it.selected ? "bg-ancient-cinnabar border-ancient-cinnabar" : "border-border"
                }`}
                aria-label="选中"
              />
              <Cover
                seed={p.id}
                src={p.imageSrc}
                alt={p.name}
                text={p.name.slice(0, 2)}
                aspect="aspect-square"
                rounded="rounded-lg"
                className="h-16 w-16 shrink-0"
                ornate={false}
              />
              <div className="min-w-0 flex-1 text-left">
                <div className="truncate text-sm font-semibold text-text-primary">{p.name}</div>
                <div className="text-[11px] text-text-hint truncate">{p.description}</div>
                <div className="mt-1 flex items-center justify-between">
                  <span className="text-ancient-cinnabar font-bold">¥{p.price}</span>
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() =>
                        setItems(items.map((x) => (x.productId === it.productId ? { ...x, count: Math.max(1, x.count - 1) } : x)))
                      }
                      className="h-7 w-7 grid place-items-center rounded-full border border-border/50 text-text-primary"
                    >
                      <Minus size={14} />
                    </button>
                    <span className="w-6 text-center text-sm">{it.count}</span>
                    <button
                      onClick={() =>
                        setItems(items.map((x) => (x.productId === it.productId ? { ...x, count: x.count + 1 } : x)))
                      }
                      className="h-7 w-7 grid place-items-center rounded-full border border-border/50 text-text-primary"
                    >
                      <Plus size={14} />
                    </button>
                  </div>
                </div>
              </div>
              <button
                type="button"
                onClick={() => setItems(items.filter((x) => x.productId !== it.productId))}
                className="grid h-8 w-8 shrink-0 place-items-center text-text-hint"
              >
                <Trash size={16} />
              </button>
            </div>
          );
        })}
      </div>

      <div
        className="fixed bottom-0 left-1/2 -translate-x-1/2 w-full max-w-phone bg-bg-card border-t border-border/50 px-3 py-2 flex items-center gap-2 md:static md:translate-x-0 md:left-auto md:max-w-none md:mt-4 md:rounded-card md:border md:border-border/50 md:px-4 md:py-3"
        style={{ paddingBottom: "max(0.5rem, env(safe-area-inset-bottom))" }}
      >
        <button
          onClick={() => setItems(items.map((x) => ({ ...x, selected: !allSel })))}
          className="flex items-center gap-2 text-xs text-text-primary"
        >
          <span
            className={`h-5 w-5 rounded-full border-2 transition-colors ${
              allSel ? "bg-ancient-cinnabar border-ancient-cinnabar" : "border-border"
            }`}
          />
          全选
        </button>
        <div className="flex-1 text-right text-sm text-text-primary">
          合计：<span className="text-ancient-cinnabar text-base font-bold">¥{total.toFixed(2)}</span>
        </div>
        <button
          onClick={() => {
            if (total === 0) return toast("请选择商品");
            clearCart();
            navigate("/shop/order");
          }}
          className="btn-stamp rounded-full px-5 py-2 text-sm"
        >
          去结算
        </button>
      </div>
    </div>
  );
}
