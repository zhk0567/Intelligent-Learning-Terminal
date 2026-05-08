import { ReactNode, useEffect } from "react";

interface Props {
  open: boolean;
  onClose: () => void;
  title?: string;
  children: ReactNode;
  actions?: ReactNode;
}

export default function Dialog({ open, onClose, title, children, actions }: Props) {
  useEffect(() => {
    if (!open) return;
    const onKey = (e: KeyboardEvent) => e.key === "Escape" && onClose();
    document.addEventListener("keydown", onKey);
    document.body.style.overflow = "hidden";
    return () => {
      document.removeEventListener("keydown", onKey);
      document.body.style.overflow = "";
    };
  }, [open, onClose]);

  if (!open) return null;
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-6">
      <div className="absolute inset-0 bg-black/55" onClick={onClose} />
      <div className="relative w-full max-w-[400px] rounded-3xl bg-bg-card p-5 shadow-card-dark animate-fadeIn">
        {title && (
          <div className="mb-3 text-lg font-bold text-text-primary">{title}</div>
        )}
        <div className="text-sm text-text-secondary leading-6">{children}</div>
        {actions && (
          <div className="mt-5 flex justify-end gap-2">{actions}</div>
        )}
      </div>
    </div>
  );
}
