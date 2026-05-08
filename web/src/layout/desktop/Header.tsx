import { useEffect, useRef, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import {
  ArrowLeft,
  Bell,
  Search,
  Settings,
  User,
  Edit,
  Sun,
  Moon,
} from "../../components/Icon";
import { useThemeStore } from "../../store/themeStore";
import { useSessionStore } from "../../store/sessionStore";
import { APP_IMAGES } from "../../assets/appImages";
import Cover from "../../components/Cover";
import { PatternStrip } from "../../components/Ornament";

const ROOT_PATHS = ["/music", "/story", "/create", "/shop", "/me"];

const TITLE_MAP: Record<string, string> = {
  "/music": "音乐",
  "/story": "故事",
  "/create": "创作",
  "/shop": "商城",
  "/me": "我的",
  "/search": "搜索",
  "/search/result": "搜索结果",
  "/player": "播放器",
  "/story/list": "故事列表",
  "/create/publish": "发布作品",
  "/create/challenge": "挑战赛",
  "/shop/cart": "购物车",
  "/shop/order": "确认订单",
  "/shop/address": "收货地址",
  "/me/setting": "设置",
  "/me/favorite": "我的收藏",
  "/me/history": "浏览历史",
  "/me/works": "我的作品",
  "/me/learning": "我的学习",
  "/me/edit": "编辑资料",
  "/about": "关于古韵薪传",
};

export default function Header() {
  const navigate = useNavigate();
  const { pathname } = useLocation();
  const { mode, toggle } = useThemeStore();
  const user = useSessionStore((s) => s.user);
  const logout = useSessionStore((s) => s.logout);
  const [menuOpen, setMenuOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!menuOpen) return;
    const onDoc = (e: MouseEvent) => {
      if (!menuRef.current?.contains(e.target as Node)) setMenuOpen(false);
    };
    document.addEventListener("mousedown", onDoc);
    return () => document.removeEventListener("mousedown", onDoc);
  }, [menuOpen]);

  const isRoot = ROOT_PATHS.includes(pathname);
  const dynamicTitle =
    TITLE_MAP[pathname] ??
    (pathname.startsWith("/story/")
      ? "故事详情"
      : pathname.startsWith("/detail/")
      ? "专辑详情"
      : pathname.startsWith("/create/work/")
      ? "作品详情"
      : pathname.startsWith("/create/creator/")
      ? "创作者主页"
      : pathname.startsWith("/shop/detail/")
      ? "商品详情"
      : "古韵薪传");

  return (
    <header
      className="hidden md:flex items-center gap-3 h-14 px-5 border-b border-border/40 bg-bg-card relative"
      style={{ gridArea: "header" }}
    >
      <span className="absolute left-0 right-0 -bottom-px pointer-events-none">
        <PatternStrip height={6} opacity={0.22} />
      </span>
      {!isRoot ? (
        <button
          onClick={() => navigate(-1)}
          className="h-9 w-9 grid place-items-center rounded-full text-text-primary hover:bg-bg-cardElevated/60 transition-colors"
          aria-label="返回"
        >
          <ArrowLeft size={20} />
        </button>
      ) : (
        <div className="w-2" />
      )}

      <div className="flex items-baseline gap-1.5 min-w-0">
        <span className="inline-block w-1 h-4 rounded-sm bg-ancient-cinnabar shrink-0" aria-hidden />
        <h1 className="text-[15px] font-bold text-text-primary truncate">{dynamicTitle}</h1>
      </div>

      <div className="flex-1" />

      <button
        onClick={() => navigate("/search")}
        className="flex items-center gap-2 rounded-full bg-bg-cardElevated/60 border border-border/40 px-3 py-1.5 text-text-secondary w-[280px] lg:w-[360px] hover:border-ancient-bronze/60 hover:text-ancient-bronze transition-colors"
      >
        <Search size={16} />
        <span className="text-xs">搜索曲目、故事、商品…</span>
      </button>

      <button
        className="h-9 w-9 grid place-items-center rounded-full text-text-primary hover:bg-bg-cardElevated/60"
        aria-label="通知"
      >
        <Bell size={18} />
      </button>

      <button
        onClick={toggle}
        className="h-9 w-9 grid place-items-center rounded-full text-text-primary hover:bg-bg-cardElevated/60"
        aria-label="切换主题"
        title={mode === "dark" ? "切换到浅色" : "切换到深色"}
      >
        {mode === "dark" ? <Sun size={18} /> : <Moon size={18} />}
      </button>

      <div className="relative" ref={menuRef}>
        <button
          onClick={() => setMenuOpen((v) => !v)}
          className="h-9 w-9 rounded-full overflow-hidden ring-1 ring-ancient-bronze/50 hover:ring-ancient-bronze transition-all"
          aria-label="账户菜单"
        >
          <Cover
            seed={user.name}
            src={APP_IMAGES.banner1}
            alt={user.name}
            text={user.name.slice(0, 1)}
            aspect="aspect-square"
            rounded="rounded-full"
            ornate={false}
          />
        </button>
        {menuOpen && (
          <div className="absolute right-0 top-11 w-44 rounded-xl border border-border/50 bg-bg-card shadow-card-dark py-1 animate-fadeIn z-50">
            <MenuItem
              icon={<Edit size={16} />}
              label="编辑资料"
              onClick={() => {
                setMenuOpen(false);
                navigate("/me/edit");
              }}
            />
            <MenuItem
              icon={<Settings size={16} />}
              label="设置"
              onClick={() => {
                setMenuOpen(false);
                navigate("/me/setting");
              }}
            />
            <div className="my-1 border-t border-border/30" />
            <MenuItem
              icon={<User size={16} />}
              label="退出登录"
              onClick={() => {
                setMenuOpen(false);
                logout();
                navigate("/login", { replace: true });
              }}
              tone="danger"
            />
          </div>
        )}
      </div>
    </header>
  );
}

function MenuItem({
  icon,
  label,
  onClick,
  tone,
}: {
  icon: React.ReactNode;
  label: string;
  onClick: () => void;
  tone?: "danger";
}) {
  return (
    <button
      onClick={onClick}
      className={`w-full flex items-center gap-2 px-3 py-2 text-sm hover:bg-bg-cardElevated/60 ${
        tone === "danger" ? "text-ancient-red" : "text-text-primary"
      }`}
    >
      <span className="text-text-hint">{icon}</span>
      <span>{label}</span>
    </button>
  );
}
