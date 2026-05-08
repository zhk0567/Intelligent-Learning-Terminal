import { APP_IMAGES } from "../assets/appImages";

export interface HistoryItem {
  id: string;
  title: string;
  type: "商品" | "作品" | "音乐";
  browseTime: string;
  desc?: string;
  /** 与 `HistoryActivity` 列表位图顺序一致：banner2 → banner1 → banner3 循环。 */
  coverSrc: string;
}

export const HISTORY: HistoryItem[] = [
  { id: "h1", title: "敦煌乐器冰箱贴", type: "商品", browseTime: "今日 10:24", desc: "¥30 · 非遗文创", coverSrc: APP_IMAGES.banner2 },
  { id: "h2", title: "高山流水遇知音", type: "作品", browseTime: "今日 09:50", desc: "古琴传承人 · 王慕之", coverSrc: APP_IMAGES.banner1 },
  { id: "h3", title: "梅花三弄", type: "音乐", browseTime: "昨日 22:18", desc: "笛子独奏", coverSrc: APP_IMAGES.banner3 },
  { id: "h4", title: "苏绣团扇（限时）", type: "商品", browseTime: "昨日 19:43", desc: "¥168 · 限时折扣", coverSrc: APP_IMAGES.banner2 },
  { id: "h5", title: "皮影戏：光影乾坤", type: "作品", browseTime: "前日 15:02", desc: "皮影戏团 · 老李", coverSrc: APP_IMAGES.banner1 },
  { id: "h6", title: "古琴拨片礼盒", type: "商品", browseTime: "上周三", desc: "¥48 · 乐器周边", coverSrc: APP_IMAGES.banner3 },
];
