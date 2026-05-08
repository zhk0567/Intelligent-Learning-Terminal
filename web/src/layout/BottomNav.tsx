import { Link, useLocation } from "react-router-dom";
import { Music, BookOpen, Edit, Shop, User } from "../components/Icon";
import { useCartStore } from "../store/cartStore";

const TABS = [
  { to: "/music", label: "音乐", icon: Music },
  { to: "/story", label: "故事", icon: BookOpen },
  { to: "/create", label: "创作", icon: Edit },
  { to: "/shop", label: "商城", icon: Shop },
  { to: "/me", label: "我的", icon: User },
];

export default function BottomNav() {
  const { pathname } = useLocation();
  const cart = useCartStore((s) => s.pending);

  return (
    <nav
      className="fixed bottom-0 left-1/2 -translate-x-1/2 z-40 w-full max-w-phone bg-bg-card/95 backdrop-blur-md border-t border-border/50"
      style={{ paddingBottom: "max(0px, env(safe-area-inset-bottom))" }}
    >
      <div className="grid grid-cols-5 h-16">
        {TABS.map((t) => {
          const active = pathname.startsWith(t.to);
          const Icon = t.icon;
          return (
            <Link
              key={t.to}
              to={t.to}
              replace
              className={`relative flex flex-col items-center justify-center gap-0.5 transition-colors ${
                active ? "text-neon-teal" : "text-text-hint"
              }`}
            >
              {active && (
                <span
                  aria-hidden
                  className="absolute top-0 left-1/2 -translate-x-1/2 h-[3px] w-7 rounded-b-full bg-ancient-cinnabar"
                />
              )}
              <div className="relative">
                <Icon size={22} strokeWidth={active ? 2.2 : 1.6} />
                {t.to === "/shop" && cart > 0 && (
                  <span className="absolute -right-2 -top-1 min-w-4 h-4 px-1 rounded-full bg-ancient-cinnabar text-[10px] text-white grid place-items-center shadow-stampInset">
                    {cart > 99 ? "99+" : cart}
                  </span>
                )}
              </div>
              <span className={`text-[11px] leading-none ${active ? "font-semibold" : ""}`}>{t.label}</span>
            </Link>
          );
        })}
      </div>
    </nav>
  );
}
