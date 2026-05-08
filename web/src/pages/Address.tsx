import { useState } from "react";
import TopBar from "../components/TopBar";
import { ADDRESSES, Address as Addr } from "../data/address";
import { Edit, MapPin, Plus, Trash } from "../components/Icon";
import Sheet from "../components/Sheet";
import { toast } from "../components/Toast";

export default function Address() {
  const [list, setList] = useState<Addr[]>(ADDRESSES);
  const [editing, setEditing] = useState<Addr | null>(null);
  const [open, setOpen] = useState(false);

  const startCreate = () => {
    setEditing({ id: `a${Date.now()}`, name: "", phone: "", detail: "", isDefault: false });
    setOpen(true);
  };

  const save = () => {
    if (!editing) return;
    if (!editing.name || !editing.phone || !editing.detail) return toast("请填写完整");
    const exists = list.find((a) => a.id === editing.id);
    let next = exists ? list.map((a) => (a.id === editing.id ? editing : a)) : [...list, editing];
    if (editing.isDefault) {
      next = next.map((a) => ({ ...a, isDefault: a.id === editing.id }));
    }
    setList(next);
    setOpen(false);
    setEditing(null);
    toast(exists ? "已更新" : "已添加");
  };

  return (
    <div className="min-h-[100dvh] pb-24 md:min-h-0 md:pb-0 md:max-w-[720px] md:mx-auto">
      <TopBar
        title="收货地址"
        right={
          <button
            onClick={startCreate}
            className="flex items-center gap-1 text-sm text-ancient-cinnabar font-semibold px-2"
          >
            <Plus size={16} /> 新增
          </button>
        }
      />
      <div className="hidden md:flex items-center justify-between pb-3">
        <div className="flex items-center">
          <span className="inline-block w-1 h-6 rounded-sm bg-ancient-cinnabar mr-2" aria-hidden />
          <h1 className="text-xl font-bold text-text-primary">收货地址</h1>
        </div>
        <button
          onClick={startCreate}
          className="btn-ghost rounded-full px-3 py-1.5 text-sm gap-1"
        >
          <Plus size={14} /> 新增地址
        </button>
      </div>
      <div className="px-3 pt-2 md:px-0 md:pt-0 space-y-2 md:grid md:grid-cols-2 md:gap-3 md:space-y-0">
        {list.map((a) => (
          <div key={a.id} className="rounded-2xl bg-bg-card border border-border/50 p-3 flex items-start gap-3 card-hover">
            <MapPin size={20} className="text-ancient-cinnabar mt-1" />
            <div className="flex-1 min-w-0">
              <div className="flex items-center gap-2">
                <span className="text-sm font-semibold text-text-primary">{a.name}</span>
                <span className="text-xs text-text-secondary">{a.phone}</span>
                {a.isDefault && (
                  <span className="rounded border border-ancient-cinnabar/40 bg-ancient-cinnabar/10 text-ancient-cinnabar text-[10px] px-1.5 py-0.5">
                    默认
                  </span>
                )}
              </div>
              <div className="text-xs text-text-secondary mt-1">{a.detail}</div>
              <div className="mt-2 flex gap-2 text-[11px]">
                <button
                  onClick={() => {
                    setEditing(a);
                    setOpen(true);
                  }}
                  className="flex items-center gap-1 text-text-hint hover:text-ancient-bronze transition-colors"
                >
                  <Edit size={12} /> 编辑
                </button>
                <button
                  onClick={() => setList(list.filter((x) => x.id !== a.id))}
                  className="flex items-center gap-1 text-text-hint hover:text-ancient-cinnabar transition-colors"
                >
                  <Trash size={12} /> 删除
                </button>
                {!a.isDefault && (
                  <button
                    onClick={() => setList(list.map((x) => ({ ...x, isDefault: x.id === a.id })))}
                    className="text-ancient-cinnabar font-semibold"
                  >
                    设为默认
                  </button>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>

      <Sheet open={open} onClose={() => setOpen(false)} title={editing && list.find((a) => a.id === editing.id) ? "编辑地址" : "新增地址"}>
        {editing && (
          <div className="space-y-3">
            <Field label="收货人" value={editing.name} onChange={(v) => setEditing({ ...editing, name: v })} />
            <Field
              label="手机号"
              value={editing.phone}
              onChange={(v) => setEditing({ ...editing, phone: v.replace(/[^\d\s]/g, "") })}
            />
            <Field
              label="详细地址"
              value={editing.detail}
              onChange={(v) => setEditing({ ...editing, detail: v })}
              multiline
            />
            <label className="flex items-center gap-2 text-sm text-text-primary">
              <input
                type="checkbox"
                checked={editing.isDefault}
                onChange={(e) => setEditing({ ...editing, isDefault: e.target.checked })}
              />
              设为默认地址
            </label>
            <button
              onClick={save}
              className="btn-primary w-full rounded-full py-3 text-sm font-semibold"
            >
              保存
            </button>
          </div>
        )}
      </Sheet>
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
          className="w-full rounded-xl border border-border/50 bg-bg-cardElevated/40 px-3 py-2 text-sm text-text-primary outline-none resize-none"
        />
      ) : (
        <input
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className="w-full rounded-xl border border-border/50 bg-bg-cardElevated/40 px-3 py-2 text-sm text-text-primary outline-none"
        />
      )}
    </div>
  );
}
