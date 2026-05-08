import TopBar from "../components/TopBar";
import SealStamp from "../components/SealStamp";
import { OrnateDivider, PatternStrip } from "../components/Ornament";

export default function About() {
  return (
    <div className="min-h-[100dvh] md:min-h-0 md:max-w-[640px] md:mx-auto">
      <TopBar title="关于古韵薪传" />
      <div className="px-6 pt-6 pb-10 md:px-0 md:pt-0 md:pb-0">
        <div className="flex flex-col items-center text-center">
          <SealStamp text="古韵" size={88} className="rounded-seal" />
          <div className="mt-4 text-lg font-bold text-text-primary tracking-wider">古韵薪传 · 科技赋能</div>
          <div className="mt-1 text-[11px] uppercase tracking-[0.32em] text-ancient-bronze/85">Heritage · Living</div>
          <div className="text-xs text-text-hint mt-1">Web Edition · v0.1.0</div>
        </div>

        <div className="my-5">
          <OrnateDivider />
        </div>

        <div className="rounded-2xl bg-bg-card border border-border/50 p-4 space-y-3">
          <p className="text-sm leading-7 text-text-secondary">
            「古韵薪传」致力于让非遗文化以现代方式流动：通过音乐、故事、社区与商城，让更多人遇见、理解并参与这份属于华夏的文化遗产。
          </p>
          <p className="text-sm leading-7 text-text-secondary">
            本 Web 版本与 Android 端共享同一套设计系统与数据结构，所有内容均为演示数据，可作为产品评审与设计走查使用。
          </p>
        </div>

        <div className="mt-4 rounded-2xl bg-bg-card border border-border/50 p-4 text-sm text-text-secondary space-y-2">
          <Row label="版权" value="© 古韵薪传 团队" />
          <Row label="官网" value="https://example.com" />
          <Row label="反馈" value="feedback@example.com" />
        </div>

        <div className="mt-6 px-2">
          <PatternStrip height={10} opacity={0.3} />
        </div>
      </div>
    </div>
  );
}

function Row({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex items-center justify-between text-xs">
      <span className="text-text-hint">{label}</span>
      <span className="text-text-primary">{value}</span>
    </div>
  );
}
