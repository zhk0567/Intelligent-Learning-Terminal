import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Eye, Lock, Phone, User } from "../components/Icon";
import { useSessionStore } from "../store/sessionStore";
import { toast } from "../components/Toast";
import { SectionTitleBar } from "../components/Ornament";

export default function Login() {
  const [account, setAccount] = useState("");
  const [pwd, setPwd] = useState("");
  const [show, setShow] = useState(false);
  const navigate = useNavigate();
  const loginAs = useSessionStore((s) => s.loginAs);

  const submit = () => {
    if (!account.trim() || !pwd) {
      toast("请输入账号和密码");
      return;
    }
    loginAs("password");
    navigate("/music", { replace: true });
  };

  return (
    <div className="relative min-h-[100dvh] px-6 pb-10 pt-12 bg-bg-primary md:max-w-[480px] md:mx-auto md:pt-[8vh]">
      <div className="relative">
        <div className="flex items-center">
          <SectionTitleBar height={28} />
          <div>
            <div className="text-[10px] uppercase tracking-[0.32em] text-ancient-bronze/85">Welcome Back</div>
            <h1 className="text-3xl font-bold text-text-primary leading-tight">欢迎回来</h1>
          </div>
        </div>
        <div className="mt-1 text-sm text-text-secondary pl-3">登录古韵薪传，继续你的非遗之旅</div>

        <div className="mt-8 rounded-2xl border border-ancient-bronze/40 bg-bg-card p-5 space-y-4 shadow-card">
          <Field icon={<User size={18} />} placeholder="账号 / 手机号 / 邮箱" value={account} onChange={setAccount} />
          <Field
            icon={<Lock size={18} />}
            placeholder="密码"
            type={show ? "text" : "password"}
            value={pwd}
            onChange={setPwd}
            right={
              <button onClick={() => setShow((v) => !v)} className="text-text-hint hover:text-ancient-bronze transition-colors">
                <Eye size={18} />
              </button>
            }
          />
          <div className="flex items-center justify-between text-xs">
            <button
              onClick={() => navigate("/change-password")}
              className="text-ancient-cinnabar font-semibold"
            >
              忘记密码？
            </button>
            <button onClick={() => navigate("/register")} className="text-text-secondary hover:text-ancient-bronze transition-colors">
              新账号注册
            </button>
          </div>
          <button
            onClick={submit}
            className="btn-primary w-full rounded-full py-3 text-sm font-semibold"
          >
            登录
          </button>
          <button
            onClick={() => navigate("/login/phone")}
            className="btn-ghost w-full rounded-full py-3 text-sm gap-2"
          >
            <Phone size={18} /> 手机验证码登录
          </button>
        </div>

        <div className="mt-8 flex items-center justify-center gap-3 text-xs text-text-hint">
          <button
            onClick={() => {
              loginAs("guest");
              navigate("/music", { replace: true });
            }}
          >
            游客访问
          </button>
          <span>·</span>
          <button onClick={() => navigate("/about")}>关于古韵薪传</button>
        </div>
      </div>
    </div>
  );
}

function Field(props: {
  icon: React.ReactNode;
  placeholder: string;
  value: string;
  onChange: (v: string) => void;
  type?: string;
  right?: React.ReactNode;
}) {
  return (
    <div className="flex items-center gap-2 border-b border-border/50 py-2 focus-within:border-ancient-bronze transition-colors">
      <span className="text-text-hint">{props.icon}</span>
      <input
        className="flex-1 outline-none text-sm text-text-primary placeholder:text-text-hint"
        type={props.type ?? "text"}
        placeholder={props.placeholder}
        value={props.value}
        onChange={(e) => props.onChange(e.target.value)}
      />
      {props.right}
    </div>
  );
}
