import { CSSProperties } from "react";

/** 章节标题前的朱砂红竖条。 */
export function SectionTitleBar({
  className = "",
  height = 16,
}: {
  className?: string;
  height?: number;
}) {
  return (
    <span
      className={`inline-block w-1 rounded-sm bg-ancient-cinnabar mr-2 align-middle ${className}`}
      style={{ height }}
      aria-hidden
    />
  );
}

/** 中央菱形珠 + 两侧虚线分隔。 */
export function OrnateDivider({ className = "" }: { className?: string }) {
  return (
    <div className={`flex items-center gap-2 my-2 text-ancient-bronze/60 ${className}`} aria-hidden>
      <span className="flex-1 h-px border-t border-dashed border-current" />
      <span className="h-2 w-2 rotate-45 bg-current" />
      <span className="flex-1 h-px border-t border-dashed border-current" />
    </div>
  );
}

/** 回纹横条 SVG，作为细装饰线。 */
export function PatternStrip({
  className = "",
  opacity = 0.28,
  height = 8,
  style,
}: {
  className?: string;
  opacity?: number;
  height?: number;
  style?: CSSProperties;
}) {
  return (
    <svg
      width="100%"
      height={height}
      viewBox="0 0 80 8"
      preserveAspectRatio="repeat"
      className={`block text-ancient-bronze ${className}`}
      style={{ opacity, ...style }}
      aria-hidden
    >
      <defs>
        <pattern id="hpattern" x="0" y="0" width="16" height="8" patternUnits="userSpaceOnUse">
          <path
            d="M0 4 H4 V1 H12 V7 H16"
            stroke="currentColor"
            strokeWidth="0.8"
            fill="none"
          />
        </pattern>
      </defs>
      <rect width="80" height="8" fill="url(#hpattern)" />
    </svg>
  );
}

/** 用作卡片右下角的回纹小章贴。 */
export function CornerPattern({
  size = 30,
  opacity = 0.3,
  className = "",
}: {
  size?: number;
  opacity?: number;
  className?: string;
}) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 30 30"
      className={`pointer-events-none ${className}`}
      style={{ opacity }}
      aria-hidden
    >
      <path
        d="M3 27 V19 H11 V11 H19 V3 H27"
        stroke="currentColor"
        strokeWidth="1.4"
        fill="none"
        strokeLinecap="square"
      />
      <path
        d="M7 27 V23 H15 V15 H23 V7 H27"
        stroke="currentColor"
        strokeWidth="1.4"
        fill="none"
        strokeLinecap="square"
      />
    </svg>
  );
}
