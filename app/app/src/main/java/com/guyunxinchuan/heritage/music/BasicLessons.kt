package com.guyunxinchuan.heritage.music

/**
 * 「基础学习」视频课。
 * - 与小程序 / Web 的 `BASIC_LESSONS`（`miniprogram/data/lessons.ts`、`web/src/data/lessons.ts`）字段一致。
 * - 视频较大（约 50–90MB），未拷入 `res/raw`；通过 [LESSON_VIDEO_BASE] 引用同一 Web 静态/CDN URL。
 *   开发期默认指向本机 Vite Dev（`http://10.0.2.2:5173` 在 Android 模拟器内对应宿主 `localhost:5173`）。
 *   上线时把 [LESSON_VIDEO_BASE] 改为正式 CDN 即可。
 */
data class BasicLesson(
    val id: String,
    val title: String,
    val artist: String,
    val desc: String,
    val longDesc: String,
    val durationSec: Int,
    val coverResId: Int,
    // 相对路径，与 Web `web/public/video/basic_lesson/*.mp4` 同名。
    val videoPath: String,
)

object BasicLessons {
    const val LESSON_VIDEO_BASE: String = "http://10.0.2.2:5173"

    val ALL: List<BasicLesson> = listOf(
        BasicLesson(
            id = "lesson_1",
            title = "乡村振兴 福满人间",
            artist = "河洛大鼓",
            desc = "国家级非遗 · 河洛大鼓新编曲目",
            longDesc =
                "河洛大鼓发源于河南偃师，兴于巩义，流行于洛阳、孟津、登封等地，是以说唱叙事为表演形式的传统曲艺。" +
                "2006 年 5 月经国务院批准，被列入第一批国家级非物质文化遗产名录（编号 Ⅴ-12）。" +
                "本曲目《乡村振兴 福满人间》为河洛大鼓代表性传承人创新创作，以乡音乡韵讲述当代河南乡村振兴故事，" +
                "将百年传统大鼓与时代主题结合，是非遗活态传承的典型范例。",
            durationSec = 285,
            coverResId = R.drawable.basic_lesson_cover_01,
            videoPath = "/video/basic_lesson/basic_lesson_01.mp4",
        ),
        BasicLesson(
            id = "lesson_2",
            title = "小包公",
            artist = "四平调",
            desc = "国家级非遗 · 商丘四平调代表剧目",
            longDesc =
                "四平调由豫东花鼓演变而来，1931 年正式定名，流行于豫、鲁、苏、皖四省交界地带。" +
                "2006 年 5 月与河洛大鼓同批列入第一批国家级非物质文化遗产名录。" +
                "《小包公》与《陈三两爬堂》《哑女告状》并称四平调最具影响力的三大代表剧目，" +
                "由商丘市四平调剧团（被誉为「天下第一团」）创排演出，因唱腔平易近人、剧情风趣劝善而深受观众喜爱，" +
                "也是了解四平调声腔与表演程式的入门佳作。",
            durationSec = 120,
            coverResId = R.drawable.basic_lesson_cover_02,
            videoPath = "/video/basic_lesson/basic_lesson_02.mp4",
        ),
    )

    fun byId(id: String): BasicLesson? = ALL.firstOrNull { it.id == id }

    fun videoUrl(lesson: BasicLesson): String = LESSON_VIDEO_BASE + lesson.videoPath
}
