# -*- coding: utf-8 -*-
"""从 data/文创/图片、data/文创/样机 生成商城资源并写入 miniprogram/web/Android drawable。"""
from __future__ import annotations

import re
import shutil
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SRC_COVER = ROOT / "data" / "文创" / "图片"
SRC_MOCK = ROOT / "data" / "文创" / "样机"

OUT_MP = ROOT / "miniprogram" / "images" / "shop"
OUT_WEB = ROOT / "web" / "public" / "images" / "shop"
OUT_AND = ROOT / "app" / "app" / "src" / "main" / "res" / "drawable"


def ensure_pillow():
    try:
        from PIL import Image  # noqa: F401
    except ImportError:
        import subprocess

        subprocess.check_call([sys.executable, "-m", "pip", "install", "pillow", "-q"])


def natural_sort_key(p: Path):
    s = p.stem
    parts = re.split(r"(\d+)", s)
    key: list = []
    for t in parts:
        if not t:
            continue
        if t.isdigit():
            key.append((0, int(t)))
        else:
            key.append((1, t.lower()))
    return tuple(key)


def list_images(folder: Path):
    fs = [p for p in folder.iterdir() if p.suffix.lower() in (".jpg", ".jpeg", ".png", ".webp")]
    return sorted(fs, key=lambda p: natural_sort_key(p) if re.search(r"\d", p.stem) else ((1, p.name.lower()),))


def save_jpeg(im, dest: Path, max_side: int, quality: int):
    from PIL import Image

    if im.mode in ("RGBA", "P"):
        bg = Image.new("RGB", im.size, (255, 255, 255))
        if im.mode == "P":
            im = im.convert("RGBA")
        bg.paste(im, mask=im.split()[-1] if im.mode == "RGBA" else None)
        im = bg
    else:
        im = im.convert("RGB")
    w, h = im.size
    m = max(w, h)
    if m > max_side:
        s = max_side / m
        im = im.resize((int(w * s), int(h * s)), Image.Resampling.LANCZOS)
    dest.parent.mkdir(parents=True, exist_ok=True)
    im.save(dest, "JPEG", quality=quality, optimize=True, progressive=True)


def main():
    ensure_pillow()
    from PIL import Image

    if not SRC_COVER.is_dir():
        print("missing", SRC_COVER)
        sys.exit(1)
    if not SRC_MOCK.is_dir():
        print("missing", SRC_MOCK)
        sys.exit(1)

    covers = list_images(SRC_COVER)
    mocks = list_images(SRC_MOCK)
    if not covers or not mocks:
        print("empty covers or mocks", len(covers), len(mocks))
        sys.exit(1)

    for base in (OUT_MP, OUT_WEB, OUT_AND):
        base.mkdir(parents=True, exist_ok=True)

    # 小程序主包 images 总大小需控制在约 2MB 内：miniprogram 用更小尺寸/更低质量；
    # Web 与 Android drawable 共用一份较高质量图（从 web 拷到 drawable，避免重复编码）。
    for i, src in enumerate(covers, start=1):
        name = f"wc_cover_{i:02d}.jpg"
        save_jpeg(Image.open(src), OUT_MP / name, max_side=560, quality=68)
        p_web = OUT_WEB / name
        save_jpeg(Image.open(src), p_web, max_side=800, quality=78)
        shutil.copy2(p_web, OUT_AND / name)

    for i, src in enumerate(mocks, start=1):
        name = f"wc_mock_{i:02d}.jpg"
        save_jpeg(Image.open(src), OUT_MP / name, max_side=640, quality=65)
        p_web = OUT_WEB / name
        save_jpeg(Image.open(src), p_web, max_side=900, quality=74)
        shutil.copy2(p_web, OUT_AND / name)

    n_c, n_m = len(covers), len(mocks)
    lines = [
        "/** 由 `tools/sync_wenchuang_shop.py` 生成：文创「图片」封面 +「样机」详情图。 */",
        f"export const WC_COVER_COUNT = {n_c} as const;",
        f"export const WC_MOCK_COUNT = {n_m} as const;",
        "",
        "const pad = (n: number) => (n < 10 ? `0${n}` : `${n}`);",
        "",
        "export function wcCoverPath(index: number): string {",
        "  const n = WC_COVER_COUNT;",
        "  const i = ((index % n) + n) % n;",
        "  return `/images/shop/wc_cover_${pad(i + 1)}.jpg`;",
        "}",
        "",
        "export function wcMockPath(index: number): string {",
        "  const n = WC_MOCK_COUNT;",
        "  const i = ((index % n) + n) % n;",
        "  return `/images/shop/wc_mock_${pad(i + 1)}.jpg`;",
        "}",
        "",
    ]
    body = "\n".join(lines) + "\n"
    for rel in (
        ROOT / "miniprogram" / "data" / "shopMedia.ts",
        ROOT / "web" / "src" / "data" / "shopMedia.ts",
    ):
        rel.write_text(body, encoding="utf-8")

    print("covers", n_c, "mocks", n_m)
    print("written", OUT_MP, OUT_WEB, OUT_AND)
    print("shopMedia.ts (miniprogram + web) updated")


if __name__ == "__main__":
    main()
