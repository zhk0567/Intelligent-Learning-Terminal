import { ReactNode } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft } from "./Icon";

interface Props {
  title?: string;
  subtitle?: string;
  back?: boolean;
  onBack?: () => void;
  right?: ReactNode;
  transparent?: boolean;
}

export default function TopBar({
  title,
  subtitle,
  back = true,
  onBack,
  right,
  transparent,
}: Props) {
  const navigate = useNavigate();
  const goBack = onBack ?? (() => navigate(-1));

  return (
    <div
      className={`sticky top-0 z-30 flex items-center gap-2 px-3 py-2 backdrop-blur-md md:hidden ${
        transparent
          ? "bg-transparent"
          : "bg-bg-primary/85 border-b border-border/40"
      }`}
      style={{ paddingTop: "max(0.5rem, env(safe-area-inset-top))" }}
    >
      {back && (
        <button
          aria-label="返回"
          className="flex h-9 w-9 items-center justify-center rounded-full text-text-primary active:bg-bg-card/40 hover:bg-bg-cardElevated/60"
          onClick={goBack}
        >
          <ArrowLeft size={20} />
        </button>
      )}
      <div className="flex-1 min-w-0">
        {title && (
          <div className="truncate text-base font-semibold leading-tight text-text-primary">
            {title}
          </div>
        )}
        {subtitle && (
          <div className="truncate text-[11px] text-text-secondary">{subtitle}</div>
        )}
      </div>
      {right && <div className="flex items-center gap-1">{right}</div>}
    </div>
  );
}
