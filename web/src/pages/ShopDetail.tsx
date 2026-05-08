import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import TopBar from "../components/TopBar";
import Cover from "../components/Cover";
import Sheet from "../components/Sheet";
import { ALL_PRODUCTS } from "../data/shop";
import { Cart, Heart, HeartFilled, Minus, Plus, Share, Star } from "../components/Icon";
import { useCartStore } from "../store/cartStore";
import { toast } from "../components/Toast";

const SPECS = ["标准款", "礼盒装", "联名限定"];

export default function ShopDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const product = ALL_PRODUCTS.find((p) => p.id === id) ?? ALL_PRODUCTS[0];
  const [fav, setFav] = useState(false);
  const [sheetMode, setSheetMode] = useState<null | "cart" | "buy">(null);
  const [spec, setSpec] = useState(SPECS[0]);
  const [count, setCount] = useState(1);
  const addCart = useCartStore((s) => s.add);

  const submit = () => {
    if (sheetMode === "cart") {
      addCart(count);
      toast(`${count} 件 ${spec} 已加入购物车`);
    } else if (sheetMode === "buy") {
      toast("已下单，等待付款（演示）");
      navigate("/shop/order");
    }
    setSheetMode(null);
  };

  return (
    <div className="min-h-[100dvh] pb-24 md:min-h-0 md:pb-0 md:max-w-[960px] md:mx-auto">
      <TopBar title={product.name} right={<button className="text-text-primary"><Share size={20} /></button>} />
      <div className="md:grid md:grid-cols-[420px_1fr] md:gap-6 md:items-start">
      <Cover
        seed={product.id}
        src={product.detailHeroSrc || product.imageSrc}
        alt={product.name}
        text={product.name}
        aspect="aspect-square"
        rounded="rounded-none md:rounded-2xl"
        className="md:border md:border-border/40"
        ornate={false}
      />

      <div className="px-4 pt-3 md:px-0 md:pt-0">
        <div className="flex items-baseline gap-2">
          <span className="text-ancient-cinnabar text-2xl font-bold">¥{product.price}</span>
          {product.originalPrice && (
            <span className="text-text-hint text-sm line-through">¥{product.originalPrice}</span>
          )}
          {product.isNew && (
            <span className="rounded-full bg-ancient-bronze/15 text-ancient-bronze text-[10px] px-2 py-0.5 border border-ancient-bronze/30">新品</span>
          )}
        </div>
        <h1 className="mt-1 text-base font-bold text-text-primary">{product.name}</h1>
        <div className="mt-1 text-xs text-text-secondary">{product.description}</div>
        <div className="mt-2 flex items-center gap-3 text-xs text-text-hint">
          <span className="flex items-center gap-0.5"><Star size={12} className="text-ancient-bronze" /> {product.rating}</span>
          <span>已售 {product.salesCount}</span>
          <span>{product.category}</span>
        </div>

        <div className="mt-4 rounded-2xl border border-border/50 bg-bg-card p-3 space-y-2">
          <Row label="发货" value="48 小时内 · 国内包邮" />
          <Row label="服务" value="7 天无理由 · 假一赔三" />
          <Row label="材质" value={product.tags.join(" / ")} />
        </div>

        <div className="mt-3 rounded-2xl border border-border/50 bg-bg-card p-3">
          <div className="flex items-center">
            <span className="inline-block w-1 h-4 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
            <div className="text-sm font-bold text-text-primary">商品详情</div>
          </div>
          <p className="mt-2 text-sm leading-7 text-text-secondary">
            本商品由非遗传承人参与设计，融合传统工艺与现代审美。每件均为手工/小批量生产，可能存在轻微色差，敬请理解。
          </p>
          <div className="mt-3 grid grid-cols-2 gap-2">
            <Cover
              seed={`${product.id}-d1`}
              src={product.detailSrc1 || product.imageSrc}
              alt="工艺"
              aspect="aspect-square"
              rounded="rounded-lg"
              ornate={false}
            />
            <Cover
              seed={`${product.id}-d2`}
              src={product.detailSrc2 || product.imageSrc}
              alt="细节"
              aspect="aspect-square"
              rounded="rounded-lg"
              ornate={false}
            />
          </div>
        </div>
      </div>
      </div>

      <div
        className="fixed bottom-0 left-1/2 -translate-x-1/2 w-full max-w-phone bg-bg-card border-t border-border/50 px-3 py-2 flex items-center gap-2 md:static md:translate-x-0 md:left-auto md:max-w-none md:mt-4 md:rounded-card md:border md:border-border/50 md:px-4 md:py-3"
        style={{ paddingBottom: "max(0.5rem, env(safe-area-inset-bottom))" }}
      >
        <button onClick={() => setFav((v) => !v)} className="h-12 w-12 grid place-items-center text-text-primary">
          {fav ? <HeartFilled className="text-ancient-cinnabar" /> : <Heart />}
        </button>
        <button
          onClick={() => navigate("/shop/cart")}
          className="h-12 w-12 grid place-items-center text-text-primary"
        >
          <Cart />
        </button>
        <button
          onClick={() => setSheetMode("cart")}
          className="btn-ghost flex-1 rounded-full py-2 text-sm"
        >
          加入购物车
        </button>
        <button
          onClick={() => setSheetMode("buy")}
          className="btn-stamp flex-1 rounded-full py-2 text-sm"
        >
          立即购买
        </button>
      </div>

      <Sheet
        open={sheetMode !== null}
        onClose={() => setSheetMode(null)}
        title={sheetMode === "buy" ? "确认下单" : "选择规格"}
      >
        <div className="flex min-w-0 items-start gap-3">
          <Cover
            seed={product.id}
            src={product.imageSrc}
            alt={product.name}
            aspect="aspect-square"
            rounded="rounded-lg"
            className="h-20 w-20 shrink-0"
            text={product.name.slice(0, 2)}
            ornate={false}
          />
          <div className="min-w-0 flex-1 text-left">
            <div className="truncate text-sm font-semibold text-text-primary">{product.name}</div>
            <div className="text-ancient-cinnabar font-bold mt-1">¥{product.price}</div>
            <div className="text-xs text-text-hint">已选：{spec} × {count}</div>
          </div>
        </div>
        <div className="mt-4">
          <div className="text-xs text-text-secondary">规格</div>
          <div className="mt-2 flex flex-wrap gap-2">
            {SPECS.map((s) => (
              <button
                key={s}
                onClick={() => setSpec(s)}
                className={`px-3 py-1 rounded-full text-xs border transition-colors ${
                  spec === s
                    ? "bg-ancient-cinnabar text-white border-ancient-cinnabar shadow-stampInset"
                    : "bg-bg-cardElevated/60 text-text-primary border-border/50 hover:border-ancient-bronze/60"
                }`}
              >
                {s}
              </button>
            ))}
          </div>
        </div>
        <div className="mt-4 flex items-center justify-between">
          <span className="text-xs text-text-secondary">数量</span>
          <div className="flex items-center gap-3">
            <button
              onClick={() => setCount((c) => Math.max(1, c - 1))}
              className="h-8 w-8 grid place-items-center rounded-full border border-border/50 text-text-primary"
            >
              <Minus size={16} />
            </button>
            <span className="text-base font-semibold text-text-primary w-6 text-center">{count}</span>
            <button
              onClick={() => setCount((c) => c + 1)}
              className="h-8 w-8 grid place-items-center rounded-full border border-border/50 text-text-primary"
            >
              <Plus size={16} />
            </button>
          </div>
        </div>
        <button
          onClick={submit}
          className="btn-stamp mt-5 w-full rounded-full py-3 text-sm font-semibold"
        >
          {sheetMode === "buy" ? "去结算" : "加入购物车"}
        </button>
      </Sheet>
    </div>
  );
}

function Row({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex items-start gap-3 text-xs">
      <span className="w-12 text-text-hint">{label}</span>
      <span className="flex-1 text-text-primary">{value}</span>
    </div>
  );
}
