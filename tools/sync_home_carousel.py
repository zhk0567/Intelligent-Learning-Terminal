# -*- coding: utf-8 -*-
"""从 `data/图片/首页轮播图` 生成三端首页轮播：`home_swiper_1.jpg` ~ `home_swiper_3.jpg`。"""
from __future__ import annotations

import re
import shutil
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "data" / "图片" / "首页轮播图"
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

    def order_key(p: Path):
        stem = p.stem
        # 文件名末尾数字优先：如 xxx_1、slide_02
        m = re.search(r"_(\d+)$", stem)
        if m:
            return (0, int(m.group(1)), p.name.lower())
        # 去掉常见前缀后按主题名排序，避免纯 Unicode 与预期不一致
        for pref in ("首页轮播图_", "首页轮播图"):
            if stem.startswith(pref):
                tail = stem[len(pref) :].lstrip("_").lower()
                return (1, tail)
        if re.search(r"\d", stem):
            return (2,) + natural_sort_key(p)
        return (3, p.name.lower())

    return sorted(fs, key=order_key)


def _sample_edge_color(im) -> tuple[int, int, int]:
    """从顶/底边采样像素均值，作为 letterbox 的填充色。

    采样行远离左右上下角（角上常压有人物 / 暗部装饰），取顶/底各两条窄带的中段像素均值。
    """
    w, h = im.size
    pixels: list[tuple[int, int, int]] = []
    rows = [max(1, h // 100), max(2, h // 50), h - 1 - max(1, h // 100), h - 1 - max(2, h // 50)]
    x_lo, x_hi = int(w * 0.20), int(w * 0.80)
    step = max(1, (x_hi - x_lo) // 80)
    for y in rows:
        if y < 0 or y >= h:
            continue
        for x in range(x_lo, x_hi, step):
            pixels.append(im.getpixel((x, y)))
    if not pixels:
        return (230, 200, 175)
    r = sum(p[0] for p in pixels) // len(pixels)
    g = sum(p[1] for p in pixels) // len(pixels)
    b = sum(p[2] for p in pixels) // len(pixels)
    return (r, g, b)


def save_jpeg(im, dest: Path, max_side: int, quality: int, target_aspect: float | None = None):
    """落盘 JPEG。

    `target_aspect` 给出后，会把过宽（aspect > target）的图片以采样的边缘色 letterbox 到目标比例，
    避免轮播位 `centerCrop` 把左右内容裁掉。
    """
    from PIL import Image

    if im.mode in ("RGBA", "P"):
        bg = Image.new("RGB", im.size, (255, 255, 255))
        if im.mode == "P":
            im = im.convert("RGBA")
        bg.paste(im, mask=im.split()[-1] if im.mode == "RGBA" else None)
        im = bg
    else:
        im = im.convert("RGB")

    if target_aspect is not None:
        w, h = im.size
        cur = w / h
        if cur > target_aspect + 0.05:  # 仅当明显比目标更宽（避免无意义重绘）
            new_h = int(round(w / target_aspect))
            pad_total = new_h - h
            pad_top = pad_total // 2
            pad_bottom = pad_total - pad_top
            fill = _sample_edge_color(im)
            canvas = Image.new("RGB", (w, new_h), fill)
            canvas.paste(im, (0, pad_top))
            im = canvas

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

    # 三张轮播图统一到接近 1.85:1，过宽的会自动用采样边缘色 letterbox，避免左右被裁。
    target_aspect = 1.85
    for i, src in enumerate(picks, start=1):
        name = f"home_swiper_{i}.jpg"
        save_jpeg(
            Image.open(src), OUT_MP / name, max_side=1000, quality=80, target_aspect=target_aspect
        )
        p_web = OUT_WEB / name
        save_jpeg(
            Image.open(src), p_web, max_side=1600, quality=85, target_aspect=target_aspect
        )
        shutil.copy2(p_web, OUT_AND / name)
        print("->", name, "<-", src.name)

    print("ok:", OUT_MP, OUT_WEB, OUT_AND)


if __name__ == "__main__":
    main()
