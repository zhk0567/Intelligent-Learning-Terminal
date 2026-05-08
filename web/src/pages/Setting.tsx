import { useState } from "react";
import { useNavigate } from "react-router-dom";
import TopBar from "../components/TopBar";
import Dialog from "../components/Dialog";
import { useThemeStore, ThemeMode } from "../store/themeStore";
import { useSessionStore } from "../store/sessionStore";
import {
  Bell,
  Globe,
  Lock,
  Settings as SettingsIcon,
  User,
} from "../components/Icon";
import { toast } from "../components/Toast";

const THEME_OPTIONS: { id: ThemeMode; label: string; desc: string }[] = [
  { id: "dark", label: "深色", desc: "默认主题，护眼且更具沉浸感" },
  { id: "light", label: "浅色", desc: "明亮风格，适合白天与户外" },
];

export default function Setting() {
  const navigate = useNavigate();
  const { mode, setMode } = useThemeStore();
  const logout = useSessionStore((s) => s.logout);
  const [themeOpen, setThemeOpen] = useState(false);
  const [logoutOpen, setLogoutOpen] = useState(false);
  const [tempMode, setTempMode] = useState<ThemeMode>(mode);

  const themeLabel = THEME_OPTIONS.find((t) => t.id === mode)?.label ?? "深色";

  return (
    <div className="min-h-[100dvh] md:min-h-0 md:max-w-[720px] md:mx-auto">
      <TopBar title="设置" />
      <div className="hidden md:flex items-center pb-3 px-1">
        <span className="inline-block w-1 h-6 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
        <h1 className="text-xl font-bold text-text-primary">设置</h1>
      </div>
      <div className="px-3 pt-2 pb-10 md:px-0 md:pt-0 md:pb-0 space-y-3">
        <Group title="账号" eyebrow="Account">
          <Row label="编辑个人信息" icon={<User size={16} />} onClick={() => navigate("/me/edit")} />
          <Row label="修改密码" icon={<Lock size={16} />} onClick={() => navigate("/change-password")} />
        </Group>

        <Group title="外观" eyebrow="Appearance">
          <Row
            label="主题模式"
            icon={<SettingsIcon size={16} />}
            onClick={() => {
              setTempMode(mode);
              setThemeOpen(true);
            }}
            value={themeLabel}
          />
        </Group>

        <Group title="通用" eyebrow="General">
          <Row label="通知设置" icon={<Bell size={16} />} onClick={() => toast("演示版本暂未开放")} />
          <Row label="语言" icon={<Globe size={16} />} onClick={() => toast("仅支持简体中文")} value="简体中文" />
          <Row
            label="用户协议"
            onClick={() => navigate("/webview?title=用户协议")}
          />
          <Row
            label="隐私政策"
            onClick={() => navigate("/webview?title=隐私政策")}
          />
          <Row label="关于古韵薪传" onClick={() => navigate("/about")} value="v0.1.0" />
        </Group>

        <button
          onClick={() => setLogoutOpen(true)}
          className="w-full rounded-card border border-border/60 bg-bg-card py-3 text-sm text-ancient-cinnabar font-semibold hover:bg-ancient-cinnabar/5 transition-colors"
        >
          退出登录
        </button>
      </div>

      <Dialog
        open={themeOpen}
        onClose={() => setThemeOpen(false)}
        title="主题模式"
        actions={
          <>
            <button onClick={() => setThemeOpen(false)} className="rounded-full px-4 py-2 text-sm text-text-secondary">
              取消
            </button>
            <button
              onClick={() => {
                if (tempMode !== mode) {
                  setMode(tempMode);
                  toast(`已切换到「${tempMode === "dark" ? "深色" : "浅色"}」`);
                }
                setThemeOpen(false);
              }}
              className="btn-primary rounded-full px-4 py-2 text-sm"
            >
              确认
            </button>
          </>
        }
      >
        <div className="space-y-2">
          {THEME_OPTIONS.map((o) => (
            <button
              key={o.id}
              onClick={() => setTempMode(o.id)}
              className={`w-full flex items-center gap-3 rounded-xl border p-3 text-left transition-colors ${
                tempMode === o.id
                  ? "bg-ancient-cinnabar/8 border-ancient-cinnabar/60"
                  : "border-border/50 bg-bg-cardElevated/40 hover:border-ancient-bronze/60"
              }`}
            >
              <span
                className={`h-5 w-5 rounded-full border-2 transition-colors ${
                  tempMode === o.id ? "bg-ancient-cinnabar border-ancient-cinnabar" : "border-border"
                }`}
              />
              <div className="flex-1 flex items-center gap-3">
                <span className="text-2xl">{o.id === "dark" ? "☾" : "☀"}</span>
                <div>
                  <div className="text-sm font-semibold text-text-primary">{o.label}</div>
                  <div className="text-xs text-text-secondary">{o.desc}</div>
                </div>
              </div>
            </button>
          ))}
        </div>
      </Dialog>

      <Dialog
        open={logoutOpen}
        onClose={() => setLogoutOpen(false)}
        title="确认退出？"
        actions={
          <>
            <button onClick={() => setLogoutOpen(false)} className="rounded-full px-4 py-2 text-sm text-text-secondary">
              取消
            </button>
            <button
              onClick={() => {
                logout();
                setLogoutOpen(false);
                navigate("/login", { replace: true });
              }}
              className="btn-stamp rounded-full px-4 py-2 text-sm"
            >
              退出
            </button>
          </>
        }
      >
        退出后将清除当前会话，下次需重新登录。
      </Dialog>
    </div>
  );
}

function Group({
  title,
  eyebrow,
  children,
}: {
  title: string;
  eyebrow?: string;
  children: React.ReactNode;
}) {
  return (
    <div className="rounded-card border border-border/60 bg-bg-card p-3">
      <div className="px-1 pb-2 flex items-center">
        <span className="inline-block w-1 h-4 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
        <div>
          <div className="text-sm font-bold text-text-primary leading-tight">{title}</div>
          {eyebrow && (
            <div className="text-[10px] uppercase tracking-[0.18em] text-ancient-bronze/85">
              {eyebrow}
            </div>
          )}
        </div>
      </div>
      {children}
    </div>
  );
}

function Row({
  label,
  value,
  icon,
  onClick,
}: {
  label: string;
  value?: string;
  icon?: React.ReactNode;
  onClick?: () => void;
}) {
  return (
    <button
      onClick={onClick}
      className="w-full flex items-center gap-3 rounded-xl border border-border/40 bg-bg-cardElevated/40 px-3 py-3 mt-2 first:mt-0 text-left card-hover"
    >
      {icon && (
        <span className="h-7 w-7 grid place-items-center rounded-lg bg-ancient-cinnabar/10 text-ancient-cinnabar">
          {icon}
        </span>
      )}
      <span className="flex-1 text-sm text-text-primary">{label}</span>
      {value && <span className="text-xs text-text-hint">{value}</span>}
      <span className="text-text-hint">›</span>
    </button>
  );
}
