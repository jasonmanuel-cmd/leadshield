"""
Generate Android launcher mipmaps + web logo pack from branding/source/mctb-logo-full.png
"""
from __future__ import annotations

import os
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "source" / "mctb-logo-full.png"
OUT_WEB = ROOT / "web-pack"
OUT_ANDROID = Path(__file__).resolve().parents[2] / "app" / "src" / "main" / "res"


def crop_symbol_square(src: Image.Image) -> Image.Image:
    """Isolate the app-mark (shield + bubble + bolt) from full marketing lockup."""
    w, h = src.size
    # Icon sits in upper portion; exclude wordmark below.
    top_frac = 0.52
    th = int(h * top_frac)
    top = src.crop((0, 0, w, th))
    tw, th2 = top.size
    side = min(tw, th2)
    left = (tw - side) // 2
    return top.crop((left, 0, left + side, th2))


def resize_rgb(img: Image.Image, size: tuple[int, int]) -> Image.Image:
    return img.convert("RGBA").resize(size, Image.Resampling.LANCZOS)


def main() -> None:
    if not SRC.exists():
        raise SystemExit(f"Missing source: {SRC}")

    src = Image.open(SRC)
    symbol = crop_symbol_square(src)
    full_rgba = src.convert("RGBA")

    OUT_WEB.mkdir(parents=True, exist_ok=True)
    symbol.save(OUT_WEB / "mctb-symbol-1024.png")
    full_rgba.save(OUT_WEB / "mctb-logo-full-1024.png")
    # Web common sizes
    resize_rgb(full_rgba, (512, int(512 * full_rgba.height / full_rgba.width))).save(
        OUT_WEB / "mctb-logo-full-512w.png"
    )
    resize_rgb(symbol, (512, 512)).save(OUT_WEB / "mctb-symbol-512.png")
    resize_rgb(symbol, (256, 256)).save(OUT_WEB / "mctb-symbol-256.png")
    resize_rgb(symbol, (128, 128)).save(OUT_WEB / "favicon-128.png")
    resize_rgb(symbol, (32, 32)).save(OUT_WEB / "favicon-32.png")
    resize_rgb(symbol, (16, 16)).save(OUT_WEB / "favicon-16.png")
    resize_rgb(symbol, (180, 180)).save(OUT_WEB / "apple-touch-icon-180.png")

    # Legacy launcher sizes (square)
    legacy = {
        "mipmap-mdpi": 48,
        "mipmap-hdpi": 72,
        "mipmap-xhdpi": 96,
        "mipmap-xxhdpi": 144,
        "mipmap-xxxhdpi": 192,
    }
    # Adaptive foreground (108dp base * density)
    adaptive = {
        "mipmap-mdpi": 108,
        "mipmap-hdpi": 162,
        "mipmap-xhdpi": 216,
        "mipmap-xxhdpi": 324,
        "mipmap-xxxhdpi": 432,
    }

    for folder, px in legacy.items():
        d = OUT_ANDROID / folder
        d.mkdir(parents=True, exist_ok=True)
        resize_rgb(symbol, (px, px)).save(d / "ic_launcher.png")
        resize_rgb(symbol, (px, px)).save(d / "ic_launcher_round.png")

    for folder, px in adaptive.items():
        d = OUT_ANDROID / folder
        d.mkdir(parents=True, exist_ok=True)
        resize_rgb(symbol, (px, px)).save(d / "ic_launcher_foreground.png")

    # Update adaptive XML to use bitmap foreground
    anydpi = OUT_ANDROID / "mipmap-anydpi-v26"
    anydpi.mkdir(parents=True, exist_ok=True)
    (anydpi / "ic_launcher.xml").write_text(
        """<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
""",
        encoding="utf-8",
    )
    (anydpi / "ic_launcher_round.xml").write_text(
        """<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
""",
        encoding="utf-8",
    )

    print("Done.")
    print(f"Web pack: {OUT_WEB}")
    print(f"Android res: {OUT_ANDROID}")


if __name__ == "__main__":
    main()
