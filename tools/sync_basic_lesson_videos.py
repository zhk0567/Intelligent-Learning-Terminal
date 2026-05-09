# -*- coding: utf-8 -*-
"""将 `data/视频/基础学习` 下 mp4 拷到 Web public（基础学习视频文件较大，约 50–90MB，仅 Web 本地可用）。

文件名 `basic_lesson_01.mp4` …（按排序）。小程序 / Android 通过同名相对路径加载，
默认指向 Web 静态资源；上线时把 `LESSON_VIDEO_BASE` 改为 CDN 即可。
"""
from __future__ import annotations

import re
import shutil
import struct
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "data" / "视频" / "基础学习"
OUT_WEB = ROOT / "web" / "public" / "video" / "basic_lesson"


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


def list_mp4(folder: Path):
    fs = [p for p in folder.iterdir() if p.suffix.lower() == ".mp4"]
    return sorted(fs, key=lambda p: natural_sort_key(p) if re.search(r"\d", p.stem) else ((1, p.name.lower()),))


def mp4_duration(p: Path):
    """解析 MP4 mvhd 原子，返回秒数；失败回退 mutagen / ffprobe。"""
    try:
        with p.open("rb") as f:
            def read_atom():
                head = f.read(8)
                if len(head) < 8:
                    return None, None
                size, typ = struct.unpack(">I4s", head)
                if size == 1:
                    size = struct.unpack(">Q", f.read(8))[0]
                    hdr = 16
                else:
                    hdr = 8
                return typ.decode("latin1"), size - hdr

            while True:
                typ_sz = read_atom()
                if typ_sz == (None, None):
                    break
                typ, sz = typ_sz
                if typ == "moov":
                    end = f.tell() + sz
                    while f.tell() < end:
                        typ2, sz2 = read_atom()
                        if typ2 is None:
                            break
                        if typ2 == "mvhd":
                            ver = f.read(1)[0]
                            f.read(3)
                            if ver == 1:
                                f.read(8); f.read(8)
                                ts = struct.unpack(">I", f.read(4))[0]
                                dur = struct.unpack(">Q", f.read(8))[0]
                            else:
                                f.read(4); f.read(4)
                                ts = struct.unpack(">I", f.read(4))[0]
                                dur = struct.unpack(">I", f.read(4))[0]
                            return int(dur / ts) if ts else None
                        f.seek(sz2, 1)
                    return None
                f.seek(sz, 1)
    except Exception:
        pass
    return None


def parse_title_instrument(stem: str) -> tuple[str, str]:
    if "-" in stem:
        a, b = stem.split("-", 1)
        return a.strip(), b.strip()
    return stem.strip(), ""


def main():
    if not SRC.is_dir():
        print("missing", SRC)
        sys.exit(1)
    files = list_mp4(SRC)
    if not files:
        print("no mp4 in", SRC)
        sys.exit(1)

    OUT_WEB.mkdir(parents=True, exist_ok=True)

    print("--- copy + meta (for BASIC_LESSONS) ---")
    for i, src in enumerate(files, start=1):
        name = f"basic_lesson_{i:02d}.mp4"
        dest = OUT_WEB / name
        shutil.copy2(src, dest)
        title, art = parse_title_instrument(src.stem)
        dur = mp4_duration(src) or 0
        size_mb = src.stat().st_size / 1024 / 1024
        print(f"  {i}: {title} | {art} | {dur}s | {size_mb:.1f}MB -> {name}")

    print("ok:", OUT_WEB)
    print("note: 视频较大未拷贝到小程序 / Android 包；通过 `LESSON_VIDEO_BASE` 引用 Web 静态/CDN。")


if __name__ == "__main__":
    main()
