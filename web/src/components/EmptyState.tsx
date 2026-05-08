import { ReactNode } from "react";

interface Props {
  text: string;
  hint?: string;
  action?: ReactNode;
  className?: string;
  icon?: "fan" | "kite" | "scroll";
}

/** 统一空状态：非遗线稿小图 + 文案 + 可选操作。 */
export default function EmptyState({
  text,
  hint,
  action,
  className = "",
  icon = "fan",
}: Props) {
  return (
    <div
      className={`flex flex-col items-center justify-center py-12 text-center ${className}`}
    >
      <div className="text-ancient-bronze/55 mb-3">
        {icon === "fan" && <FanSvg />}
        {icon === "kite" && <KiteSvg />}
        {icon === "scroll" && <ScrollSvg />}
      </div>
      <div className="text-sm text-text-secondary">{text}</div>
      {hint && <div className="mt-1 text-xs text-text-hint">{hint}</div>}
      {action && <div className="mt-4">{action}</div>}
    </div>
  );
}

function FanSvg() {
  return (
    <svg width="76" height="76" viewBox="0 0 76 76" fill="none" aria-hidden>
      <path
        d="M38 64 L14 28 A28 28 0 0 1 62 28 Z"
        stroke="currentColor"
        strokeWidth="1.4"
        strokeLinejoin="round"
      />
      <path d="M38 64 L26 32" stroke="currentColor" strokeWidth="1.2" />
      <path d="M38 64 L38 28" stroke="currentColor" strokeWidth="1.2" />
      <path d="M38 64 L50 32" stroke="currentColor" strokeWidth="1.2" />
      <circle cx="38" cy="64" r="2" fill="currentColor" />
    </svg>
  );
}

function KiteSvg() {
  return (
    <svg width="76" height="76" viewBox="0 0 76 76" fill="none" aria-hidden>
      <path
        d="M38 8 L60 32 L38 56 L16 32 Z"
        stroke="currentColor"
        strokeWidth="1.4"
        strokeLinejoin="round"
      />
      <path d="M38 8 L38 56" stroke="currentColor" strokeWidth="1.2" />
      <path d="M16 32 L60 32" stroke="currentColor" strokeWidth="1.2" />
      <path
        d="M38 56 C 36 60, 40 62, 38 66 C 36 70, 40 72, 38 74"
        stroke="currentColor"
        strokeWidth="1.2"
        fill="none"
      />
    </svg>
  );
}

function ScrollSvg() {
  return (
    <svg width="76" height="76" viewBox="0 0 76 76" fill="none" aria-hidden>
      <path
        d="M14 18 H62 V58 H14 Z"
        stroke="currentColor"
        strokeWidth="1.4"
        strokeLinejoin="round"
      />
      <path d="M22 28 H54" stroke="currentColor" strokeWidth="1.2" />
      <path d="M22 38 H54" stroke="currentColor" strokeWidth="1.2" />
      <path d="M22 48 H44" stroke="currentColor" strokeWidth="1.2" />
      <circle cx="14" cy="18" r="3" stroke="currentColor" strokeWidth="1.2" fill="none" />
      <circle cx="62" cy="58" r="3" stroke="currentColor" strokeWidth="1.2" fill="none" />
    </svg>
  );
}
