import { ReactNode, useEffect } from "react";

interface Props {
  open: boolean;
  onClose: () => void;
  title?: string;
  children: ReactNode;
  maxHeight?: string;
}

export default function Sheet({ open, onClose, title, children, maxHeight = "70vh" }: Props) {
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
    <div className="fixed inset-0 z-50">
      <div
        className="absolute inset-0 bg-black/50"
        onClick={onClose}
      />
      <div
        className="absolute bottom-0 left-1/2 -translate-x-1/2 w-full max-w-phone bg-bg-card rounded-t-3xl shadow-card-dark animate-fadeIn flex flex-col"
        style={{ maxHeight, paddingBottom: "max(1rem, env(safe-area-inset-bottom))" }}
      >
        <div className="flex flex-col items-center pt-2">
          <div className="h-1 w-10 rounded-full bg-text-hint/40" />
        </div>
        {title && (
          <div className="px-5 pt-3 pb-2 text-base font-semibold text-text-primary">
            {title}
          </div>
        )}
        <div className="overflow-y-auto px-5 pb-3">{children}</div>
      </div>
    </div>
  );
}
