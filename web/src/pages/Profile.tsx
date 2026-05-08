import { useNavigate } from "react-router-dom";
import {
  Bell,
  BookOpen,
  Clock,
  Edit,
  Footprint,
  Heart,
  Settings,
  Trophy,
  User,
} from "../components/Icon";
import { APP_IMAGES } from "../assets/appImages";
import Cover from "../components/Cover";
import SealStamp from "../components/SealStamp";
import { SectionTitleBar } from "../components/Ornament";
import { useSessionStore } from "../store/sessionStore";

export default function Profile() {
  const navigate = useNavigate();
  const user = useSessionStore((s) => s.user);

  return (
    <div className="flex min-h-[calc(100dvh-136px)] w-full flex-col justify-center pb-2 md:min-h-[calc(100dvh-56px-80px-2.5rem)] md:pb-0">
      <div className="w-full md:grid md:grid-cols-[280px_1fr] md:gap-6">
        <div
          className="px-4 pb-3 pt-3 bg-bg-secondary md:bg-bg-card md:rounded-card md:border md:border-border/40 md:p-5"
          style={{ paddingTop: "max(0.75rem, calc(env(safe-area-inset-top) + 0.5rem))" }}
        >
          <div className="flex items-center justify-between md:hidden">
            <div className="text-xl font-bold text-text-primary">我的</div>
            <div className="flex gap-2">
              <button
                className="h-9 w-9 grid place-items-center rounded-full bg-bg-card border border-border/60 text-text-primary"
                onClick={() => navigate("/me/setting")}
              >
                <Settings size={18} />
              </button>
              <button className="h-9 w-9 grid place-items-center rounded-full bg-bg-card border border-border/60 text-text-primary">
                <Bell size={18} />
              </button>
            </div>
          </div>

          <div className="mt-3 flex min-w-0 items-center gap-3 md:mt-0 md:flex-col md:items-center md:text-center md:gap-2">
            <div className="relative shrink-0">
              <Cover
                seed={user.name}
                src={APP_IMAGES.banner1}
                alt={user.name}
                text={user.name.slice(0, 1)}
                aspect="aspect-square"
                rounded="rounded-full"
                className="h-16 w-16 shrink-0 md:h-24 md:w-24 ring-2 ring-ancient-bronze/60"
                ornate={false}
              />
              {user.vip && (
                <SealStamp
                  text="VIP"
                  size={22}
                  className="absolute -right-1 -bottom-1 md:-right-2 md:-bottom-1"
                />
              )}
            </div>
            <div className="flex-1 min-w-0 md:flex-none">
              <div className="flex items-center gap-2 md:justify-center">
                <span className="text-base font-bold text-text-primary truncate">{user.name}</span>
              </div>
              <div className="text-xs text-text-secondary">{user.bio}</div>
              <div className="text-xs text-text-hint">{user.phone}</div>
            </div>
            <button
              className="btn-ghost rounded-full px-3 py-1.5 text-xs"
              onClick={() => navigate("/me/edit")}
            >
              编辑资料
            </button>
          </div>
        </div>

        <div className="md:space-y-4">
          {/* 统计三格：含外框 */}
          <div className="mx-3 mt-3 md:mx-0 md:mt-0 rounded-card border border-border/60 bg-bg-card p-3 grid grid-cols-3 gap-2">
            <Stat
              icon={<Heart size={20} />}
              label="收藏"
              value="23"
              onClick={() => navigate("/me/favorite")}
            />
            <Stat
              icon={<Footprint size={20} />}
              label="足迹"
              value="86"
              onClick={() => navigate("/me/history")}
            />
            <Stat
              icon={<Edit size={20} />}
              label="创作"
              value="9"
              onClick={() => navigate("/me/works")}
            />
          </div>

          {/* 常用功能 */}
          <div className="mx-3 mt-3 md:mx-0 md:mt-0 rounded-card border border-border/60 bg-bg-card p-3">
            <div className="px-1 pb-2 flex items-center">
              <SectionTitleBar />
              <div>
                <div className="text-sm font-bold text-text-primary leading-tight">常用功能</div>
                <div className="text-[10px] uppercase tracking-[0.18em] text-ancient-bronze/80">Quick Access</div>
              </div>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
              <FuncRow icon={<BookOpen size={18} />} label="我的学习" desc="持续学习中" onClick={() => navigate("/me/learning")} />
              <FuncRow icon={<Trophy size={18} />} label="我的作品" desc="已发布 9 件" onClick={() => navigate("/me/works")} />
              <FuncRow icon={<Heart size={18} />} label="我的收藏" desc="23 个内容" onClick={() => navigate("/me/favorite")} />
              <FuncRow icon={<Clock size={18} />} label="浏览历史" desc="近 7 天 86 条" onClick={() => navigate("/me/history")} />
              <FuncRow icon={<User size={18} />} label="编辑资料" desc="更新个人信息" onClick={() => navigate("/me/edit")} />
              <FuncRow icon={<Settings size={18} />} label="系统设置" desc="主题 · 安全 · 关于" onClick={() => navigate("/me/setting")} />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

function Stat({
  icon,
  label,
  value,
  onClick,
}: {
  icon: React.ReactNode;
  label: string;
  value: string;
  onClick: () => void;
}) {
  return (
    <button
      onClick={onClick}
      className="flex flex-col items-center gap-1 rounded-xl bg-bg-cardElevated/60 border border-border/40 py-3 text-text-primary card-hover group"
    >
      <span className="text-ancient-bronze group-hover:text-ancient-cinnabar transition-colors">{icon}</span>
      <span className="text-base font-bold">{value}</span>
      <span className="text-xs text-text-secondary">{label}</span>
    </button>
  );
}

function FuncRow({
  icon,
  label,
  desc,
  onClick,
}: {
  icon: React.ReactNode;
  label: string;
  desc: string;
  onClick: () => void;
}) {
  return (
    <button
      onClick={onClick}
      className="flex items-center gap-3 rounded-xl border border-border/40 bg-bg-cardElevated/40 px-3 py-3 text-left card-hover group"
    >
      <span className="grid h-9 w-9 place-items-center rounded-lg bg-ancient-cinnabar/10 text-ancient-cinnabar group-hover:bg-ancient-cinnabar group-hover:text-white transition-colors">
        {icon}
      </span>
      <div className="flex-1 min-w-0">
        <div className="text-sm font-semibold text-text-primary">{label}</div>
        <div className="text-xs text-text-secondary">{desc}</div>
      </div>
      <span className="text-text-hint group-hover:text-ancient-bronze transition-colors">›</span>
    </button>
  );
}
