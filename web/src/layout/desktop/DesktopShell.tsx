import { ReactNode } from "react";
import Sidebar from "./Sidebar";
import Header from "./Header";
import PlayerBar from "./PlayerBar";
import { ToastHost } from "../../components/Toast";

interface Props {
  children: ReactNode;
}

export default function DesktopShell({ children }: Props) {
  return (
    <div
      className="min-h-[100dvh] w-full bg-bg-primary"
      style={{
        display: "grid",
        gridTemplateAreas: '"side header" "side main" "side player"',
        gridTemplateRows: "56px 1fr 80px",
        gridTemplateColumns: "240px 1fr",
      }}
    >
      <Sidebar />
      <Header />
      <main
        className="overflow-y-auto"
        style={{ gridArea: "main" }}
      >
        <div className="max-w-[1180px] mx-auto px-6 pt-4 pb-6">{children}</div>
      </main>
      <PlayerBar />
      <ToastHost />
    </div>
  );
}
