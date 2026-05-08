import { useEffect, useState } from "react";

const QUERY = "(min-width: 768px)";

export function useIsDesktop(): boolean {
  const [v, set] = useState(() =>
    typeof window === "undefined" ? false : window.matchMedia(QUERY).matches,
  );
  useEffect(() => {
    if (typeof window === "undefined") return;
    const mql = window.matchMedia(QUERY);
    const on = () => set(mql.matches);
    mql.addEventListener("change", on);
    set(mql.matches);
    return () => mql.removeEventListener("change", on);
  }, []);
  return v;
}
