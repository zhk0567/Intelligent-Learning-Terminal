/** 与小程序同源：文创「图片」封面 +「样机」详情图（`tools/sync_wenchuang_shop.py`）。 */
export const WC_COVER_COUNT = 20 as const;
export const WC_MOCK_COUNT = 17 as const;

const pad = (n: number) => (n < 10 ? `0${n}` : `${n}`);

export function wcCoverPath(index: number): string {
  const n = WC_COVER_COUNT;
  const i = ((index % n) + n) % n;
  return `/images/shop/wc_cover_${pad(i + 1)}.jpg`;
}

export function wcMockPath(index: number): string {
  const n = WC_MOCK_COUNT;
  const i = ((index % n) + n) % n;
  return `/images/shop/wc_mock_${pad(i + 1)}.jpg`;
}
