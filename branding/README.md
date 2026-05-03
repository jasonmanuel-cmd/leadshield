# MCTB brand assets

## Source

- `source/mctb-logo-full.png` — Full marketing lockup (icon + wordmark + tagline).  
  Replace this file if you update the master artwork, then re-run the generator.

## Regenerate Android + web packs

Requires Python 3 with Pillow:

```bash
pip install pillow
python branding/scripts/generate_brand_assets.py
```

This updates:

- `app/src/main/res/mipmap-*` — launcher icons and adaptive foreground
- `branding/web-pack/` — website + favicon assets

## Web pack (`branding/web-pack/`)

| File | Use |
|------|-----|
| `mctb-logo-full-1024.png` | Full lockup, high resolution |
| `mctb-logo-full-512w.png` | Header / marketing (width 512px) |
| `mctb-symbol-1024.png` | Symbol-only (cropped from full art) |
| `mctb-symbol-512.png` / `mctb-symbol-256.png` | Avatars, compact UI |
| `favicon-32.png` / `favicon-16.png` | Classic favicons |
| `favicon-128.png` | PWA / larger tab icons |
| `apple-touch-icon-180.png` | iOS “Add to Home Screen” |

Pair with `website-brand-tokens.css` (repo root) for colors.

## Android

- Adaptive icon background: `values/colors.xml` → `ic_launcher_background` (`#111827`, matches brand).
- Foreground uses the **symbol-only** mark (shield + bubble + bolt), not the wordmark.

If the crop feels tight, edit `crop_symbol_square()` in `scripts/generate_brand_assets.py` and re-run.
