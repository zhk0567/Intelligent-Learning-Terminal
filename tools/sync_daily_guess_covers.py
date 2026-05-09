# -*- coding: utf-8 -*-
"""从 `data/图片/猜你喜欢` 生成封面：`daily_guess_cover_01.jpg` …（与 `daily_guess_01.mp3` 顺序一致）。"""
from __future__ import annotations

import re
import shutil
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "data" / "图片" / "猜你喜欢"
OUT_MP = ROOT / "miniprogram" / "images" / "daily_guess"
OUT_WEB = ROOT / "web" / "public" / "images" / "daily_guess"
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
    if not files:
        print("no images in", SRC)
        sys.exit(1)

    for d in (OUT_MP, OUT_WEB, OUT_AND):
        d.mkdir(parents=True, exist_ok=True)

    for i, src in enumerate(files, start=1):
        name = f"daily_guess_cover_{i:02d}.jpg"
        save_jpeg(Image.open(src), OUT_MP / name, max_side=720, quality=82)
        p_web = OUT_WEB / name
        save_jpeg(Image.open(src), p_web, max_side=1200, quality=86)
        shutil.copy2(p_web, OUT_AND / name)
        print("->", name, "<-", src.name)

    print("ok:", OUT_MP, OUT_WEB, OUT_AND)


if __name__ == "__main__":
    main()
