# -*- coding: utf-8 -*-
"""从 `data/图片/商城页轮播图` 生成三端商城顶栏轮播：`shop_swiper_1.jpg` ~ `shop_swiper_3.jpg`。"""
from __future__ import annotations

import re
import shutil
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "data" / "图片" / "商城页轮播图"
OUT_MP = ROOT / "miniprogram" / "images"
OUT_WEB = ROOT / "web" / "public" / "images"
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

    if not SRC.is_dir():
        print("missing", SRC)
        sys.exit(1)

    files = list_images(SRC)
    if len(files) < 3:
        print("need at least 3 images in", SRC, "got", len(files))
        sys.exit(1)

    picks = files[:3]
    for base in (OUT_MP, OUT_WEB, OUT_AND):
        base.mkdir(parents=True, exist_ok=True)

    for i, src in enumerate(picks, start=1):
        name = f"shop_swiper_{i}.jpg"
        save_jpeg(Image.open(src), OUT_MP / name, max_side=1000, quality=80)
        p_web = OUT_WEB / name
        save_jpeg(Image.open(src), p_web, max_side=1600, quality=85)
        shutil.copy2(p_web, OUT_AND / name)
        print("->", name, "<-", src.name)

    print("ok:", OUT_MP, OUT_WEB, OUT_AND)


if __name__ == "__main__":
    main()
