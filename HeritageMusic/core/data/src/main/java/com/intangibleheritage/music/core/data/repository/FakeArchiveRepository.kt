package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.ArchiveAssetType
import com.intangibleheritage.music.core.data.model.HeritageArchiveAsset
import com.intangibleheritage.music.core.resources.R

class FakeArchiveRepository : ArchiveRepository {

    private val assets: List<HeritageArchiveAsset> = listOf(
        HeritageArchiveAsset(
            id = "archive_video_001",
            title = "敦煌壁画乐舞影像采样",
            region = "甘肃敦煌",
            genre = "宫廷燕乐",
            era = "唐代",
            inheritor = "李青岚",
            sourcePath = "田野采集 -> 传承人初审 -> 学术复核 -> 发布",
            sourceTimeline = listOf(
                "2025-03-02 田野影像采集",
                "2025-03-08 传承人内容确认",
                "2025-03-15 学术委员会复核",
                "2025-03-20 平台标准化入库"
            ),
            copyrightStatus = "已签署授权书（演示）",
            credibilityScore = 92,
            type = ArchiveAssetType.Video,
            coverRes = R.drawable.music_banner_3,
            relatedStoryId = "s3",
            relatedTrackId = "track_ladies_music"
        ),
        HeritageArchiveAsset(
            id = "archive_audio_001",
            title = "丝路弦歌口传旋律录音",
            region = "新疆喀什",
            genre = "丝路器乐",
            era = "清末传谱",
            inheritor = "阿不都热合曼",
            sourcePath = "工作坊录音 -> 曲谱比对 -> 数据标准化 -> 发布",
            sourceTimeline = listOf(
                "2025-02-11 工作坊多轨录音",
                "2025-02-14 旋律片段切分",
                "2025-02-20 曲谱学者比对修订",
                "2025-02-26 资料库音频发布"
            ),
            copyrightStatus = "平台授权存档（演示）",
            credibilityScore = 89,
            type = ArchiveAssetType.Audio,
            coverRes = R.drawable.music_hot_elem_3,
            relatedStoryId = "s2",
            relatedTrackId = "track_wall_rhymes"
        ),
        HeritageArchiveAsset(
            id = "archive_text_001",
            title = "工尺谱与调式注解文档",
            region = "江苏苏州",
            genre = "江南丝竹",
            era = "民国整理本",
            inheritor = "顾清越",
            sourcePath = "馆藏扫描 -> OCR 清洗 -> 专家校勘 -> 入库",
            sourceTimeline = listOf(
                "2025-01-07 馆藏文档扫描",
                "2025-01-10 OCR 与人工校对",
                "2025-01-17 术语规范化",
                "2025-01-23 文档入库发布"
            ),
            copyrightStatus = "馆校共建授权（演示）",
            credibilityScore = 95,
            type = ArchiveAssetType.Text,
            coverRes = R.drawable.music_pick_elem_1,
            relatedStoryId = "s1",
            relatedTrackId = "track_echoes_east"
        ),
        HeritageArchiveAsset(
            id = "archive_inheritor_001",
            title = "传承人口述史：月琴与乡土仪式",
            region = "云南大理",
            genre = "民俗仪式音乐",
            era = "当代口述",
            inheritor = "段和音",
            sourcePath = "口述访谈 -> 视频转录 -> 事实核验 -> 发布",
            sourceTimeline = listOf(
                "2025-04-03 人物专访拍摄",
                "2025-04-05 口述内容转录",
                "2025-04-10 民俗专家核验",
                "2025-04-12 传承人库上线"
            ),
            copyrightStatus = "肖像及内容授权（演示）",
            credibilityScore = 90,
            type = ArchiveAssetType.Inheritor,
            coverRes = R.drawable.music_hot_elem_4,
            relatedStoryId = "s4",
            relatedTrackId = "track_echoes_east"
        ),
        HeritageArchiveAsset(
            id = "archive_audio_002",
            title = "编钟复原音色分轨样本",
            region = "湖北随州",
            genre = "礼乐编钟",
            era = "先秦复原",
            inheritor = "周礼文",
            sourcePath = "录音棚采样 -> 频谱校正 -> 元数据标注 -> 发布",
            sourceTimeline = listOf(
                "2025-02-02 编钟分层采样",
                "2025-02-06 频谱修复",
                "2025-02-09 标签与元数据录入",
                "2025-02-13 礼乐专题上架"
            ),
            copyrightStatus = "机构内部授权（演示）",
            credibilityScore = 94,
            type = ArchiveAssetType.Audio,
            coverRes = R.drawable.music_hot_elem_1,
            relatedStoryId = "s1",
            relatedTrackId = "track_echoes_east"
        ),
        HeritageArchiveAsset(
            id = "archive_video_002",
            title = "古琴减字谱教学示范片",
            region = "浙江杭州",
            genre = "古琴",
            era = "现代教学",
            inheritor = "沈墨",
            sourcePath = "课程拍摄 -> 章节拆分 -> 标签索引 -> 发布",
            sourceTimeline = listOf(
                "2025-03-10 课程录制",
                "2025-03-12 章节拆分与切片",
                "2025-03-18 教学审核",
                "2025-03-22 课程资料公开"
            ),
            copyrightStatus = "课程版权签约（演示）",
            credibilityScore = 91,
            type = ArchiveAssetType.Video,
            coverRes = R.drawable.music_pick_elem_3,
            relatedStoryId = "s2",
            relatedTrackId = "track_wall_rhymes"
        )
    )

    override fun allAssets(): List<HeritageArchiveAsset> = assets

    override fun assetById(id: String): HeritageArchiveAsset? = assets.firstOrNull { it.id == id }
}
