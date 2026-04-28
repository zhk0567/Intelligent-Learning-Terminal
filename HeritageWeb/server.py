# -*- coding: utf-8 -*-
"""
非遗音乐网页版 — 独立网页服务（多页面路由 + 静态资源）。
在 HeritageWeb 目录下执行: python server.py ，默认入口 http://127.0.0.1:9010/music
"""
from __future__ import annotations

import json
import os
import subprocess
import sys

import uvicorn
from pathlib import Path

from fastapi import FastAPI, Request
from fastapi.responses import HTMLResponse, JSONResponse, RedirectResponse
from fastapi.staticfiles import StaticFiles
from fastapi.templating import Jinja2Templates

# 与后端 API DTO 字段名（camelCase）一致，便于对接真实接口
MUSIC_HOME_DATA = {
    "banners": [
        {
            "id": "b1",
            "title": "钟鼓和鸣",
            "imageUrl": "https://picsum.photos/seed/heritage_b1/800/420",
            "audioTrackId": "track_echoes_east",
        },
        {
            "id": "b2",
            "title": "丝弦雅韵",
            "imageUrl": "https://picsum.photos/seed/heritage_b2/800/420",
            "audioTrackId": "track_wall_rhymes",
        },
        {
            "id": "b3",
            "title": "敦煌乐舞",
            "imageUrl": "https://picsum.photos/seed/heritage_b3/800/420",
            "audioTrackId": "track_ladies_music",
        },
    ],
    "hotTiles": [
        {
            "id": "h1",
            "title": "古韵新声",
            "imageUrl": "https://picsum.photos/seed/heritage_h1/400/400",
            "audioTrackId": "track_echoes_east",
        },
        {
            "id": "h2",
            "title": "壁画遗音",
            "imageUrl": "https://picsum.photos/seed/heritage_h2/400/400",
            "audioTrackId": "track_wall_rhymes",
        },
        {
            "id": "h3",
            "title": "丝路弦歌",
            "imageUrl": "https://picsum.photos/seed/heritage_h3/400/400",
            "audioTrackId": "track_ladies_music",
        },
        {
            "id": "h4",
            "title": "宫廷燕乐",
            "imageUrl": "https://picsum.photos/seed/heritage_h4/400/400",
            "audioTrackId": "track_echoes_east",
        },
    ],
    "dailyPicks": [
        {
            "id": "p1",
            "title": "东方回响",
            "imageUrl": "https://picsum.photos/seed/heritage_p1/640/360",
            "audioTrackId": "track_echoes_east",
        },
        {
            "id": "p2",
            "title": "绘壁遗韵",
            "imageUrl": "https://picsum.photos/seed/heritage_p2/640/360",
            "audioTrackId": "track_wall_rhymes",
        },
        {
            "id": "p3",
            "title": "仕女弄乐图",
            "imageUrl": "https://picsum.photos/seed/heritage_p3/640/360",
            "audioTrackId": "track_ladies_music",
        },
    ],
    "guessTags": [
        {"id": "t1", "label": "民俗节日"},
        {"id": "t2", "label": "非遗韵味"},
        {"id": "t3", "label": "小众民乐"},
    ],
    "bottomCards": [
        {
            "id": "x1",
            "title": "非遗跨界",
            "imageUrl": "https://picsum.photos/seed/heritage_x1/520/280",
        },
        {
            "id": "x2",
            "title": "基础学习",
            "imageUrl": "https://picsum.photos/seed/heritage_x2/520/280",
        },
    ],
}

COMMUNITY_POSTS_DATA = {
    "posts": [
        {
            "id": "c1",
            "title": "当非遗音乐碰上现代电子乐",
            "subtitle": "融合节奏与古调，社区热议中",
            "body": "我在排练厅尝试把古琴散板与电子底鼓叠在一起，意外得到了很有空间感的声场。后续会分享工程文件与分轨思路，欢迎同好一起实验。",
            "category": "FolkInstrument",
        },
        {
            "id": "c2",
            "title": "挖掘小众非遗音乐",
            "subtitle": "发现被忽略的民间曲调与传承人",
            "body": "走访西南山区时录到几段口传小调，准备整理成短纪录片配乐。若你有类似采风素材，欢迎留言交流版权与署名方式。",
            "category": "Electronic",
        },
        {
            "id": "c3",
            "title": "自制非遗音乐动画，邀你共赏",
            "subtitle": "用动画讲述非遗旋律的故事",
            "body": "用二维骨骼动画复现了一段皮影戏节奏，配乐用的是改编后的工尺谱旋律。渲染还在优化，先抛砖引玉。",
            "category": "Ai",
        },
        {
            "id": "c4",
            "title": "探访民间艺人，记录非遗音乐传承",
            "subtitle": "走访记录，留存声音与记忆",
            "body": "跟随非遗传承人学习月琴指法，用手机录了练习日志。希望更多年轻人愿意走进工作坊，而不只是线上观看。",
            "category": "FolkInstrument",
        },
    ]
}

# 演示音频直链（占位试听，可替换为自有 CDN）
AUDIO_DEMO = {
    "track_echoes_east": "https://upload.wikimedia.org/wikipedia/commons/0/04/Beethoven_Moonlight_1st_movement.ogg",
    "track_wall_rhymes": "https://upload.wikimedia.org/wikipedia/commons/e/e8/Toccata_and_Fugue_in_D_minor.ogg",
    "track_ladies_music": "https://upload.wikimedia.org/wikipedia/commons/6/6e/Jesu%2C_Joy_of_Man%27s_Desiring_%28Piano_arrangement%29.ogg",
}

# 故事瀑布流：推荐 / 关注双流演示数据
def _story_item(sid: str, overlay: str | None, min_h: int) -> dict[str, object]:
    return {
        "id": sid,
        "imageUrl": f"https://picsum.photos/seed/story_{sid}/400/{min_h + 120}",
        "overlay": overlay,
        "minHeight": min_h,
    }


STORIES_FEED_DATA = {
    "recommend": [
        _story_item("s1", None, 220),
        _story_item("s2", "烟火弦音", 160),
        _story_item("s3", None, 200),
        _story_item("s4", "SUPER CH", 140),
        _story_item("s5", None, 240),
        _story_item("s6", None, 150),
        _story_item("s7", None, 190),
        _story_item("s8", None, 170),
    ],
    "following": [
        _story_item("f1", "烟火弦音", 200),
        _story_item("f2", None, 180),
        _story_item("f3", "SUPER CH", 160),
    ],
}

# 商城：四分区与商品演示数据
MALL_CATALOG_DATA = {
    "sections": [
        {
            "id": "Story",
            "title": "非遗动画故事",
            "description": "围绕非遗元素创作动画内容，延展故事传播场景。",
        },
        {
            "id": "Cultural",
            "title": "非遗文创产品",
            "description": "以传统乐器与纹样为核心，打造日常可用文创。",
        },
        {
            "id": "Cross",
            "title": "非遗跨界活动",
            "description": "连接演出、展览与城市活动，提升品牌传播触达。",
        },
        {
            "id": "Instrument",
            "title": "非遗乐器商品",
            "description": "聚焦传统乐器周边与教学器具，服务学习与收藏。",
        },
    ],
    "products": [
        {
            "id": "dunhuang_magnet",
            "title": "敦煌乐器木制冰箱贴",
            "description": "精选敦煌壁画乐器形象，手工木质，可作收藏与家居点缀。",
            "priceYuan": 30,
            "rating": 4.9,
            "reviewCount": 12,
            "section": "Story",
            "imageUrl": "https://picsum.photos/seed/prod_dunhuang_magnet/360/360",
        },
        {
            "id": "story_shadow_charm",
            "title": "皮影纹样挂件",
            "description": "非遗皮影剪影元素亚克力挂件，轻巧便携。",
            "priceYuan": 42,
            "rating": 4.8,
            "reviewCount": 9,
            "section": "Story",
            "imageUrl": "https://picsum.photos/seed/prod_story_shadow/360/360",
        },
        {
            "id": "story_canvas_print",
            "title": "非遗故事帆布画",
            "description": "绢布微喷装裱，适合书房与展示墙。",
            "priceYuan": 158,
            "rating": 4.7,
            "reviewCount": 6,
            "section": "Story",
            "imageUrl": "https://picsum.photos/seed/prod_story_canvas/360/360",
        },
        {
            "id": "silk_scarf",
            "title": "丝路纹样丝巾",
            "description": "敦煌色系纹样，轻薄亲肤。",
            "priceYuan": 199,
            "rating": 4.8,
            "reviewCount": 21,
            "section": "Cultural",
            "imageUrl": "https://picsum.photos/seed/prod_silk_scarf/360/360",
        },
        {
            "id": "cultural_bookmark_set",
            "title": "纹样书签礼盒",
            "description": "四枚入礼盒，乐器纹样烫金。",
            "priceYuan": 56,
            "rating": 4.6,
            "reviewCount": 11,
            "section": "Cultural",
            "imageUrl": "https://picsum.photos/seed/prod_bookmarks/360/360",
        },
        {
            "id": "cultural_palace_lantern",
            "title": "竹骨纸艺小宫灯",
            "description": "手工拼接，暖光氛围摆件。",
            "priceYuan": 88,
            "rating": 4.7,
            "reviewCount": 7,
            "section": "Cultural",
            "imageUrl": "https://picsum.photos/seed/prod_lantern/360/360",
        },
        {
            "id": "bronze_bells",
            "title": "青铜编钟摆件",
            "description": "仿古青铜质感，书桌上的国风点缀。",
            "priceYuan": 128,
            "rating": 4.7,
            "reviewCount": 8,
            "section": "Cross",
            "imageUrl": "https://picsum.photos/seed/prod_bronze/360/360",
        },
        {
            "id": "cross_tote_bag",
            "title": "纹样双面帆布包",
            "description": "大容量通勤款，印花耐磨。",
            "priceYuan": 79,
            "rating": 4.5,
            "reviewCount": 18,
            "section": "Cross",
            "imageUrl": "https://picsum.photos/seed/prod_tote/360/360",
        },
        {
            "id": "cross_badge_set",
            "title": "城市联名徽章组",
            "description": "三枚胸针套装，收藏与搭配皆宜。",
            "priceYuan": 36,
            "rating": 4.6,
            "reviewCount": 24,
            "section": "Cross",
            "imageUrl": "https://picsum.photos/seed/prod_badge/360/360",
        },
        {
            "id": "pipa_bookmark",
            "title": "琵琶造型书签",
            "description": "金属镂空琵琶轮廓，阅读好伴侣。",
            "priceYuan": 25,
            "rating": 4.6,
            "reviewCount": 15,
            "section": "Instrument",
            "imageUrl": "https://picsum.photos/seed/prod_pipa/360/360",
        },
        {
            "id": "inst_score_notebook",
            "title": "古琴减字谱笔记本",
            "description": "线装便签，摘录练习要点。",
            "priceYuan": 49,
            "rating": 4.7,
            "reviewCount": 10,
            "section": "Instrument",
            "imageUrl": "https://picsum.photos/seed/prod_score_nb/360/360",
        },
        {
            "id": "inst_clip_tuner",
            "title": "调音夹式校音器",
            "description": "夹具稳固，适合多件弦乐器（演示）。",
            "priceYuan": 119,
            "rating": 4.8,
            "reviewCount": 33,
            "section": "Instrument",
            "imageUrl": "https://picsum.photos/seed/prod_tuner/360/360",
        },
    ],
}

# 资料库条目（演示）
ARCHIVE_ASSETS_DATA = {
    "items": [
        {
            "id": "archive_video_001",
            "title": "敦煌壁画乐舞影像采样",
            "region": "甘肃敦煌",
            "genre": "宫廷燕乐",
            "era": "唐代",
            "inheritor": "李青岚",
            "type": "Video",
            "typeLabel": "影像资料",
            "credibilityScore": 92,
            "copyrightStatus": "已签署授权书（演示）",
            "sourcePath": "田野采集 -> 传承人初审 -> 学术复核 -> 发布",
            "sourceTimeline": [
                "2025-03-02 田野影像采集",
                "2025-03-08 传承人内容确认",
                "2025-03-15 学术委员会复核",
                "2025-03-20 平台标准化入库",
            ],
            "relatedTrackId": "track_ladies_music",
            "coverImageUrl": "https://picsum.photos/seed/archive_v001/400/260",
        },
        {
            "id": "archive_audio_001",
            "title": "丝路弦歌口传旋律录音",
            "region": "新疆喀什",
            "genre": "丝路器乐",
            "era": "清末传谱",
            "inheritor": "阿不都热合曼",
            "type": "Audio",
            "typeLabel": "音频资料",
            "credibilityScore": 89,
            "copyrightStatus": "平台授权存档（演示）",
            "sourcePath": "工作坊录音 -> 曲谱比对 -> 数据标准化 -> 发布",
            "sourceTimeline": [
                "2025-02-11 工作坊多轨录音",
                "2025-02-14 旋律片段切分",
                "2025-02-20 曲谱学者比对修订",
                "2025-02-26 资料库音频发布",
            ],
            "relatedTrackId": "track_wall_rhymes",
            "coverImageUrl": "https://picsum.photos/seed/archive_a001/400/260",
        },
        {
            "id": "archive_text_001",
            "title": "工尺谱与调式注解文档",
            "region": "江苏苏州",
            "genre": "江南丝竹",
            "era": "民国整理本",
            "inheritor": "顾清越",
            "type": "Text",
            "typeLabel": "文字资料",
            "credibilityScore": 95,
            "copyrightStatus": "馆校共建授权（演示）",
            "sourcePath": "馆藏扫描 -> OCR 清洗 -> 专家校勘 -> 入库",
            "sourceTimeline": [
                "2025-01-07 馆藏文档扫描",
                "2025-01-10 OCR 与人工校对",
                "2025-01-17 术语规范化",
                "2025-01-23 文档入库发布",
            ],
            "relatedTrackId": "track_echoes_east",
            "coverImageUrl": "https://picsum.photos/seed/archive_t001/400/260",
        },
        {
            "id": "archive_inheritor_001",
            "title": "传承人口述史：月琴与乡土仪式",
            "region": "云南大理",
            "genre": "民俗仪式音乐",
            "era": "当代口述",
            "inheritor": "段和音",
            "type": "Inheritor",
            "typeLabel": "传承人库",
            "credibilityScore": 90,
            "copyrightStatus": "肖像及内容授权（演示）",
            "sourcePath": "口述访谈 -> 视频转录 -> 事实核验 -> 发布",
            "sourceTimeline": [
                "2025-04-03 人物专访拍摄",
                "2025-04-05 口述内容转录",
                "2025-04-10 民俗专家核验",
                "2025-04-12 传承人库上线",
            ],
            "relatedTrackId": "track_echoes_east",
            "coverImageUrl": "https://picsum.photos/seed/archive_i001/400/260",
        },
        {
            "id": "archive_audio_002",
            "title": "编钟复原音色分轨样本",
            "region": "湖北随州",
            "genre": "礼乐编钟",
            "era": "先秦复原",
            "inheritor": "周礼文",
            "type": "Audio",
            "typeLabel": "音频资料",
            "credibilityScore": 94,
            "copyrightStatus": "机构内部授权（演示）",
            "sourcePath": "录音棚采样 -> 频谱校正 -> 元数据标注 -> 发布",
            "sourceTimeline": [
                "2025-02-02 编钟分层采样",
                "2025-02-06 频谱修复",
                "2025-02-09 标签与元数据录入",
                "2025-02-13 礼乐专题上架",
            ],
            "relatedTrackId": "track_echoes_east",
            "coverImageUrl": "https://picsum.photos/seed/archive_a002/400/260",
        },
        {
            "id": "archive_video_002",
            "title": "古琴减字谱教学示范片",
            "region": "浙江杭州",
            "genre": "古琴",
            "era": "现代教学",
            "inheritor": "沈墨",
            "type": "Video",
            "typeLabel": "影像资料",
            "credibilityScore": 91,
            "copyrightStatus": "课程版权签约（演示）",
            "sourcePath": "课程拍摄 -> 章节拆分 -> 标签索引 -> 发布",
            "sourceTimeline": [
                "2025-03-10 课程录制",
                "2025-03-12 章节拆分与切片",
                "2025-03-18 教学审核",
                "2025-03-22 课程资料公开",
            ],
            "relatedTrackId": "track_wall_rhymes",
            "coverImageUrl": "https://picsum.photos/seed/archive_v002/400/260",
        },
    ]
}

# 课程列表（演示）
COURSES_DATA = {
    "courses": [
        {
            "id": "course_basic_guqin",
            "title": "古琴入门：减字谱与指法基础",
            "tutorName": "沈墨",
            "level": "Basic",
            "levelLabel": "基础",
            "lessons": 12,
            "summary": "面向零基础学习者，建立古琴基本手型、节奏和减字谱识读能力。",
            "goals": ["认识减字谱", "掌握基础指法", "完成 1 首入门曲"],
            "copyrightNote": "课程内容版权归平台与传承人共同所有（演示）",
            "relatedTrackId": "track_echoes_east",
            "coverImageUrl": "https://picsum.photos/seed/course_guqin/400/240",
        },
        {
            "id": "course_advanced_dizi",
            "title": "笛乐进阶：吐音与风格化表达",
            "tutorName": "李青岚",
            "level": "Advanced",
            "levelLabel": "进阶",
            "lessons": 18,
            "summary": "面向有基础学习者，强化吐音、气息控制和区域风格演奏。",
            "goals": ["提升吐音清晰度", "掌握装饰音", "完成风格化演奏片段"],
            "copyrightNote": "课程内容已签署授权与传播协议（演示）",
            "relatedTrackId": "track_ladies_music",
            "coverImageUrl": "https://picsum.photos/seed/course_dizi/400/240",
        },
        {
            "id": "course_research_bells",
            "title": "礼乐研究：编钟复原与音色分析",
            "tutorName": "周礼文",
            "level": "Research",
            "levelLabel": "研究",
            "lessons": 10,
            "summary": "面向高校研究阶段，聚焦编钟音色复原与谱例结构分析。",
            "goals": ["理解礼乐语境", "掌握频谱分析方法", "完成一篇小型研究报告"],
            "copyrightNote": "课程研究资料仅限教学用途，禁止二次传播（演示）",
            "relatedTrackId": "track_wall_rhymes",
            "coverImageUrl": "https://picsum.photos/seed/course_bells/400/240",
        },
    ]
}

# 互动中心模块文案（演示）
INTERACTIVE_HUB_DATA = {
    "title": "非遗交互体验",
    "subtitle": "包含智能编曲与 AI 导师点评两条演示链路，支持从输入到结果的完整闭环。",
    "modules": [
        {
            "id": "compose",
            "title": "智能编曲（MVP）",
            "description": "输入风格、情绪、节奏参数，生成演示片段并提供试听/收藏按钮。",
            "actionLabel": "进入编曲",
            "hint": "当前为模板生成版本，后续可替换为真实模型服务。",
        },
        {
            "id": "review",
            "title": "AI 导师点评（MVP）",
            "description": "输入练习音频与关注维度，输出多维评分与改进建议。",
            "actionLabel": "进入点评",
            "hint": "当前为规则引擎点评版本，结果用于演示流程。",
        },
    ],
}


# 网页独立部署：多 URL 页面 + static/heritage.* + templates/app.html
_DIR = Path(__file__).resolve().parent
templates = Jinja2Templates(directory=str(_DIR / "templates"))

PAGE_LABELS: dict[str, str] = {
    "music": "音乐馆",
    "stories": "故事",
    "community": "创作社区",
    "mall": "商城",
    "profile": "我的",
}


def _page_response(request: Request, page: str):
    if page not in PAGE_LABELS:
        return RedirectResponse(url="/music", status_code=302)
    audio_js = json.dumps(AUDIO_DEMO, ensure_ascii=False)
    return templates.TemplateResponse(
        request,
        "app.html",
        {
            "request": request,
            "page": page,
            "page_title": PAGE_LABELS[page] + " — 非遗音乐（网页演示）",
            "header_label": PAGE_LABELS[page],
            "audio_demo_json": audio_js,
        },
    )



app = FastAPI(title="非遗音乐网页版", version="0.1.0")

app.mount("/static", StaticFiles(directory=str(_DIR / "static")), name="static")


@app.get("/", include_in_schema=False)
def root():
    return RedirectResponse(url="/music", status_code=302)


@app.get("/music", response_class=HTMLResponse, include_in_schema=False)
def page_music(request: Request):
    return _page_response(request, "music")


@app.get("/stories", response_class=HTMLResponse, include_in_schema=False)
def page_stories(request: Request):
    return _page_response(request, "stories")


@app.get("/community", response_class=HTMLResponse, include_in_schema=False)
def page_community(request: Request):
    return _page_response(request, "community")


@app.get("/mall", response_class=HTMLResponse, include_in_schema=False)
def page_mall(request: Request):
    return _page_response(request, "mall")


@app.get("/profile", response_class=HTMLResponse, include_in_schema=False)
def page_profile(request: Request):
    return _page_response(request, "profile")


@app.get("/music/home")
@app.get("/api/music/home")
def music_home() -> JSONResponse:
    return JSONResponse(MUSIC_HOME_DATA)


@app.get("/community/posts")
@app.get("/api/community/posts")
def community_posts() -> JSONResponse:
    return JSONResponse(COMMUNITY_POSTS_DATA)


@app.get("/stories/feed")
@app.get("/api/stories/feed")
def stories_feed(tab: str = "recommend") -> JSONResponse:
    key = "following" if tab.strip().lower() == "following" else "recommend"
    items = STORIES_FEED_DATA.get(key) or []
    return JSONResponse({"tab": key, "items": items})


@app.get("/mall/catalog")
@app.get("/api/mall/catalog")
def mall_catalog() -> JSONResponse:
    return JSONResponse(MALL_CATALOG_DATA)


@app.get("/archive/assets")
@app.get("/api/archive/assets")
def archive_assets() -> JSONResponse:
    return JSONResponse(ARCHIVE_ASSETS_DATA)


@app.get("/courses")
@app.get("/api/courses")
def courses_list() -> JSONResponse:
    return JSONResponse(COURSES_DATA)


@app.get("/interactive/hub")
@app.get("/api/interactive/hub")
def interactive_hub() -> JSONResponse:
    return JSONResponse(INTERACTIVE_HUB_DATA)


# 默认不使用 8000；可通过环境变量 HERITAGE_WEB_PORT 覆盖
_DEFAULT_HOST = "127.0.0.1"
_DEFAULT_PORT = 9010


def _netstat_creationflags() -> int:
    if sys.platform == "win32" and hasattr(subprocess, "CREATE_NO_WINDOW"):
        return subprocess.CREATE_NO_WINDOW
    return 0


def _pids_listening_on_port_windows(port: int) -> list[int]:
    """解析 netstat -ano，返回在指定端口处于 LISTENING 的 PID 列表。"""
    result = subprocess.run(
        ["netstat", "-ano"],
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
        creationflags=_netstat_creationflags(),
        check=False,
    )
    pids: list[int] = []
    port_suffix = ":" + str(port)
    for raw in result.stdout.splitlines():
        line = raw.strip()
        if not line.upper().startswith("TCP"):
            continue
        if "LISTENING" not in line and "侦听" not in line:
            continue
        parts = line.split()
        if len(parts) < 5:
            continue
        local = parts[1]
        if not local.endswith(port_suffix):
            continue
        host, _, tail = local.rpartition(":")
        if not host or tail != str(port):
            continue
        try:
            pid = int(parts[-1])
        except ValueError:
            continue
        if pid > 0:
            pids.append(pid)
    # 去重
    seen: set[int] = set()
    out: list[int] = []
    for p in pids:
        if p not in seen:
            seen.add(p)
            out.append(p)
    return out


def _kill_pids_on_port_windows(port: int) -> None:
    """若端口被占用，结束占用该端口的进程（当前进程除外）。"""
    pids = _pids_listening_on_port_windows(port)
    me = os.getpid()
    for pid in pids:
        if pid == me:
            continue
        print("端口 " + str(port) + " 已被占用，正结束进程 PID " + str(pid) + " …", flush=True)
        subprocess.run(
            ["taskkill", "/F", "/PID", str(pid)],
            capture_output=True,
            text=True,
            encoding="utf-8",
            errors="replace",
            creationflags=_netstat_creationflags(),
            check=False,
        )


def _resolve_port() -> int:
    raw = os.environ.get("HERITAGE_WEB_PORT", str(_DEFAULT_PORT)).strip()
    try:
        p = int(raw)
    except ValueError:
        p = _DEFAULT_PORT
    if p < 1 or p > 65535:
        p = _DEFAULT_PORT
    return p


if __name__ == "__main__":
    host = os.environ.get("HERITAGE_WEB_HOST", _DEFAULT_HOST).strip() or _DEFAULT_HOST
    port = _resolve_port()
    if sys.platform == "win32":
        _kill_pids_on_port_windows(port)
    else:
        print("非 Windows 系统：请自行释放端口 " + str(port) + " 后再启动。", flush=True)
    print("非遗音乐网页版: http://" + host + ":" + str(port) + "/music", flush=True)
    uvicorn.run(app, host=host, port=port, access_log=False)
