import { shopSwiperBySlot } from "./appImages";
import { wcCoverPath, wcMockPath } from "./shopMedia";

export type ShopCategoryId =
  | "all"
  | "heritage"
  | "instrument"
  | "aroma"
  | "collab"
  | "sale";

export interface Product {
  id: string;
  name: string;
  price: number;
  rating: number;
  description: string;
  category: string;
  tags: string[];
  shopCategoryId: ShopCategoryId;
  originalPrice?: number;
  salesCount: number;
  isNew: boolean;
  /** 列表封面：`data/文创/图片` 同步至 `/images/shop/wc_cover_*.jpg`。 */
  imageSrc?: string;
  /** 详情首图（样机）：`/images/shop/wc_mock_*.jpg`。 */
  detailHeroSrc?: string;
  /** 详情栅格图（样机）。 */
  detailSrc1?: string;
  detailSrc2?: string;
}

export interface ShopCategoryTab {
  id: ShopCategoryId;
  label: string;
}

export const SHOP_CATEGORY_TABS: ShopCategoryTab[] = [
  { id: "all", label: "全部商品" },
  { id: "heritage", label: "非遗文创" },
  { id: "instrument", label: "乐器周边" },
  { id: "aroma", label: "香薰雅物" },
  { id: "collab", label: "限定联名" },
  { id: "sale", label: "限时折扣" },
];

export const ALL_PRODUCTS: Product[] = [
  { id: "s1",  name: "敦煌乐器冰箱贴", price: 30,  rating: 4.9, description: "敦煌壁画灵感金属冰箱贴，非遗文创伴手礼。", category: "文创", tags: ["敦煌", "冰箱贴"], shopCategoryId: "heritage", originalPrice: 38, salesCount: 1280, isNew: true },
  { id: "s2",  name: "非遗剪纸书签", price: 25,  rating: 4.7, description: "手工剪纸书签，传统纹样镂空设计。", category: "文创", tags: ["剪纸", "书签"], shopCategoryId: "heritage", originalPrice: 32, salesCount: 856, isNew: false },
  { id: "s3",  name: "青花瓷香薰蜡烛", price: 68,  rating: 4.8, description: "青花瓷纹陶瓷杯身，天然植物蜡。", category: "香薰", tags: ["青花", "蜡烛"], shopCategoryId: "aroma", originalPrice: 88, salesCount: 642, isNew: true },
  { id: "s4",  name: "蜀绣丝巾", price: 128, rating: 4.9, description: "手工蜀绣方巾，桑蚕丝底料。", category: "服饰", tags: ["蜀绣", "丝巾"], shopCategoryId: "heritage", originalPrice: 168, salesCount: 420, isNew: false },
  { id: "s5",  name: "景泰蓝手镯", price: 89,  rating: 4.6, description: "铜胎掐丝珐琅工艺，国风配色。", category: "首饰", tags: ["景泰蓝"], shopCategoryId: "heritage", salesCount: 310, isNew: false },
  { id: "s6",  name: "古琴拨片礼盒", price: 48,  rating: 4.8, description: "牛角材质拨片，配锦盒与养护说明。", category: "乐器", tags: ["古琴", "拨片"], shopCategoryId: "instrument", originalPrice: 58, salesCount: 512, isNew: true },
  { id: "s7",  name: "非遗大师联名乐谱", price: 118, rating: 4.9, description: "大师校订古谱影印，附导读册。", category: "乐谱", tags: ["联名", "乐谱"], shopCategoryId: "collab", originalPrice: 158, salesCount: 289, isNew: true },
  { id: "s8",  name: "线香礼盒套装", price: 96,  rating: 4.7, description: "天然沉香线香，竹制香插与礼盒。", category: "香道", tags: ["线香", "礼盒"], shopCategoryId: "aroma", originalPrice: 128, salesCount: 730, isNew: false },
  { id: "s9",  name: "铜制小号香炉", price: 158, rating: 4.8, description: "仿古三足炉形，适合盘香与塔香。", category: "香道", tags: ["香炉"], shopCategoryId: "aroma", originalPrice: 198, salesCount: 205, isNew: false },
  { id: "s10", name: "敦煌联名帆布袋", price: 59,  rating: 4.6, description: "加厚帆布，飞天纹样数码印花。", category: "包袋", tags: ["敦煌", "联名"], shopCategoryId: "collab", originalPrice: 79, salesCount: 940, isNew: false },
  { id: "s11", name: "苏绣团扇（限时）", price: 168, rating: 4.9, description: "双面绣团扇，檀木扇柄。", category: "工艺", tags: ["苏绣", "团扇"], shopCategoryId: "sale", originalPrice: 228, salesCount: 156, isNew: false },
  { id: "s12", name: "古琴模型摆件", price: 198, rating: 4.7, description: "1:8 缩比仲尼式古琴，鸡翅木底座。", category: "摆件", tags: ["古琴", "模型"], shopCategoryId: "instrument", originalPrice: 248, salesCount: 178, isNew: true },
  { id: "s13", name: "竹笛挂饰（迷你）", price: 36,  rating: 4.5, description: "手工竹制迷你笛，可作包挂与车挂。", category: "挂饰", tags: ["竹笛"], shopCategoryId: "instrument", salesCount: 402, isNew: false },
  { id: "s14", name: "大师签名折扇", price: 268, rating: 5.0, description: "宣纸折扇，非遗书法大师题字限量版。", category: "联名", tags: ["折扇", "限量"], shopCategoryId: "collab", originalPrice: 328, salesCount: 92, isNew: true },
  { id: "s15", name: "香薰精油补充装", price: 45,  rating: 4.6, description: "与店内香薰蜡烛同系列香型。", category: "香薰", tags: ["精油"], shopCategoryId: "aroma", originalPrice: 58, salesCount: 560, isNew: false },
  { id: "s16", name: "云纹金属书签（套装）", price: 42, rating: 4.8, description: "激光镂空云雷纹，黄铜镀哑光。", category: "文创", tags: ["书签", "金属"], shopCategoryId: "heritage", originalPrice: 52, salesCount: 670, isNew: false },
  { id: "s17", name: "非遗皮影小夜灯", price: 128, rating: 4.7, description: "皮影元素亚克力灯罩，可调光。", category: "家居", tags: ["皮影", "夜灯"], shopCategoryId: "heritage", originalPrice: 168, salesCount: 240, isNew: true },
  { id: "s18", name: "联名款蓝牙音箱（宫调）", price: 399, rating: 4.8, description: "宫灯造型腔体，与民乐厂牌联名调音。", category: "数码", tags: ["联名", "音箱"], shopCategoryId: "collab", originalPrice: 499, salesCount: 133, isNew: true },
  { id: "s19", name: "手工漆器首饰盒（清仓）", price: 188, rating: 4.7, description: "大漆莳绘小盒，微瑕特价出清。", category: "工艺", tags: ["漆器", "清仓"], shopCategoryId: "sale", originalPrice: 320, salesCount: 45, isNew: false },
].map((p, i) => ({
  ...p,
  imageSrc: wcCoverPath(i),
  detailHeroSrc: wcMockPath(i),
  detailSrc1: wcMockPath(i),
  detailSrc2: wcMockPath(i + 1),
})) as Product[];

export function filterByCategory(list: Product[], id: ShopCategoryId): Product[] {
  if (id === "all") return list;
  return list.filter((p) => p.shopCategoryId === id);
}

/** 商城顶部活动轮播，图源 `data/图片/商城页轮播图`（`tools/sync_shop_carousel.py`）。 */
export const SHOP_BANNERS = [
  { id: "b1", title: "敦煌系列联名上线", subtitle: "非遗匠心 · 限量发售", imageSrc: shopSwiperBySlot(0) },
  { id: "b2", title: "新品·苏绣团扇", subtitle: "双面绣 / 檀木扇柄", imageSrc: shopSwiperBySlot(1) },
  { id: "b3", title: "夏夜香薰雅集", subtitle: "买二赠一 · 限时折扣", imageSrc: shopSwiperBySlot(2) },
];
