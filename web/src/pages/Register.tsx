import { useState } from "react";
import { useNavigate } from "react-router-dom";
import TopBar from "../components/TopBar";
import { Eye, Lock, Phone, User } from "../components/Icon";
import { toast } from "../components/Toast";
import { useSessionStore } from "../store/sessionStore";

export default function Register() {
  const [name, setName] = useState("");
  const [phone, setPhone] = useState("");
  const [pwd, setPwd] = useState("");
  const [confirm, setConfirm] = useState("");
  const [show, setShow] = useState(false);
  const navigate = useNavigate();
  const loginAs = useSessionStore((s) => s.loginAs);
  const setUser = useSessionStore((s) => s.setUser);

  const strength = scorePwd(pwd);

  const submit = () => {
    if (!name || !phone || !pwd) return toast("请完整填写信息");
    if (pwd !== confirm) return toast("两次密码不一致");
    if (strength < 2) return toast("密码强度过低，请使用大小写+数字");
    setUser({ name, phone });
    loginAs("password");
    navigate("/music", { replace: true });
  };

  return (
    <div className="min-h-[100dvh] md:max-w-[480px] md:mx-auto md:pt-[6vh]">
      <TopBar title="注册账号" />
      <div className="hidden md:flex items-center px-6 pt-2 pb-1">
        <span className="inline-block w-1 h-7 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
        <div>
          <div className="text-[10px] uppercase tracking-[0.28em] text-ancient-bronze/85">New Member</div>
          <h1 className="text-2xl font-bold text-text-primary leading-tight">注册账号</h1>
        </div>
      </div>
      <p className="hidden md:block text-sm text-text-secondary px-6 pb-3">让非遗的故事从你这里继续流动</p>
      <div className="px-6 pt-4 pb-10 md:pt-2">
        <div className="rounded-2xl border border-ancient-bronze/40 bg-bg-card p-5 space-y-4 shadow-card">
          <Row icon={<User size={18} />} placeholder="昵称" value={name} onChange={setName} />
          <Row
            icon={<Phone size={18} />}
            placeholder="手机号"
            value={phone}
            onChange={(v) => setPhone(v.replace(/\D/g, "").slice(0, 11))}
          />
          <Row
            icon={<Lock size={18} />}
            placeholder="设置密码"
            value={pwd}
            onChange={setPwd}
            type={show ? "text" : "password"}
            right={
              <button onClick={() => setShow((v) => !v)} className="text-text-hint">
                <Eye size={18} />
              </button>
            }
          />
          <div className="flex gap-1 px-1">
            {[0, 1, 2, 3].map((i) => (
              <div
                key={i}
                className={`h-1 flex-1 rounded-full ${
                  strength > i
                    ? strength >= 3
                      ? "bg-emerald-400"
                      : strength === 2
                      ? "bg-amber-400"
                      : "bg-rose-400"
                    : "bg-border/40"
                }`}
              />
            ))}
          </div>
          <Row
            icon={<Lock size={18} />}
            placeholder="再次输入密码"
            value={confirm}
            onChange={setConfirm}
            type={show ? "text" : "password"}
          />
          <button
            onClick={submit}
            className="btn-primary w-full rounded-full py-3 text-sm font-semibold"
          >
            注册并登录
          </button>
        </div>
      </div>
    </div>
  );
}

function Row(p: {
  icon: React.ReactNode;
  placeholder: string;
  value: string;
  onChange: (v: string) => void;
  type?: string;
  right?: React.ReactNode;
}) {
  return (
    <div className="flex items-center gap-2 border-b border-border/50 py-2 focus-within:border-ancient-bronze transition-colors">
      <span className="text-text-hint">{p.icon}</span>
      <input
        className="flex-1 outline-none text-sm text-text-primary placeholder:text-text-hint"
        placeholder={p.placeholder}
        type={p.type ?? "text"}
        value={p.value}
        onChange={(e) => p.onChange(e.target.value)}
      />
      {p.right}
    </div>
  );
}

function scorePwd(p: string) {
  let s = 0;
  if (p.length >= 8) s++;
  if (/[a-z]/.test(p) && /[A-Z]/.test(p)) s++;
  if (/\d/.test(p)) s++;
  if (/[^a-zA-Z0-9]/.test(p)) s++;
  return s;
}
