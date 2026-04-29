package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.ArchiveAssetType
import com.intangibleheritage.music.core.data.model.HeritageArchiveAsset
import com.intangibleheritage.music.core.resources.R

class FakeArchiveRepository : ArchiveRepository {

    private val assets: List<HeritageArchiveAsset> = listOf(
        HeritageArchiveAsset(
            id = "archive_henan_zhuizi_001",
            title = "河南坠子《包公案》",
            region = "河南开封",
            genre = "河南坠子（传统曲艺）",
            era = "当代演绎（传统书目）",
            inheritor = "陈胜利（资料来源）",
            sourcePath = "data/乐器/河南坠子/《包公案》.mp4",
            sourceTimeline = listOf(
                "2026-04-29 数据整理：河南坠子结构化建档",
                "2026-04-29 媒体文件校验：包公案视频可用",
                "2026-04-29 条目替换：资料库采用真实数据"
            ),
            copyrightStatus = "内部整理素材（待补充授权信息）",
            credibilityScore = 88,
            type = ArchiveAssetType.Video,
            coverRes = R.drawable.music_banner_3,
            relatedStoryId = "s3",
            relatedTrackId = "track_echoes_east"
        ),
        HeritageArchiveAsset(
            id = "archive_qinyang_suona_001",
            title = "沁阳唢呐《抬花轿》合奏录音",
            region = "河南焦作沁阳",
            genre = "沁阳唢呐（传统音乐）",
            era = "当代录音",
            inheritor = "李金海（资料来源）",
            sourcePath = "data/乐器/沁阳唢呐/李金海唢呐合奏《抬花轿》.mp3",
            sourceTimeline = listOf(
                "2026-04-29 数据整理：沁阳唢呐结构化建档",
                "2026-04-29 媒体文件校验：抬花轿音频可用",
                "2026-04-29 条目替换：资料库采用真实数据"
            ),
            copyrightStatus = "内部整理素材（待补充授权信息）",
            credibilityScore = 90,
            type = ArchiveAssetType.Audio,
            coverRes = R.drawable.music_hot_elem_3,
            relatedStoryId = "s2",
            relatedTrackId = "track_wall_rhymes"
        ),
        HeritageArchiveAsset(
            id = "archive_sipingdiao_001",
            title = "四平调《陈三两爬堂》",
            region = "河南商丘",
            genre = "四平调（地方戏曲）",
            era = "当代演绎（传统剧目）",
            inheritor = "付梅（资料来源）",
            sourcePath = "data/乐器/四平调/《陈三两爬堂》.mp4",
            sourceTimeline = listOf(
                "2026-04-29 数据整理：四平调结构化建档",
                "2026-04-29 媒体文件校验：代表剧目音视频可用",
                "2026-04-29 条目替换：资料库采用真实数据"
            ),
            copyrightStatus = "内部整理素材（待补充授权信息）",
            credibilityScore = 89,
            type = ArchiveAssetType.Video,
            coverRes = R.drawable.music_pick_elem_1,
            relatedStoryId = "s1",
            relatedTrackId = "track_ladies_music"
        ),
        HeritageArchiveAsset(
            id = "archive_jiuliandeng_001",
            title = "九莲灯现场资料（多媒体）",
            region = "河南（待复核）",
            genre = "九莲灯（传统表演艺术）",
            era = "当代采集",
            inheritor = "待补充",
            sourcePath = "data/乐器/九莲灯/{1.mp4,2.mp4,3.mp4,九莲灯.mp3,醉美玉见.mp3,图片1.png}",
            sourceTimeline = listOf(
                "2026-04-29 媒体文件整理：音视频与图片已提取",
                "2026-04-29 文本状态：缺少可用文字资料",
                "2026-04-29 条目替换：先按多媒体条目入库"
            ),
            copyrightStatus = "内部整理素材（待补充授权信息）",
            credibilityScore = 76,
            type = ArchiveAssetType.Text,
            coverRes = R.drawable.music_hot_elem_4,
            relatedStoryId = "s4",
            relatedTrackId = "track_echoes_east"
        )
    )

    override fun allAssets(): List<HeritageArchiveAsset> = assets

    override fun assetById(id: String): HeritageArchiveAsset? = assets.firstOrNull { it.id == id }
}
