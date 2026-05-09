# -*- coding: utf-8 -*-
"""从 `data/图片/故事` 生成三端故事封面：`story_cover_<key>.jpg`（与 `StoriesData` / `story.ts` 中 key 一致）。"""
from __future__ import annotations

import shutil
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "data" / "图片" / "故事"
OUT_MP = ROOT / "miniprogram" / "images" / "story"
OUT_WEB = ROOT / "web" / "public" / "images" / "story"
OUT_AND = ROOT / "app" / "app" / "src" / "main" / "res" / "drawable"

# 与 `sync_stories_data.py` 中故事条目 key 对应（文件名不含扩展名 → key）
STEM_TO_KEY: dict[str, str] = {
    "河南坠子": "henan_zhuizi",
    "古琴": "guqin",
    "四平调": "siping_diao",
    "河洛大鼓": "heluo_dagu",
    "九莲灯": "jiulian_deng",
    "濮阳大弦戏": "puyang_daxianxi",
    "箜篌艺术": "konghou_yishu",
}


def ensure_pillow():
    try:
        from PIL import Image  # noqa: F401
    except ImportError:
        import subprocess

        subprocess.check_call([sys.executable, "-m", "pip", "install", "pillow", "-q"])


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
    if not SRC.is_dir():
        print("skip: missing", SRC)
        return 0
    files = [p for p in SRC.iterdir() if p.suffix.lower() in (".png", ".jpg", ".jpeg", ".webp")]
    if not files:
        print("skip: no images in", SRC)
        return 0

    ensure_pillow()
    from PIL import Image

    for base in (OUT_MP, OUT_WEB, OUT_AND):
        base.mkdir(parents=True, exist_ok=True)

    n = 0
    for src in sorted(files, key=lambda p: p.name):
        stem = src.stem
        key = STEM_TO_KEY.get(stem)
        if not key:
            print("warn: unknown stem, skip:", src.name)
            continue
        name = f"story_cover_{key}.jpg"
        save_jpeg(Image.open(src), OUT_MP / name, max_side=1000, quality=82)
        p_web = OUT_WEB / name
        save_jpeg(Image.open(src), p_web, max_side=1600, quality=86)
        shutil.copy2(p_web, OUT_AND / name)
        print("->", name, "<-", src.name)
        n += 1
    print("ok:", n, "covers")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
