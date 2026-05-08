import { useState } from "react";
import { useNavigate } from "react-router-dom";
import TopBar from "../components/TopBar";
import { Eye, Lock } from "../components/Icon";
import { toast } from "../components/Toast";

export default function ChangePassword() {
  const [oldP, setOldP] = useState("");
  const [newP, setNewP] = useState("");
  const [confirm, setConfirm] = useState("");
  const [show, setShow] = useState(false);
  const navigate = useNavigate();

  const submit = () => {
    if (!oldP || !newP) return toast("请填写完整");
    if (newP.length < 8) return toast("新密码至少 8 位");
    if (newP !== confirm) return toast("两次密码不一致");
    if (newP === oldP) return toast("新密码不能与旧密码相同");
    toast("密码已更新");
    navigate(-1);
  };

  return (
    <div className="min-h-[100dvh] md:max-w-[480px] md:mx-auto md:pt-[6vh]">
      <TopBar title="修改密码" />
      <div className="hidden md:flex items-center px-6 pt-2 pb-3">
        <span className="inline-block w-1 h-7 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
        <div>
          <div className="text-[10px] uppercase tracking-[0.28em] text-ancient-bronze/85">Update Password</div>
          <h1 className="text-2xl font-bold text-text-primary leading-tight">修改密码</h1>
        </div>
      </div>
      <div className="px-6 pt-4 md:pt-2">
        <div className="rounded-2xl border border-ancient-bronze/40 bg-bg-card p-5 space-y-4 shadow-card">
          <Row
            label="旧密码"
            value={oldP}
            onChange={setOldP}
            type={show ? "text" : "password"}
            right={
              <button onClick={() => setShow((v) => !v)} className="text-text-hint">
                <Eye size={18} />
              </button>
            }
          />
          <Row label="新密码" value={newP} onChange={setNewP} type={show ? "text" : "password"} />
          <Row
            label="确认新密码"
            value={confirm}
            onChange={setConfirm}
            type={show ? "text" : "password"}
          />
          <button
            onClick={submit}
            className="btn-primary w-full rounded-full py-3 text-sm font-semibold"
          >
            提交
          </button>
        </div>
        <div className="mt-3 text-xs text-text-hint">
          安全提示：建议使用 8 位以上、包含大小写字母与数字的密码。
        </div>
      </div>
    </div>
  );
}

function Row(p: {
  label: string;
  value: string;
  onChange: (v: string) => void;
  type?: string;
  right?: React.ReactNode;
}) {
  return (
    <div className="flex items-center gap-2 border-b border-border/50 py-2 focus-within:border-ancient-bronze transition-colors">
      <Lock size={18} className="text-text-hint" />
      <input
        className="flex-1 outline-none text-sm text-text-primary placeholder:text-text-hint"
        placeholder={p.label}
        type={p.type ?? "text"}
        value={p.value}
        onChange={(e) => p.onChange(e.target.value)}
      />
      {p.right}
    </div>
  );
}
