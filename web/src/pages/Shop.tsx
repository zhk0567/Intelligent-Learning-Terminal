import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  ALL_PRODUCTS,
  filterByCategory,
  Product,
  SHOP_BANNERS,
  SHOP_CATEGORY_TABS,
  ShopCategoryId,
} from "../data/shop";
import Cover from "../components/Cover";
import { Cart, Search } from "../components/Icon";
import Skeleton from "../components/Skeleton";
import { useCartStore } from "../store/cartStore";

const PAGE_SIZE = 8;

export default function Shop() {
  const [active, setActive] = useState<ShopCategoryId>("all");
  const [bannerIdx, setBannerIdx] = useState(0);
  const [page, setPage] = useState(1);
  const [skeleton, setSkeleton] = useState(true);
  const navigate = useNavigate();
  const cart = useCartStore((s) => s.pending);
  const addCart = useCartStore((s) => s.add);

  useEffect(() => {
    setSkeleton(true);
    const t = setTimeout(() => setSkeleton(false), 400);
    return () => clearTimeout(t);
  }, [active]);

  useEffect(() => {
    const id = setInterval(
      () => setBannerIdx((v) => (v + 1) % SHOP_BANNERS.length),
      4000,
    );
    return () => clearInterval(id);
  }, []);

  const filtered = filterByCategory(ALL_PRODUCTS, active);
  const list = filtered.slice(0, page * PAGE_SIZE);
  const hasMore = list.length < filtered.length;

  return (
    <div className="px-3 pt-2 md:px-0 md:pt-0">
      <div
        className="flex items-center gap-2 py-1 md:hidden"
        style={{ paddingTop: "max(0.25rem, env(safe-area-inset-top))" }}
      >
        <div
          className="flex-1 flex items-center gap-2 rounded-full bg-bg-card border border-border/60 px-3 py-2 text-text-secondary"
          onClick={() => navigate("/search")}
        >
          <Search size={18} />
          <span className="text-sm">搜索商品</span>
        </div>
        <button
          className="relative h-10 w-10 grid place-items-center rounded-full bg-bg-card border border-border/60 text-text-primary"
          onClick={() => navigate("/shop/cart")}
        >
          <Cart size={20} />
          {cart > 0 && (
            <span className="absolute -right-1 -top-1 min-w-4 h-4 px-1 rounded-full bg-ancient-red text-[10px] text-white grid place-items-center">
              {cart > 99 ? "99+" : cart}
            </span>
          )}
        </button>
      </div>

      <div className="mt-3 md:mt-0 w-full max-h-[200px] sm:max-h-[240px] md:max-h-[260px] overflow-hidden rounded-2xl border border-ancient-bronze/40 dark:border-ancient-bronze/30">
        <Cover
          seed={SHOP_BANNERS[bannerIdx].id}
          src={SHOP_BANNERS[bannerIdx].imageSrc}
          alt={SHOP_BANNERS[bannerIdx].title}
          aspect="aspect-[16/7] md:aspect-[16/5]"
          rounded="rounded-none"
          className="w-full border-0"
          ornate={false}
        />
        <div className="mt-2 text-center text-xs text-text-secondary">
          {SHOP_BANNERS[bannerIdx].subtitle}
        </div>
        <div className="mt-2 flex justify-center gap-1">
          {SHOP_BANNERS.map((_, i) => (
            <span
              key={i}
              className={`h-1.5 rounded-full ${
                i === bannerIdx ? "w-5 bg-neon-teal" : "w-1.5 bg-border/50"
              }`}
            />
          ))}
        </div>
      </div>

      <div className="mt-3 flex gap-2 overflow-x-auto no-scrollbar md:flex-wrap md:overflow-visible">
        {SHOP_CATEGORY_TABS.map((t) => (
          <button
            key={t.id}
            onClick={() => {
              setActive(t.id);
              setPage(1);
            }}
            className={`px-3 py-1.5 rounded-full text-sm whitespace-nowrap border ${
              active === t.id
                ? "bg-neon-teal text-white border-neon-teal"
                : "bg-bg-card text-text-secondary border-border/50"
            }`}
          >
            {t.label}
          </button>
        ))}
      </div>

      <div className="mt-3 grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-3 md:gap-4 pb-4">
        {skeleton
          ? Array.from({ length: 6 }).map((_, i) => (
              <div key={i} className="space-y-2">
                <Skeleton className="aspect-square w-full" rounded="rounded-xl" />
                <Skeleton className="h-3 w-4/5" />
                <Skeleton className="h-3 w-3/5" />
              </div>
            ))
          : list.map((p) => (
              <ProductCard
                key={p.id}
                p={p}
                onOpen={() => navigate(`/shop/detail/${p.id}`)}
                onAdd={() => addCart(1)}
              />
            ))}
      </div>

      {!skeleton && hasMore && (
        <button
          onClick={() => setPage((v) => v + 1)}
          className="mb-6 w-full rounded-full border border-border/60 bg-bg-card py-2.5 text-sm text-text-secondary"
        >
          加载更多
        </button>
      )}
      {!skeleton && !hasMore && (
        <div className="mb-6 text-center text-xs text-text-hint">— 全部加载完成 —</div>
      )}
    </div>
  );
}

function ProductCard({ p, onOpen, onAdd }: { p: Product; onOpen: () => void; onAdd: () => void }) {
  return (
    <div className="rounded-xl bg-bg-card border border-border/40 overflow-hidden flex flex-col card-hover">
      <button onClick={onOpen} className="text-left">
        <Cover
          seed={p.id}
          src={p.imageSrc}
          alt={p.name}
          text={p.name.slice(0, 4)}
          aspect="aspect-square"
          rounded="rounded-none"
          ornate={false}
        />
        <div className="p-2">
          <div className="line-clamp-1 text-sm font-semibold text-text-primary">{p.name}</div>
          <div className="mt-1 flex items-center gap-1">
            <span className="text-ancient-cinnabar text-base font-bold">¥{p.price}</span>
            {p.originalPrice && (
              <span className="text-xs text-text-hint line-through">¥{p.originalPrice}</span>
            )}
          </div>
          <div className="mt-1 flex items-center justify-between text-[11px] text-text-hint">
            <span className="text-ancient-bronze">★ {p.rating}</span>
            <span>已售 {p.salesCount}</span>
          </div>
        </div>
      </button>
      <button
        className="mb-2 mx-2 mt-auto rounded-full border border-ancient-bronze/40 text-ancient-bronze hover:bg-ancient-bronze/10 py-1 text-xs transition-colors"
        onClick={onAdd}
      >
        + 加入购物车
      </button>
    </div>
  );
}
