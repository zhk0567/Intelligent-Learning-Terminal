import { useState } from "react";
import { useNavigate } from "react-router-dom";
import TopBar from "../components/TopBar";
import { APP_IMAGES } from "../assets/appImages";
import Cover from "../components/Cover";
import { useSessionStore } from "../store/sessionStore";
import { Camera } from "../components/Icon";
import { toast } from "../components/Toast";

export default function EditProfile() {
  const user = useSessionStore((s) => s.user);
  const setUser = useSessionStore((s) => s.setUser);
  const navigate = useNavigate();
  const [name, setName] = useState(user.name);
  const [bio, setBio] = useState(user.bio);
  const [phone, setPhone] = useState(user.phone);

  return (
    <div className="min-h-[100dvh] pb-10 md:min-h-0 md:pb-0 md:max-w-[640px] md:mx-auto">
      <TopBar
        title="编辑资料"
        right={
          <button
            onClick={() => {
              setUser({ name, bio, phone });
              toast("已保存");
              navigate(-1);
            }}
            className="text-sm text-ancient-cinnabar font-semibold px-2"
          >
            保存
          </button>
        }
      />
      <div className="hidden md:flex items-center justify-between pb-3">
        <div className="flex items-center">
          <span className="inline-block w-1 h-6 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
          <h1 className="text-xl font-bold text-text-primary">编辑资料</h1>
        </div>
        <button
          onClick={() => {
            setUser({ name, bio, phone });
            toast("已保存");
            navigate(-1);
          }}
          className="btn-primary rounded-full px-4 py-1.5 text-sm"
        >
          保存
        </button>
      </div>
      <div className="px-4 pt-2 md:px-0 md:pt-0 space-y-4">
        <div className="flex min-w-0 items-start gap-3">
          <div className="relative shrink-0">
            <Cover
              seed={name}
              src={APP_IMAGES.banner1}
              alt={name}
              text={name.slice(0, 1)}
              aspect="aspect-square"
              rounded="rounded-full"
              className="h-20 w-20 shrink-0 ring-2 ring-ancient-bronze/55"
              ornate={false}
            />
            <button className="absolute -right-1 -bottom-1 h-7 w-7 rounded-full bg-ancient-cinnabar text-white grid place-items-center shadow-stampInset">
              <Camera size={14} />
            </button>
          </div>
          <div className="text-xs text-text-hint">点击右下角更换头像</div>
        </div>
        <Field label="昵称" value={name} onChange={setName} />
        <Field label="手机号" value={phone} onChange={(v) => setPhone(v)} />
        <Field label="个人简介" value={bio} onChange={setBio} multiline />
      </div>
    </div>
  );
}

function Field({
  label,
  value,
  onChange,
  multiline,
}: {
  label: string;
  value: string;
  onChange: (v: string) => void;
  multiline?: boolean;
}) {
  return (
    <div>
      <div className="text-xs text-text-secondary mb-1">{label}</div>
      {multiline ? (
        <textarea
          rows={3}
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className="w-full rounded-xl border border-border/50 bg-bg-card px-3 py-2 text-sm text-text-primary outline-none resize-none focus:border-ancient-bronze transition-colors"
        />
      ) : (
        <input
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className="w-full rounded-xl border border-border/50 bg-bg-card px-3 py-2 text-sm text-text-primary outline-none focus:border-ancient-bronze transition-colors"
        />
      )}
    </div>
  );
}
