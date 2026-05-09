package com.guyunxinchuan.heritage.music

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

/**
 * 「基础学习」视频课页：使用 [VideoView] + [MediaController] 播放远程 mp4。
 * 视频 URL 由 [BasicLessons.videoUrl] 拼接：`gradle.properties` 中 `STATIC_ASSET_ORIGIN` 非空时用 CDN，否则用 [BasicLessons.LESSON_VIDEO_BASE]（开发机 Vite）。
 */
class LessonActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LESSON_ID = "extra_lesson_id"
    }

    private lateinit var videoView: VideoView
    private lateinit var loading: ProgressBar
    private var savedPositionMs: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)

        val id = intent.getStringExtra(EXTRA_LESSON_ID) ?: BasicLessons.ALL.first().id
        val lesson = BasicLessons.byId(id)
        if (lesson == null) {
            Toast.makeText(this, "课程不存在", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<ImageButton>(R.id.lesson_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        findViewById<TextView>(R.id.lesson_title).text = lesson.title
        findViewById<TextView>(R.id.lesson_meta).text = "${lesson.artist} · 时长 ${formatDur(lesson.durationSec)}"
        findViewById<TextView>(R.id.lesson_tag).text = lesson.desc
        findViewById<TextView>(R.id.lesson_long_desc).text = lesson.longDesc

        val poster = findViewById<ImageView>(R.id.lesson_poster)
        val coverIdx = BasicLessons.indexOf(lesson)
        poster.loadCoverRemoteOrDrawable(
            StaticRemoteAssets.basicLessonCover(coverIdx),
            lesson.coverResId,
            CoverPreset.Banner,
        )
        poster.visibility = View.VISIBLE

        videoView = findViewById(R.id.lesson_video)
        loading = findViewById(R.id.lesson_loading)
        val controller = MediaController(this).apply { setAnchorView(videoView) }
        videoView.setMediaController(controller)
        videoView.setVideoURI(Uri.parse(BasicLessons.videoUrl(lesson)))
        videoView.setOnPreparedListener { mp: MediaPlayer ->
            loading.visibility = View.GONE
            poster.visibility = View.GONE
            mp.isLooping = false
            if (savedPositionMs > 0) videoView.seekTo(savedPositionMs)
            videoView.start()
        }
        videoView.setOnErrorListener { _, _, _ ->
            loading.visibility = View.GONE
            poster.visibility = View.VISIBLE
            Toast.makeText(
                this,
                "视频加载失败，请确认 ${BasicLessons.currentVideoBase()} 可达且文件已部署",
                Toast.LENGTH_LONG,
            ).show()
            true
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::videoView.isInitialized) {
            savedPositionMs = videoView.currentPosition
            videoView.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (this::videoView.isInitialized && savedPositionMs > 0 && !videoView.isPlaying) {
            videoView.seekTo(savedPositionMs)
        }
    }

    override fun onDestroy() {
        if (this::videoView.isInitialized) {
            videoView.stopPlayback()
        }
        super.onDestroy()
    }

    private fun formatDur(sec: Int): String {
        val m = sec / 60
        val s = sec % 60
        return "%d:%02d".format(m, s)
    }
}
