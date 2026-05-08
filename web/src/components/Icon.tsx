/** 简易内联 SVG 图标库，避免引入额外依赖。 */
import { SVGProps } from "react";

type IconProps = SVGProps<SVGSVGElement> & { size?: number };

function base({ size = 24, strokeWidth = 1.8, ...rest }: IconProps) {
  return {
    width: size,
    height: size,
    viewBox: "0 0 24 24",
    fill: "none",
    stroke: "currentColor",
    strokeWidth,
    strokeLinecap: "round" as const,
    strokeLinejoin: "round" as const,
    ...rest,
  };
}

export const ArrowLeft = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M15 6l-6 6 6 6" />
  </svg>
);
export const ArrowRight = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M9 6l6 6-6 6" />
  </svg>
);
export const Search = (p: IconProps) => (
  <svg {...base(p)}>
    <circle cx="11" cy="11" r="7" />
    <path d="M20 20l-3.5-3.5" />
  </svg>
);
export const Play = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M7 5l12 7-12 7V5z" fill="currentColor" />
  </svg>
);
export const Pause = (p: IconProps) => (
  <svg {...base(p)}>
    <rect x="6" y="5" width="4" height="14" rx="1" fill="currentColor" />
    <rect x="14" y="5" width="4" height="14" rx="1" fill="currentColor" />
  </svg>
);
export const SkipBack = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M6 5v14" />
    <path d="M19 5L9 12l10 7V5z" fill="currentColor" />
  </svg>
);
export const SkipForward = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M18 5v14" />
    <path d="M5 5l10 7-10 7V5z" fill="currentColor" />
  </svg>
);
export const ListIcon = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M4 6h16M4 12h16M4 18h10" />
  </svg>
);
export const Heart = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M20.84 4.61a5.5 5.5 0 00-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 00-7.78 7.78L12 21l8.84-8.84a5.5 5.5 0 000-7.55z" />
  </svg>
);
export const HeartFilled = (p: IconProps) => (
  <svg {...base(p)}>
    <path
      d="M20.84 4.61a5.5 5.5 0 00-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 00-7.78 7.78L12 21l8.84-8.84a5.5 5.5 0 000-7.55z"
      fill="currentColor"
    />
  </svg>
);
export const Cart = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M3 4h2l2 12h12l2-8H6" />
    <circle cx="9" cy="20" r="1.5" />
    <circle cx="18" cy="20" r="1.5" />
  </svg>
);
export const User = (p: IconProps) => (
  <svg {...base(p)}>
    <circle cx="12" cy="8" r="4" />
    <path d="M4 21c0-4 4-7 8-7s8 3 8 7" />
  </svg>
);
export const Music = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M9 18V5l12-2v13" />
    <circle cx="6" cy="18" r="3" />
    <circle cx="18" cy="16" r="3" />
  </svg>
);
export const BookOpen = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M2 5a3 3 0 013-3h4v18H5a3 3 0 01-3-3V5z" />
    <path d="M22 5a3 3 0 00-3-3h-4v18h4a3 3 0 003-3V5z" />
  </svg>
);
export const Edit = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M12 20h9" />
    <path d="M16.5 3.5a2.121 2.121 0 113 3L7 19l-4 1 1-4 12.5-12.5z" />
  </svg>
);
export const Shop = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M3 9l1-5h16l1 5" />
    <path d="M3 9v11h18V9" />
    <path d="M9 13h6" />
  </svg>
);
export const Plus = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M12 5v14M5 12h14" />
  </svg>
);
export const Minus = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M5 12h14" />
  </svg>
);
export const Close = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M6 6l12 12M18 6L6 18" />
  </svg>
);
export const Check = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M5 12l5 5L20 7" />
  </svg>
);
export const Settings = (p: IconProps) => (
  <svg {...base(p)}>
    <circle cx="12" cy="12" r="3" />
    <path d="M19 12a7 7 0 00-.18-1.6l2.05-1.6-2-3.46-2.4.96a7 7 0 00-2.78-1.6L13.4 2h-2.8l-.29 2.7a7 7 0 00-2.78 1.6l-2.4-.96-2 3.46 2.05 1.6A7 7 0 005 12a7 7 0 00.18 1.6l-2.05 1.6 2 3.46 2.4-.96a7 7 0 002.78 1.6l.29 2.7h2.8l.29-2.7a7 7 0 002.78-1.6l2.4.96 2-3.46-2.05-1.6c.13-.51.18-1.05.18-1.6z" />
  </svg>
);
export const Bell = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M6 8a6 6 0 0112 0c0 7 3 7 3 9H3c0-2 3-2 3-9z" />
    <path d="M10 21a2 2 0 004 0" />
  </svg>
);
export const Clock = (p: IconProps) => (
  <svg {...base(p)}>
    <circle cx="12" cy="12" r="9" />
    <path d="M12 7v5l3 2" />
  </svg>
);
export const Star = (p: IconProps) => (
  <svg {...base(p)}>
    <path
      d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"
      fill="currentColor"
    />
  </svg>
);
export const Trophy = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M6 4h12v4a4 4 0 01-4 4h-4a4 4 0 01-4-4V4z" />
    <path d="M4 6h2M18 6h2M9 18h6M12 12v6" />
  </svg>
);
export const MapPin = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M12 22s7-7 7-12a7 7 0 10-14 0c0 5 7 12 7 12z" />
    <circle cx="12" cy="10" r="2.5" />
  </svg>
);
export const Phone = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M5 4h4l2 5-2 1a11 11 0 005 5l1-2 5 2v4a2 2 0 01-2 2A18 18 0 013 6a2 2 0 012-2z" />
  </svg>
);
export const Lock = (p: IconProps) => (
  <svg {...base(p)}>
    <rect x="4" y="11" width="16" height="10" rx="2" />
    <path d="M8 11V8a4 4 0 018 0v3" />
  </svg>
);
export const Eye = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z" />
    <circle cx="12" cy="12" r="3" />
  </svg>
);
export const Download = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M12 4v12M6 12l6 6 6-6" />
    <path d="M5 20h14" />
  </svg>
);
export const Share = (p: IconProps) => (
  <svg {...base(p)}>
    <circle cx="6" cy="12" r="2.5" />
    <circle cx="18" cy="6" r="2.5" />
    <circle cx="18" cy="18" r="2.5" />
    <path d="M8 11l8-4M8 13l8 4" />
  </svg>
);
export const Camera = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M3 7h4l2-3h6l2 3h4v12H3V7z" />
    <circle cx="12" cy="13" r="3.5" />
  </svg>
);
export const Image = (p: IconProps) => (
  <svg {...base(p)}>
    <rect x="3" y="3" width="18" height="18" rx="2" />
    <circle cx="9" cy="9" r="2" />
    <path d="M21 15l-5-5L5 21" />
  </svg>
);
export const Filter = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M3 5h18M6 12h12M10 19h4" />
  </svg>
);
export const Globe = (p: IconProps) => (
  <svg {...base(p)}>
    <circle cx="12" cy="12" r="9" />
    <path d="M3 12h18M12 3a14 14 0 010 18M12 3a14 14 0 000 18" />
  </svg>
);
export const Trash = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M3 6h18M8 6V4h8v2M6 6l1 14h10l1-14" />
  </svg>
);
export const Refresh = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M3 12a9 9 0 0115-6.7L21 8" />
    <path d="M21 3v5h-5" />
    <path d="M21 12a9 9 0 01-15 6.7L3 16" />
    <path d="M3 21v-5h5" />
  </svg>
);
export const Shuffle = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M16 3h5v5M21 3l-7 7M16 21h5v-5M21 21l-7-7M3 3l7 7M3 21l5-5" />
  </svg>
);
export const Repeat = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M17 1l4 4-4 4" />
    <path d="M3 11V9a4 4 0 014-4h14" />
    <path d="M7 23l-4-4 4-4" />
    <path d="M21 13v2a4 4 0 01-4 4H3" />
  </svg>
);
export const Compass = (p: IconProps) => (
  <svg {...base(p)}>
    <circle cx="12" cy="12" r="9" />
    <path d="M16 8l-2 6-6 2 2-6 6-2z" />
  </svg>
);
export const Send = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M22 2L11 13" />
    <path d="M22 2l-7 20-4-9-9-4 20-7z" />
  </svg>
);
export const Tag = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M3 12V3h9l9 9-9 9-9-9z" />
    <circle cx="7" cy="7" r="1.5" />
  </svg>
);
export const Footprint = (p: IconProps) => (
  <svg {...base(p)}>
    <ellipse cx="9" cy="9" rx="3" ry="4" />
    <ellipse cx="16" cy="14" rx="3" ry="4" />
    <ellipse cx="11" cy="18" rx="2" ry="2.5" />
    <ellipse cx="6" cy="14" rx="2" ry="2.5" />
  </svg>
);
export const Sun = (p: IconProps) => (
  <svg {...base(p)}>
    <circle cx="12" cy="12" r="4" />
    <path d="M12 2v2M12 20v2M2 12h2M20 12h2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M4.93 19.07l1.41-1.41M17.66 6.34l1.41-1.41" />
  </svg>
);
export const Moon = (p: IconProps) => (
  <svg {...base(p)}>
    <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
  </svg>
);
