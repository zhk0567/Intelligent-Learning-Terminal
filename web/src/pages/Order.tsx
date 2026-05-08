import { useState } from "react";
import { useNavigate } from "react-router-dom";
import TopBar from "../components/TopBar";
import Cover from "../components/Cover";
import { ALL_PRODUCTS } from "../data/shop";
import { ADDRESSES } from "../data/address";
import { MapPin } from "../components/Icon";
import { toast } from "../components/Toast";

const ORDER_ITEMS = [
  { productId: "s1", count: 2 },
  { productId: "s3", count: 1 },
];

export default function Order() {
  const [pay, setPay] = useState<"alipay" | "wechat" | "card">("alipay");
  const navigate = useNavigate();
  const addr = ADDRESSES.find((a) => a.isDefault) ?? ADDRESSES[0];

  const subtotal = ORDER_ITEMS.reduce((s, i) => {
    const p = ALL_PRODUCTS.find((x) => x.id === i.productId)!;
    return s + p.price * i.count;
  }, 0);
  const ship = subtotal > 99 ? 0 : 8;
  const total = subtotal + ship;

  return (
    <div className="min-h-[100dvh] pb-24 md:min-h-0 md:pb-0 md:max-w-[720px] md:mx-auto">
      <TopBar title="确认订单" />
      <div className="hidden md:flex items-center pb-3 px-1">
        <span className="inline-block w-1 h-6 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
        <h1 className="text-xl font-bold text-text-primary">确认订单</h1>
      </div>
      <div className="px-3 pt-2 md:px-0 md:pt-0 space-y-3">
        <button
          onClick={() => navigate("/shop/address")}
          className="w-full rounded-2xl bg-bg-card border border-border/50 p-3 text-left flex items-start gap-3 card-hover"
        >
          <MapPin size={20} className="text-ancient-cinnabar mt-0.5" />
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-2">
              <span className="text-sm font-semibold text-text-primary">{addr.name}</span>
              <span className="text-xs text-text-secondary">{addr.phone}</span>
              {addr.isDefault && (
                <span className="rounded border border-ancient-cinnabar/40 bg-ancient-cinnabar/10 text-ancient-cinnabar text-[10px] px-1.5 py-0.5">默认</span>
              )}
            </div>
            <div className="text-xs text-text-secondary mt-1">{addr.detail}</div>
          </div>
          <span className="text-text-hint">›</span>
        </button>

        <div className="rounded-2xl border border-border/50 bg-bg-card p-3 space-y-3">
          {ORDER_ITEMS.map((it) => {
            const p = ALL_PRODUCTS.find((x) => x.id === it.productId)!;
            return (
              <div key={it.productId} className="flex min-w-0 items-start gap-3">
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
                  <div className="text-xs text-text-hint">标准款</div>
                  <div className="mt-1 flex items-center justify-between">
                    <span className="text-ancient-cinnabar font-bold">¥{p.price}</span>
                    <span className="text-xs text-text-hint">x{it.count}</span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        <div className="rounded-2xl border border-border/50 bg-bg-card p-3 text-sm">
          <Row label="商品金额" value={`¥${subtotal.toFixed(2)}`} />
          <Row label="运费" value={ship === 0 ? "免运费" : `¥${ship}`} />
          <Row label="优惠券" value="暂无可用" />
        </div>

        <div className="rounded-2xl border border-border/50 bg-bg-card p-3">
          <div className="flex items-center mb-2">
            <span className="inline-block w-1 h-4 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
            <div className="text-sm font-bold text-text-primary">支付方式</div>
          </div>
          {[
            { id: "alipay", label: "支付宝", color: "#1677FF" },
            { id: "wechat", label: "微信支付", color: "#10B981" },
            { id: "card", label: "银行卡", color: "#A57C2C" },
          ].map((m) => (
            <button
              key={m.id}
              onClick={() => setPay(m.id as typeof pay)}
              className="flex w-full items-center gap-3 py-2"
            >
              <span className="h-8 w-8 rounded-full grid place-items-center text-white text-xs font-bold" style={{ background: m.color }}>
                {m.label.slice(0, 1)}
              </span>
              <span className="flex-1 text-left text-sm text-text-primary">{m.label}</span>
              <span
                className={`h-5 w-5 rounded-full border-2 transition-colors ${
                  pay === m.id ? "bg-ancient-cinnabar border-ancient-cinnabar" : "border-border"
                }`}
              />
            </button>
          ))}
        </div>
      </div>

      <div
        className="fixed bottom-0 left-1/2 -translate-x-1/2 w-full max-w-phone bg-bg-card border-t border-border/50 px-3 py-2 flex items-center gap-2 md:static md:translate-x-0 md:left-auto md:max-w-none md:mt-4 md:rounded-card md:border md:border-border/50 md:px-4 md:py-3"
        style={{ paddingBottom: "max(0.5rem, env(safe-area-inset-bottom))" }}
      >
        <div className="flex-1 text-sm">
          实付：<span className="text-ancient-cinnabar text-lg font-bold">¥{total.toFixed(2)}</span>
        </div>
        <button
          onClick={() => {
            toast("订单已提交（演示）");
            setTimeout(() => navigate("/me"), 600);
          }}
          className="btn-stamp rounded-full px-5 py-2 text-sm"
        >
          提交订单
        </button>
      </div>
    </div>
  );
}

function Row({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex items-center justify-between py-1">
      <span className="text-text-secondary text-xs">{label}</span>
      <span className="text-text-primary">{value}</span>
    </div>
  );
}
