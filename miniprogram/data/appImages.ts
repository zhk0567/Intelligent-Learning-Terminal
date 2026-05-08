/**
 * 小程序 `images/` 下资源路径。
 * - `homeSwiper*`：仅首页（音乐 Tab）顶部轮播，源图见 `data/图片/首页轮播图`，打包为 `home_swiper_*.jpg`。
 * - `banner*`：历史占位，与 Android 布局/旧数据一致，对应 `banner*_img.jpg`（与 p_1~3 同图，勿再当轮播用）。
 */
export const APP_IMAGES = {
  homeSwiper1: "/images/home_swiper_1.jpg",
  homeSwiper2: "/images/home_swiper_2.jpg",
  homeSwiper3: "/images/home_swiper_3.jpg",
  banner1: "/images/banner1_img.jpg",
  banner2: "/images/banner2_img.jpg",
  banner3: "/images/banner3_img.jpg",
  logo: "/images/logo.jpg",
  p1: "/images/p_1.jpg",
  p2: "/images/p_2.jpg",
  p3: "/images/p_3.jpg",
  p4: "/images/p_4.jpg",
  p5: "/images/p_5.jpg",
  p6: "/images/p_6.jpg",
  p7: "/images/p_7.jpg",
} as const;

const SHOP_POOL: (keyof typeof APP_IMAGES | null)[] = [
  "banner1",
  "banner2",
  "banner3",
  null,
  null,
  null,
];

export function shopListCoverSrc(listIndex: number): string | undefined {
  const key = SHOP_POOL[listIndex % SHOP_POOL.length];
  return key ? APP_IMAGES[key] : undefined;
}

export function bannerBySlot(slot: 0 | 1 | 2): string {
  return [APP_IMAGES.banner1, APP_IMAGES.banner2, APP_IMAGES.banner3][slot];
}

/** 仅音乐首页顶部轮播三帧（勿用于商城/头像等）。 */
export function homeSwiperBySlot(slot: 0 | 1 | 2): string {
  return [APP_IMAGES.homeSwiper1, APP_IMAGES.homeSwiper2, APP_IMAGES.homeSwiper3][slot];
}
