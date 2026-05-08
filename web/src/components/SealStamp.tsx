import { CSSProperties } from "react";

interface Props {
  text: string;
  size?: number;
  tone?: "solid" | "outline";
  className?: string;
  style?: CSSProperties;
  animate?: boolean;
}

/**
 * 朱砂方章。tone="solid" 实底白字（强 CTA、徽章），tone="outline" 仅红框（用于 logo 旁装饰）。
 */
export default function SealStamp({
  text,
  size = 28,
  tone = "solid",
  className = "",
  style,
  animate = false,
}: Props) {
  const fontSize = Math.max(10, size * 0.5);
  return (
    <span
      className={`relative inline-grid place-items-center align-middle select-none ${
        animate ? "animate-stampPress" : ""
      } ${className}`}
      style={{ width: size, height: size, ...style }}
      aria-hidden
    >
      {tone === "solid" ? (
        <span className="absolute inset-0 rounded-seal bg-ancient-cinnabar shadow-stampInset" />
      ) : (
        <span className="absolute inset-0 rounded-seal border-[1.5px] border-ancient-cinnabar" />
      )}
      <span
        className={`relative font-bold tracking-tight leading-none ${
          tone === "solid" ? "text-white" : "text-ancient-cinnabar"
        }`}
        style={{ fontSize }}
      >
        {text}
      </span>
    </span>
  );
}
