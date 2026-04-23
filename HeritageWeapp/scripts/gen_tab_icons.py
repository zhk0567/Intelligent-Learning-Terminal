# -*- coding: utf-8 -*-
"""生成 tabBar 图标：81×81 透明底 + 线形图标（微信建议尺寸，避免纯色块像「方框」）。"""
from __future__ import annotations

from pathlib import Path

from PIL import Image, ImageDraw


def _music(draw: ImageDraw.ImageDraw, c: tuple[int, int, int, int], s: float) -> None:
    ox, oy = 40 * s, 18 * s
    r = 7 * s
    draw.ellipse((ox - r, oy - r, ox + r, oy + r), outline=c, width=max(2, int(2 * s)))
    ox2, oy2 = 52 * s, 16 * s
    r2 = 5 * s
    draw.ellipse((ox2 - r2, oy2 - r2, ox2 + r2, oy2 + r2), outline=c, width=max(2, int(2 * s)))
    draw.line((ox + r - 1, oy, 54 * s, 62 * s), fill=c, width=max(2, int(2 * s)))
    draw.line((ox2 + r2 - 1, oy2, 54 * s, 62 * s), fill=c, width=max(2, int(2 * s)))


def _stories(draw: ImageDraw.ImageDraw, c: tuple[int, int, int, int], s: float) -> None:
    x0, y0 = 22 * s, 20 * s
    w, h = 36 * s, 42 * s
    draw.rounded_rectangle((x0, y0, x0 + w, y0 + h), radius=4 * s, outline=c, width=max(2, int(2 * s)))
    draw.line((x0 + w * 0.5, y0, x0 + w * 0.5, y0 + h), fill=c, width=max(2, int(2 * s)))


def _community(draw: ImageDraw.ImageDraw, c: tuple[int, int, int, int], s: float) -> None:
    draw.line((24 * s, 58 * s, 56 * s, 26 * s), fill=c, width=max(3, int(3 * s)))
    draw.polygon(
        (
            (58 * s, 22 * s),
            (62 * s, 30 * s),
            (54 * s, 28 * s),
        ),
        outline=c,
        width=max(2, int(2 * s)),
    )


def _mall(draw: ImageDraw.ImageDraw, c: tuple[int, int, int, int], s: float) -> None:
    x0, y0 = 22 * s, 28 * s
    w, h = 36 * s, 30 * s
    draw.arc((x0, y0 - 10 * s, x0 + w, y0 + 8 * s), start=180, end=360, fill=c, width=max(2, int(2 * s)))
    draw.rectangle((x0, y0, x0 + w, y0 + h), outline=c, width=max(2, int(2 * s)))
    draw.line((30 * s, 36 * s, 30 * s, 48 * s), fill=c, width=max(2, int(2 * s)))
    draw.line((50 * s, 36 * s, 50 * s, 48 * s), fill=c, width=max(2, int(2 * s)))


def _profile(draw: ImageDraw.ImageDraw, c: tuple[int, int, int, int], s: float) -> None:
    cx, cy = 40 * s, 28 * s
    r = 10 * s
    draw.ellipse((cx - r, cy - r, cx + r, cy + r), outline=c, width=max(2, int(2 * s)))
    draw.arc((24 * s, 44 * s, 56 * s, 72 * s), start=200, end=340, fill=c, width=max(2, int(2 * s)))


def _draw_icon(kind: str, size: int, rgba: tuple[int, int, int, int]) -> Image.Image:
    s = size / 81.0
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    dispatch = {
        "music": _music,
        "stories": _stories,
        "community": _community,
        "mall": _mall,
        "profile": _profile,
    }
    dispatch[kind](draw, rgba, s)
    return img


def main() -> None:
    root = Path(__file__).resolve().parent.parent / "assets" / "tab"
    root.mkdir(parents=True, exist_ok=True)
    size = 81
    # 未选中：与 tabBar color 接近的灰蓝；选中：主色
    normal = (169, 190, 208, 255)
    active = (55, 231, 255, 255)
    for name in ("music", "stories", "community", "mall", "profile"):
        _draw_icon(name, size, normal).save(root / f"{name}.png", format="PNG")
        _draw_icon(name, size, active).save(root / f"{name}-active.png", format="PNG")
    print("ok", root)


if __name__ == "__main__":
    main()
