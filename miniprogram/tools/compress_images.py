"""压缩 miniprogram/images，降低主包体积（需 < 2MB）。"""
from __future__ import annotations

import io
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
IMG_DIR = ROOT / "images"


def ensure_pillow():
    try:
        from PIL import Image  # noqa: F401
    except ImportError:
        import subprocess

        subprocess.check_call(
            [sys.executable, "-m", "pip", "install", "pillow", "-q"],
            cwd=str(ROOT.parent),
        )


def save_jpeg(im, dest: Path, quality: int = 82) -> None:
    if im.mode not in ("RGB", "L"):
        im = im.convert("RGB")
    buf = io.BytesIO()
    im.save(buf, format="JPEG", quality=quality, optimize=True, progressive=True)
    dest.write_bytes(buf.getvalue())


def save_png(im, dest: Path) -> None:
    if im.mode == "P":
        im = im.convert("RGBA")
    buf = io.BytesIO()
    im.save(buf, format="PNG", optimize=True, compress_level=9)
    dest.write_bytes(buf.getvalue())


def main() -> None:
    ensure_pillow()
    from PIL import Image

    if not IMG_DIR.is_dir():
        print("missing", IMG_DIR)
        sys.exit(1)

    before = 0
    after = 0
    for path in sorted(IMG_DIR.iterdir()):
        if not path.is_file():
            continue
        suf = path.suffix.lower()
        if suf not in (".png", ".jpg", ".jpeg"):
            continue
        before += path.stat().st_size
        im = Image.open(path)
        out_path = path

        if suf in (".jpg", ".jpeg"):
            save_jpeg(im, path)
        elif suf == ".png":
            use_jpeg = False
            if im.mode in ("RGB", "L"):
                use_jpeg = True
            elif im.mode == "RGBA":
                ext = im.getchannel("A").getextrema()
                if ext == (255, 255):
                    use_jpeg = True
            if use_jpeg:
                new_path = path.with_suffix(".jpg")
                if im.mode == "RGBA":
                    im = im.convert("RGB")
                save_jpeg(im, new_path)
                if new_path != path:
                    path.unlink(missing_ok=True)
                out_path = new_path
            else:
                save_png(im, path)

        after += out_path.stat().st_size

    print(
        f"images: {before / 1024:.1f} KB -> {after / 1024:.1f} KB "
        f"(saved {(before - after) / 1024:.1f} KB)"
    )


if __name__ == "__main__":
    main()
