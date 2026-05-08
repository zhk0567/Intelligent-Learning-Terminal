/**
 * 与 Android `res/drawable`、小程序 `images/` 中位图一致。
 * `homeSwiper*` 仅音乐首页轮播；`banner*` 为通用占位（与 p_1~3 同图），勿与轮播混用。
 */
export const APP_IMAGES = {
  homeSwiper1: "/images/home_swiper_1.jpg",
  homeSwiper2: "/images/home_swiper_2.jpg",
  homeSwiper3: "/images/home_swiper_3.jpg",
  banner1: "/images/banner1_img.jpg",
  banner2: "/images/banner2_img.jpg",
  banner3: "/images/banner3_img.jpg",
  logo: "/images/logo.png",
  p1: "/images/p_1.jpg",
  p2: "/images/p_2.jpg",
  p3: "/images/p_3.jpg",
  p4: "/images/p_4.jpg",
  p5: "/images/p_5.jpg",
  p6: "/images/p_6.jpg",
  p7: "/images/p_7.jpg",
} as const;

/** 与 `ShopCatalog.shopListImagePool` 顺序一致：三 banner + 三个占位（Android 为 vector）。 */
const SHOP_POOL: (keyof typeof APP_IMAGES | null)[] = [
  "banner1",
  "banner2",
  "banner3",
  null,
  null,
  null,
];

/** 列表商品封面：与 `ShopCatalog.shopListImage(index)` 对齐。 */
export function shopListCoverSrc(listIndex: number): string | undefined {
  const key = SHOP_POOL[listIndex % SHOP_POOL.length];
  return key ? APP_IMAGES[key] : undefined;
}

/** 通用三图占位（商城顶栏活动等），与 `ShopActivity.shopBannerSlides` 用图一致。 */
export function bannerBySlot(slot: 0 | 1 | 2): string {
  return [APP_IMAGES.banner1, APP_IMAGES.banner2, APP_IMAGES.banner3][slot];
}

/** 仅音乐首页轮播，与 `MusicLibActivity.bannerImages` 顺序一致。 */
export function homeSwiperBySlot(slot: 0 | 1 | 2): string {
  return [APP_IMAGES.homeSwiper1, APP_IMAGES.homeSwiper2, APP_IMAGES.homeSwiper3][slot];
}
