# -*- coding: utf-8 -*-
"""从 `data/故事/*.txt` 生成 `miniprogram/data/story.ts`、`web/src/data/story.ts`、`StoriesData.kt`。

每篇 `.txt`：第一行为标题，其余为正文（段间空行保留为 \\n\\n）。元数据（作者、分类、标签、互动数等）见下方 `META`。
"""
from __future__ import annotations

import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "data" / "故事"
COVER_IMG_DIR = ROOT / "data" / "图片" / "故事"
OUT_MP = ROOT / "miniprogram" / "data" / "story.ts"
OUT_WEB = ROOT / "web" / "src" / "data" / "story.ts"
OUT_KT = ROOT / "app" / "app" / "src" / "main" / "java" / "com" / "guyunxinchuan" / "heritage" / "music" / "StoriesData.kt"

# 列表顺序 = 各端故事 Tab「推荐」流中展示顺序
STEM_ORDER = [
    "河南坠子",
    "古琴",
    "四平调",
    "河洛大鼓",
    "九莲灯",
    "濮阳大弦戏",
    "箜篌艺术",
]

STEM_TO_KEY = {
    "河南坠子": "henan_zhuizi",
    "古琴": "guqin",
    "四平调": "siping_diao",
    "河洛大鼓": "heluo_dagu",
    "九莲灯": "jiulian_deng",
    "濮阳大弦戏": "puyang_daxianxi",
    "箜篌艺术": "konghou_yishu",
}

META: dict[str, dict] = {
    "henan_zhuizi": {
        "author": "陈胜利 · 第五批国家级非遗河南坠子代表性传承人",
        "category": "曲艺",
        "tags": ("河南坠子", "曲艺", "坠胡", "中原非遗"),
        "publishTime": "2024-04-22",
        "likeCount": 1267,
        "commentCount": 92,
        "readCount": 8540,
    },
    "guqin": {
        "author": "王光磊 · 浙派徐门第四代嫡传",
        "category": "古琴",
        "tags": ("古琴", "浙派", "雁山堂", "传统音乐"),
        "publishTime": "2024-04-19",
        "likeCount": 1132,
        "commentCount": 76,
        "readCount": 7320,
    },
    "siping_diao": {
        "author": "付梅 · 国家级非遗四平调代表性传承人",
        "category": "戏曲",
        "tags": ("四平调", "戏曲", "商丘", "中原非遗"),
        "publishTime": "2024-04-15",
        "likeCount": 998,
        "commentCount": 64,
        "readCount": 6850,
    },
    "heluo_dagu": {
        "author": "河洛文化研究 · 编辑部",
        "category": "曲艺",
        "tags": ("河洛大鼓", "说唱", "洛阳", "中原非遗"),
        "publishTime": "2024-04-10",
        "likeCount": 824,
        "commentCount": 51,
        "readCount": 5620,
    },
    "jiulian_deng": {
        "author": "民俗影像志 · 田野记述",
        "category": "民间舞蹈",
        "tags": ("九莲灯", "民间舞蹈", "元宵", "上党八音"),
        "publishTime": "2024-04-05",
        "likeCount": 712,
        "commentCount": 43,
        "readCount": 4980,
    },
    "puyang_daxianxi": {
        "author": "戴建平 · 国家级非遗大弦戏代表性传承人",
        "category": "戏曲",
        "tags": ("大弦戏", "锡笛", "唐宋古曲", "中原非遗"),
        "publishTime": "2024-04-02",
        "likeCount": 658,
        "commentCount": 37,
        "readCount": 4420,
    },
    "konghou_yishu": {
        "author": "鲁璐 · 河南省非遗箜篌艺术代表性传承人",
        "category": "古乐器",
        "tags": ("箜篌", "古乐器", "雅乐", "久鼎空侯博物馆"),
        "publishTime": "2024-03-28",
        "likeCount": 1184,
        "commentCount": 88,
        "readCount": 7860,
    },
}

STORY_CATEGORIES = [
    "推荐",
    "戏曲",
    "曲艺",
    "古琴",
    "古乐器",
    "民间舞蹈",
]


def _ensure_pillow():
    try:
        from PIL import Image  # noqa: F401
    except ImportError:
        import subprocess

        subprocess.check_call([sys.executable, "-m", "pip", "install", "pillow", "-q"])


def read_cover_dimensions(stem: str) -> tuple[int, int]:
    """读取 `data/图片/故事` 下与正文同名的封面像素尺寸，供各端保持与原图一致的长宽比。"""
    if not COVER_IMG_DIR.is_dir():
        return 1600, 900
    for ext in (".png", ".jpg", ".jpeg", ".webp"):
        p = COVER_IMG_DIR / f"{stem}{ext}"
        if not p.is_file():
            continue
        try:
            _ensure_pillow()
            from PIL import Image

            with Image.open(p) as im:
                w, h = im.size
            if w > 0 and h > 0:
                return w, h
        except OSError:
            continue
    return 1600, 900


def read_story_txt(path: Path) -> tuple[str, str]:
    raw = path.read_text(encoding="utf-8").strip("\ufeff").strip()
    if not raw:
        return "", ""
    all_lines = raw.splitlines()
    title = all_lines[0].strip()
    # 源文件多为「一行一段」；段间用空行或换行分隔，统一为 \n\n
    paras = [ln.strip() for ln in all_lines[1:] if ln.strip()]
    body = "\n\n".join(paras) if paras else title
    return title, body


def excerpt_from_body(body: str, max_len: int = 220) -> str:
    first = body.split("\n\n")[0].strip()
    if len(first) <= max_len:
        return first
    cut = first[: max_len - 1].rstrip()
    # 避免半个词太难看：尽量在句号处截
    for sep in ("。", "；", "，", " "):
        i = cut.rfind(sep)
        if i > max_len // 2:
            cut = cut[: i + 1]
            break
    return cut + "…"


def ts_escape(s: str) -> str:
    return s.replace("\\", "\\\\").replace("`", "\\`").replace("${", "\\${")


def ts_str(s: str) -> str:
    return "`" + ts_escape(s) + "`"


def kotlin_escape_raw(s: str) -> str:
    # Kotlin 多行 raw string 中 `$` 会触发插值，写成 ${'$'}
    return s.replace("$", "${'$'}")


def kotlin_triple(s: str) -> str:
    s2 = kotlin_escape_raw(s)
    if '"""' in s2:
        s2 = s2.replace('"""', '\\"\\"\\"')
    return '"""' + s2 + '"""'


def build_entries() -> list[dict]:
    entries: list[dict] = []
    for stem in STEM_ORDER:
        key = STEM_TO_KEY[stem]
        path = SRC / f"{stem}.txt"
        if not path.is_file():
            print("missing txt:", path, file=sys.stderr)
            sys.exit(1)
        title, body = read_story_txt(path)
        m = META[key]
        excerpt = excerpt_from_body(body)
        cw, ch = read_cover_dimensions(stem)
        entries.append(
            {
                "id": f"story_{key}",
                "key": key,
                "title": title,
                "author": m["author"],
                "category": m["category"],
                "excerpt": excerpt,
                "body": body,
                "publishTime": m["publishTime"],
                "likeCount": m["likeCount"],
                "commentCount": m["commentCount"],
                "readCount": m["readCount"],
                "tags": list(m["tags"]),
                "coverWidth": cw,
                "coverHeight": ch,
            }
        )
    return entries


def emit_ts(entries: list[dict], path: Path):
    lines: list[str] = []
    lines.append("/**")
    lines.append(" * AUTO-GENERATED by `tools/sync_stories_data.py` from `data/故事/*.txt`.")
    lines.append(" * 不要手工编辑：修改正文请改源 .txt 或 `tools/sync_stories_data.py` 内 META，运行脚本重新生成。")
    lines.append(" */")
    lines.append("")
    lines.append("export interface Story {")
    lines.append("  id: string;")
    lines.append("  title: string;")
    lines.append("  author: string;")
    lines.append("  category: string;")
    lines.append("  /** 卡片摘要（来自正文首段，可放心截断） */")
    lines.append("  excerpt: string;")
    lines.append("  /** 完整正文（段落以 \\n\\n 分隔） */")
    lines.append("  body: string;")
    lines.append("  publishTime: string;")
    lines.append("  likeCount: number;")
    lines.append("  commentCount: number;")
    lines.append("  readCount: number;")
    lines.append("  tags: string[];")
    lines.append("  isLiked?: boolean;")
    lines.append("  isSaved?: boolean;")
    lines.append("  coverSrc: string;")
    lines.append("  /** 封面原图像素宽，与 `data/图片/故事` 同源，用于保持展示比例 */")
    lines.append("  coverWidth: number;")
    lines.append("  /** 封面原图像素高 */")
    lines.append("  coverHeight: number;")
    lines.append("}")
    lines.append("")
    cats = ", ".join(f'"{c}"' for c in STORY_CATEGORIES)
    lines.append(f"export const STORY_CATEGORIES = [{cats}] as const;")
    lines.append("")
    lines.append("export const STORIES: Story[] = [")
    for e in entries:
        tags_js = ", ".join(json.dumps(t, ensure_ascii=False) for t in e["tags"])
        lines.append("  {")
        lines.append(f'    id: "{e["id"]}",')
        lines.append(f"    title: {ts_str(e['title'])}, ")
        lines.append(f"    author: {ts_str(e['author'])}, ")
        lines.append(f'    category: "{e["category"]}", ')
        lines.append(f"    excerpt: {ts_str(e['excerpt'])}, ")
        lines.append(f"    body: {ts_str(e['body'])}, ")
        lines.append(f'    publishTime: "{e["publishTime"]}", ')
        lines.append(f"    likeCount: {e['likeCount']}, ")
        lines.append(f"    commentCount: {e['commentCount']}, ")
        lines.append(f"    readCount: {e['readCount']}, ")
        lines.append(f"    tags: [{tags_js}], ")
        lines.append(f'    coverSrc: "/images/story/{e["key"]}.jpg",')
        lines.append(f"    coverWidth: {e['coverWidth']},")
        lines.append(f"    coverHeight: {e['coverHeight']},")
        lines.append("  },")
    lines.append("];")
    lines.append("")
    lines.append("export function storyById(id: string): Story | undefined {")
    lines.append("  return STORIES.find((s) => s.id === id);")
    lines.append("}")
    lines.append("")
    path.write_text("\n".join(lines), encoding="utf-8")


def emit_kt(entries: list[dict], path: Path):
    lines: list[str] = []
    lines.append("package com.guyunxinchuan.heritage.music")
    lines.append("")
    lines.append("// AUTO-GENERATED by `tools/sync_stories_data.py` from `data/故事/*.txt`.")
    lines.append("// 不要手工编辑：修改正文请改源 .txt 或 `tools/sync_stories_data.py` 内 META，运行脚本重新生成。")
    lines.append("")
    lines.append("data class StoryEntry(")
    lines.append('    val id: String,')
    lines.append('    /** 与 `R.drawable.story_cover_<key>` 同名，便于跨端定位 */')
    lines.append("    val key: String,")
    lines.append("    val title: String,")
    lines.append("    val author: String,")
    lines.append("    val category: String,")
    lines.append("    val excerpt: String,")
    lines.append("    val body: String,")
    lines.append("    val publishTime: String,")
    lines.append("    val likeCount: Int,")
    lines.append("    val commentCount: Int,")
    lines.append("    val readCount: Int,")
    lines.append("    val tags: List<String>,")
    lines.append("    /** 封面原图像素尺寸，与 `data/图片/故事` 同源，用于列表/瀑布流保持长宽比 */")
    lines.append("    val coverWidth: Int,")
    lines.append("    val coverHeight: Int,")
    lines.append("    val coverResId: Int,")
    lines.append(")")
    lines.append("")
    lines.append("object StoriesData {")
    lines.append("    /** 故事分类标签（首页 Tab 顺序与之保持一致） */")
    cats = ", ".join(f'"{c}"' for c in STORY_CATEGORIES)
    lines.append(f"    val CATEGORIES: List<String> = listOf({cats})")
    lines.append("")
    lines.append("    val ALL: List<StoryEntry> = listOf(")
    for e in entries:
        tags_kt = ", ".join(f'"{t}"' for t in e["tags"])
        lines.append("        StoryEntry(")
        lines.append(f'            id = "{e["id"]}",')
        lines.append(f'            key = "{e["key"]}",')
        lines.append(f"            title = {kotlin_triple(e['title'])},")
        lines.append(f"            author = {kotlin_triple(e['author'])},")
        lines.append(f'            category = "{e["category"]}",')
        lines.append(f"            excerpt = {kotlin_triple(e['excerpt'])},")
        lines.append(f"            body = {kotlin_triple(e['body'])},")
        lines.append(f'            publishTime = "{e["publishTime"]}",')
        lines.append(f"            likeCount = {e['likeCount']},")
        lines.append(f"            commentCount = {e['commentCount']},")
        lines.append(f"            readCount = {e['readCount']},")
        lines.append(f"            tags = listOf({tags_kt}),")
        lines.append(f"            coverWidth = {e['coverWidth']},")
        lines.append(f"            coverHeight = {e['coverHeight']},")
        lines.append(f"            coverResId = R.drawable.story_cover_{e['key']},")
        lines.append("        ),")
    lines.append("    )")
    lines.append("}")
    lines.append("")
    path.write_text("\n".join(lines), encoding="utf-8")


def main():
    if not SRC.is_dir():
        print("missing", SRC)
        return 1
    entries = build_entries()
    emit_ts(entries, OUT_MP)
    emit_ts(entries, OUT_WEB)
    emit_kt(entries, OUT_KT)
    print("ok:", len(entries), "stories ->", OUT_MP.name, OUT_WEB.relative_to(ROOT), OUT_KT.relative_to(ROOT))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
