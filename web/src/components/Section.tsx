import { ReactNode } from "react";
import { SectionTitleBar } from "./Ornament";

interface Props {
  title?: string;
  /** 副标题（多用作英文小字）。 */
  eyebrow?: string;
  action?: ReactNode;
  children: ReactNode;
  framed?: boolean;
  className?: string;
}

/** 章节区块：标题前自动加朱砂红竖条，可选英文小字。 */
export default function Section({
  title,
  eyebrow,
  action,
  children,
  framed = true,
  className = "",
}: Props) {
  return (
    <section
      className={`mt-4 md:mt-5 ${
        framed
          ? "rounded-card border border-border/60 bg-bg-card p-4 md:p-5 transition-colors hover:border-ancient-bronze/55"
          : ""
      } ${className}`}
    >
      {(title || action) && (
        <div className="mb-3 flex items-end justify-between gap-3">
          <div className="flex items-center min-w-0">
            {title && <SectionTitleBar />}
            <div className="min-w-0">
              {title && (
                <h3 className="text-[15px] md:text-base font-bold text-text-primary leading-tight truncate">
                  {title}
                </h3>
              )}
              {eyebrow && (
                <div className="text-[10px] uppercase tracking-[0.18em] text-ancient-bronze/80 mt-0.5">
                  {eyebrow}
                </div>
              )}
            </div>
          </div>
          {action && <div className="shrink-0">{action}</div>}
        </div>
      )}
      {children}
    </section>
  );
}
