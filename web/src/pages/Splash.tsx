import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { APP_IMAGES } from "../assets/appImages";
import SealStamp from "../components/SealStamp";

export default function Splash() {
  const navigate = useNavigate();
  useEffect(() => {
    const t = setTimeout(() => navigate("/login", { replace: true }), 1600);
    return () => clearTimeout(t);
  }, [navigate]);

  return (
    <div className="min-h-[100dvh] flex flex-col items-center justify-center px-8 text-center bg-bg-primary md:max-w-[420px] md:mx-auto">
      <div className="relative">
        <div className="h-32 w-32 rounded-full bg-bg-card grid place-items-center border-2 border-ancient-bronze/60 shadow-card overflow-hidden p-2">
          <img src={APP_IMAGES.logo} alt="古韵薪传" className="h-full w-full object-contain" />
        </div>
        <div className="absolute -inset-3 rounded-full border border-ancient-bronze/30 animate-pulseSoft" />
        <SealStamp
          text="韵"
          size={36}
          className="absolute -right-2 -bottom-2 animate-stampPress"
        />
      </div>
      <h1 className="mt-8 text-2xl font-bold text-text-primary tracking-[0.4em]">古韵薪传</h1>
      <p className="mt-2 text-xs uppercase tracking-[0.32em] text-ancient-bronze/85">
        Heritage · Living
      </p>
      <p className="mt-2 text-sm text-text-secondary">科技赋能 · 让非遗触手可及</p>
      <div className="mt-10 h-1 w-32 overflow-hidden rounded-full bg-border/30">
        <div className="h-full w-1/2 bg-ancient-cinnabar animate-pulseSoft" />
      </div>
    </div>
  );
}
