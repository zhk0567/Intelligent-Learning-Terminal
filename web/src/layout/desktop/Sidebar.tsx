import { NavLink } from "react-router-dom";
import {
  BookOpen,
  Clock,
  Edit,
  Heart,
  Music,
  Settings,
  Shop,
  Star,
  Trophy,
  User,
} from "../../components/Icon";
import { useSessionStore } from "../../store/sessionStore";
import { useCartStore } from "../../store/cartStore";
import { APP_IMAGES } from "../../assets/appImages";
import Cover from "../../components/Cover";
import SealStamp from "../../components/SealStamp";

interface NavSpec {
  to: string;
  label: string;
  icon: React.ComponentType<{ size?: number; className?: string; strokeWidth?: number }>;
}

const PRIMARY: NavSpec[] = [
  { to: "/music", label: "音乐", icon: Music },
  { to: "/story", label: "故事", icon: BookOpen },
  { to: "/create", label: "创作", icon: Edit },
  { to: "/shop", label: "商城", icon: Shop },
  { to: "/me", label: "我的", icon: User },
];

const PERSONAL: NavSpec[] = [
  { to: "/me/favorite", label: "我的收藏", icon: Heart },
  { to: "/me/history", label: "浏览历史", icon: Clock },
  { to: "/me/works", label: "我的作品", icon: Trophy },
  { to: "/me/learning", label: "我的学习", icon: BookOpen },
  { to: "/me/edit", label: "编辑资料", icon: Edit },
  { to: "/me/setting", label: "系统设置", icon: Settings },
  { to: "/about", label: "关于古韵", icon: Star },
];

export default function Sidebar() {
  const user = useSessionStore((s) => s.user);
  const cart = useCartStore((s) => s.pending);

  return (
    <aside
      className="hidden md:flex flex-col h-full w-[240px] shrink-0 border-r border-border/40 bg-bg-card"
      style={{ gridArea: "side" }}
    >
      <div className="flex items-center gap-2 px-5 h-14 border-b border-border/40">
        <SealStamp text="韵" size={28} />
        <span className="text-base font-bold text-text-primary tracking-wide">古韵薪传</span>
      </div>

      <nav className="flex-1 overflow-y-auto py-3">
        <SectionLabel label="Discover · 发现" />
        {PRIMARY.map((n) => (
          <Item key={n.to} spec={n} badge={n.to === "/shop" && cart > 0 ? cart : undefined} />
        ))}

        <SectionLabel label="My · 我的资源" />
        {PERSONAL.map((n) => (
          <Item key={n.to} spec={n} />
        ))}
      </nav>

      <div className="border-t border-border/40 p-3">
        <NavLink
          to="/me/edit"
          className="flex items-center gap-3 rounded-xl px-2 py-2 hover:bg-bg-cardElevated/60 transition-colors"
        >
          <div className="relative shrink-0">
            <Cover
              seed={user.name}
              src={APP_IMAGES.banner1}
              alt={user.name}
              text={user.name.slice(0, 1)}
              aspect="aspect-square"
              rounded="rounded-full"
              className="h-10 w-10 ring-1 ring-ancient-bronze/50"
              ornate={false}
            />
            {user.vip && (
              <SealStamp
                text="VIP"
                size={18}
                className="absolute -right-1 -bottom-1"
              />
            )}
          </div>
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-1">
              <span className="truncate text-sm font-semibold text-text-primary">{user.name}</span>
            </div>
            <div className="truncate text-xs text-text-hint">{user.phone}</div>
          </div>
        </NavLink>
      </div>
    </aside>
  );
}

function SectionLabel({ label }: { label: string }) {
  return (
    <div className="px-5 pt-4 pb-1 text-[10px] font-semibold uppercase tracking-[0.18em] text-ancient-bronze/85">
      {label}
    </div>
  );
}

function Item({ spec, badge }: { spec: NavSpec; badge?: number }) {
  const Icon = spec.icon;
  return (
    <NavLink
      to={spec.to}
      end
      className={({ isActive }) =>
        `relative flex items-center gap-3 px-5 py-2 text-sm transition-colors ${
          isActive
            ? "bg-neon-teal/12 text-neon-teal font-semibold"
            : "text-text-secondary hover:bg-bg-cardElevated/60 hover:text-text-primary"
        }`
      }
    >
      {({ isActive }) => (
        <>
          <span
            aria-hidden
            className={`absolute left-0 top-1/2 -translate-y-1/2 h-5 w-1 rounded-r-sm transition-colors ${
              isActive ? "bg-ancient-cinnabar" : "bg-transparent"
            }`}
          />
          <Icon size={18} />
          <span className="flex-1 truncate">{spec.label}</span>
          {badge !== undefined && (
            <span className="rounded-full bg-ancient-cinnabar text-white text-[10px] px-1.5 py-0.5 min-w-4 text-center shadow-stampInset">
              {badge > 99 ? "99+" : badge}
            </span>
          )}
        </>
      )}
    </NavLink>
  );
}
