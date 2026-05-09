# -*- coding: utf-8 -*-
"""将 `data/音频/猜你喜欢` 下 mp3 拷到小程序 / Web / Android raw，文件名 `daily_guess_01.mp3` …（按排序）。"""
from __future__ import annotations

import re
import shutil
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "data" / "音频" / "猜你喜欢"
OUT_MP = ROOT / "miniprogram" / "audio" / "daily_guess"
OUT_WEB = ROOT / "web" / "public" / "audio" / "daily_guess"
OUT_RAW = ROOT / "app" / "app" / "src" / "main" / "res" / "raw"


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


def list_mp3(folder: Path):
    fs = [p for p in folder.iterdir() if p.suffix.lower() == ".mp3"]
    return sorted(fs, key=lambda p: natural_sort_key(p) if re.search(r"\d", p.stem) else ((1, p.name.lower()),))


def duration_sec(path: Path) -> int:
    try:
        from mutagen.mp3 import MP3

        return max(1, int(MP3(path).info.length))
    except Exception:
        pass
    try:
        import subprocess

        r = subprocess.run(
            [
                "ffprobe",
                "-v",
                "error",
                "-show_entries",
                "format=duration",
                "-of",
                "default=noprint_wrappers=1:nokey=1",
                str(path),
            ],
            capture_output=True,
            text=True,
            timeout=60,
            creationflags=subprocess.CREATE_NO_WINDOW if sys.platform == "win32" else 0,
        )
        if r.returncode == 0 and r.stdout.strip():
            return max(1, int(float(r.stdout.strip())))
    except Exception:
        pass
    return 240


def parse_title_instrument(stem: str) -> tuple[str, str]:
    if "-" in stem:
        a, b = stem.split("-", 1)
        return a.strip(), b.strip()
    return stem.strip(), ""


def main():
    if not SRC.is_dir():
        print("missing", SRC)
        sys.exit(1)
    files = list_mp3(SRC)
    if not files:
        print("no mp3 in", SRC)
        sys.exit(1)

    for d in (OUT_MP, OUT_WEB, OUT_RAW):
        d.mkdir(parents=True, exist_ok=True)

    print("--- copy + meta (for tracks.ts / PlayerSyncState) ---")
    for i, src in enumerate(files, start=1):
        name = f"daily_guess_{i:02d}.mp3"
        shutil.copy2(src, OUT_MP / name)
        shutil.copy2(src, OUT_WEB / name)
        shutil.copy2(src, OUT_RAW / name)
        title, inst = parse_title_instrument(src.stem)
        dur = duration_sec(src)
        print(f"  {i}: {title} | {inst} | {dur}s -> {name}")

    print("ok:", OUT_MP, OUT_WEB, OUT_RAW)


if __name__ == "__main__":
    main()
