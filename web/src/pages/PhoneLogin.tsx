import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import TopBar from "../components/TopBar";
import { Phone } from "../components/Icon";
import { toast } from "../components/Toast";
import { useSessionStore } from "../store/sessionStore";

export default function PhoneLogin() {
  const [phone, setPhone] = useState("");
  const [code, setCode] = useState("");
  const [seconds, setSeconds] = useState(0);
  const navigate = useNavigate();
  const loginAs = useSessionStore((s) => s.loginAs);

  useEffect(() => {
    if (seconds <= 0) return;
    const id = setInterval(() => setSeconds((v) => v - 1), 1000);
    return () => clearInterval(id);
  }, [seconds]);

  const sendCode = () => {
    if (!/^1\d{10}$/.test(phone)) {
      toast("请输入 11 位有效手机号");
      return;
    }
    setSeconds(60);
    toast("验证码已发送（123456）");
  };
  const submit = () => {
    if (code !== "123456") {
      toast("验证码错误，演示请输入 123456");
      return;
    }
    loginAs("phone");
    navigate("/music", { replace: true });
  };

  return (
    <div className="min-h-[100dvh] md:max-w-[480px] md:mx-auto md:pt-[6vh]">
      <TopBar title="手机验证码登录" />
      <div className="hidden md:flex items-center px-6 pt-2 pb-1">
        <span className="inline-block w-1 h-7 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
        <div>
          <div className="text-[10px] uppercase tracking-[0.28em] text-ancient-bronze/85">Phone Login</div>
          <h1 className="text-2xl font-bold text-text-primary leading-tight">手机验证码登录</h1>
        </div>
      </div>
      <p className="hidden md:block text-sm text-text-secondary px-6 pb-3">输入手机号获取验证码即可登录</p>
      <div className="px-6 pt-4 md:pt-2">
        <div className="rounded-2xl border border-ancient-bronze/40 bg-bg-card p-5 space-y-4 shadow-card">
          <div className="flex items-center gap-2 border-b border-border/50 py-2 focus-within:border-ancient-bronze transition-colors">
            <Phone size={18} className="text-text-hint" />
            <input
              className="flex-1 outline-none text-sm text-text-primary placeholder:text-text-hint"
              placeholder="11 位手机号"
              value={phone}
              onChange={(e) => setPhone(e.target.value.replace(/\D/g, "").slice(0, 11))}
              inputMode="tel"
            />
          </div>
          <div className="flex items-center gap-2 border-b border-border/50 py-2 focus-within:border-ancient-bronze transition-colors">
            <input
              className="flex-1 outline-none text-sm text-text-primary placeholder:text-text-hint"
              placeholder="验证码（演示请填 123456）"
              value={code}
              onChange={(e) => setCode(e.target.value.replace(/\D/g, "").slice(0, 6))}
              inputMode="numeric"
            />
            <button
              onClick={sendCode}
              disabled={seconds > 0}
              className={`text-sm whitespace-nowrap font-semibold ${
                seconds > 0 ? "text-text-hint" : "text-ancient-cinnabar"
              }`}
            >
              {seconds > 0 ? `${seconds}s 后重发` : "获取验证码"}
            </button>
          </div>
          <button
            onClick={submit}
            className="btn-primary w-full rounded-full py-3 text-sm font-semibold"
          >
            登录
          </button>
          <div className="text-center text-xs text-text-hint">
            登录即代表同意 <button className="text-ancient-bronze font-semibold" onClick={() => navigate("/webview?title=用户协议")}>用户协议</button> 与 <button className="text-ancient-bronze font-semibold" onClick={() => navigate("/webview?title=隐私政策")}>隐私政策</button>
          </div>
        </div>
      </div>
    </div>
  );
}
