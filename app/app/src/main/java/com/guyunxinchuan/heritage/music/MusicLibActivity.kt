package com.guyunxinchuan.heritage.music

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.NfcAdapter.ACTION_NDEF_DISCOVERED
import android.nfc.NfcAdapter.ACTION_TAG_DISCOVERED
import android.nfc.NfcAdapter.ACTION_TECH_DISCOVERED
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class MusicLibActivity : AppCompatActivity() {

    // NFC
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var nfcPendingIntent: PendingIntent
    private lateinit var nfcIntentFilters: Array<IntentFilter>

    // 三个标签
    private lateinit var tag1: TextView
    private lateinit var tag2: TextView
    private lateinit var tag3: TextView

    // 三个内容容器
    private lateinit var contentTag1: LinearLayout
    private lateinit var contentTag2: LinearLayout
    private lateinit var contentTag3: LinearLayout

    // 轮播相关
    private lateinit var bannerViewPager: ViewPager2
    private lateinit var dot1: View
    private lateinit var dot2: View
    private lateinit var dot3: View

    // 轮播图片资源
    /** 与小程序/Web `homeSwiper*`、`home_swiper_*.jpg` 一致，仅乐库首页轮播。 */
    private val bannerImages = listOf(
        R.drawable.home_swiper_1,
        R.drawable.home_swiper_2,
        R.drawable.home_swiper_3,
    )
    private var miniPlayHaloAnimator: AnimatorSet? = null
    private val miniProgressHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private val musicBannerHandler = Handler(Looper.getMainLooper())
    private val musicBannerAdvanceRunnable = Runnable {
        if (!::bannerViewPager.isInitialized) return@Runnable
        val adapter = bannerViewPager.adapter ?: return@Runnable
        if (adapter.itemCount <= 1) return@Runnable
        bannerViewPager.setCurrentItem(bannerViewPager.currentItem + 1, true)
    }
    private var miniProgressLine: View? = null
    private var miniTitleText: TextView? = null
    private var miniArtistText: TextView? = null
    private var miniCoverImage: ImageView? = null
    private var miniPlayPauseButton: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_music_lib)

        MallWindowInsets.applyToActivity(
            this,
            findViewById(R.id.music_lib_root),
            findViewById(R.id.scrollView),
            findViewById(R.id.bottom_navigation),
            findViewById(R.id.scrollView),
            8f,
        )

        // 检查是否需要显示游客模式提示
        if (intent.getBooleanExtra("show_guest_hint", false)) {
            showGuestModeHint()
        }

        // 初始化控件
        tag1 = findViewById(R.id.tag1)
        tag2 = findViewById(R.id.tag2)
        tag3 = findViewById(R.id.tag3)

        contentTag1 = findViewById(R.id.content_tag1)
        contentTag2 = findViewById(R.id.content_tag2)
        contentTag3 = findViewById(R.id.content_tag3)

        // 轮播初始化
        bannerViewPager = findViewById(R.id.banner_viewpager)
        dot1 = findViewById(R.id.dot1)
        dot2 = findViewById(R.id.dot2)
        dot3 = findViewById(R.id.dot3)

        // 设置轮播适配器（循环滑动：首尾可衔接）
        bannerViewPager.adapter = BannerAdapter()
        bannerViewPager.offscreenPageLimit = 1
        val start = bannerLoopStartIndex()
        bannerViewPager.setCurrentItem(start, false)

        bannerViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDots(position % bannerImages.size)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                when (state) {
                    ViewPager2.SCROLL_STATE_DRAGGING -> cancelMusicBannerAutoScroll()
                    ViewPager2.SCROLL_STATE_IDLE -> {
                        normalizeMusicBannerPosition()
                        scheduleMusicBannerAutoScroll()
                    }
                }
            }
        })
        scheduleMusicBannerAutoScroll()

        // 页面加载时，默认显示非遗跨界
        switchTag(2)

        // 标签点击
        tag1.setOnClickListener { switchTag(1) }
        tag2.setOnClickListener { switchTag(2) }
        tag3.setOnClickListener { switchTag(3) }

        // 搜索框
        findViewById<LinearLayout>(R.id.search_bar).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        // 跳转详情
        val toDetail = Intent(this, DetailActivity::class.java)
        val toPlayer = Intent(this, PlayerActivity::class.java)
        findViewById<TextView>(R.id.arrow_select).setOnClickListener { startActivity(toDetail) }
        findViewById<TextView>(R.id.arrow_like).setOnClickListener { startActivity(toDetail) }

        // 每日热门：与 `data/音频/每日热门` 曲目一致（PlayerSyncState 索引 5–8）
        bindDailyHotCells(toPlayer)
        // 每日精选：与 `data/音频/每日精选` 曲目一致（索引 9–11）
        bindDailySelectCells(toPlayer)
        // 猜你喜欢：与 `data/音频/猜你喜欢` 曲目一致（索引 12–14），三个标签 → 三首
        bindDailyGuessCells(toPlayer)
        // 基础学习：BASIC_LESSONS（视频课）→ LessonActivity
        bindBasicLessonCells()
        setupMiniPlayer(toPlayer)

        // 初始化底部导航栏
        val bottomNavigation = findViewById<android.widget.LinearLayout>(R.id.bottom_navigation)
        val navigationManager = BottomNavigationManager(this, "MusicLibActivity")
        navigationManager.setupNavigation(bottomNavigation)

        // 初始化NFC
        setupNfc()
    }

    // ── NFC 初始化 ────────────────────────────────────────────────────

    private fun setupNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) return  // 设备不支持NFC，跳过

        // 前台调度：app 在前台时优先拦截 NFC 事件
        val intent = Intent(this, MusicLibActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        nfcPendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        // 监听写有本app MIME 类型的NDEF 标签
        val ndefFilter = IntentFilter(ACTION_NDEF_DISCOVERED).apply {
            addDataType("application/com.guyunxinchuan.heritage.music")
        }
        nfcIntentFilters = arrayOf(ndefFilter)
    }

    override fun onResume() {
        super.onResume()
        // 开启前台调度，app 在前台时优先响应 NFC
        nfcAdapter?.enableForegroundDispatch(
            this, nfcPendingIntent, nfcIntentFilters, null
        )
        refreshMiniPlayerFromSync()
        scheduleMusicBannerAutoScroll()
    }

    override fun onPause() {
        super.onPause()
        // 离开前台时关闭前台调度
        nfcAdapter?.disableForegroundDispatch(this)
        stopMiniProgressUpdater()
        cancelMusicBannerAutoScroll()
    }

    private fun scheduleMusicBannerAutoScroll() {
        musicBannerHandler.removeCallbacks(musicBannerAdvanceRunnable)
        if (!::bannerViewPager.isInitialized) return
        val adapter = bannerViewPager.adapter ?: return
        if (adapter.itemCount <= 1) return
        musicBannerHandler.postDelayed(musicBannerAdvanceRunnable, MUSIC_BANNER_INTERVAL_MS)
    }

    private fun cancelMusicBannerAutoScroll() {
        musicBannerHandler.removeCallbacks(musicBannerAdvanceRunnable)
    }

    /**
     * NFC 碰卡触发（app 已在前台时走这里）
     * app 未启动时直接进入onCreate，intent 已包含NFC 数据
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val action = intent.action
        if (action == ACTION_NDEF_DISCOVERED
            || action == ACTION_TECH_DISCOVERED
            || action == ACTION_TAG_DISCOVERED) {
            UiFeedback.toast(this, "欢迎体验非遗音乐")
            // 已在主页，无需额外跳转；如需跳转特定页可在此处理
        }
    }

    // 切换标签
    private fun switchTag(index: Int) {
        // 重置所有标签为未选中：深色底 + 青绿文字
        tag1.setBackgroundResource(R.drawable.tag_bg_normal)
        tag1.setTextColor(0xFF2EC4B6.toInt())
        tag2.setBackgroundResource(R.drawable.tag_bg_normal)
        tag2.setTextColor(0xFF2EC4B6.toInt())
        tag3.setBackgroundResource(R.drawable.tag_bg_normal)
        tag3.setTextColor(0xFF2EC4B6.toInt())

        contentTag1.visibility = View.GONE
        contentTag2.visibility = View.GONE
        contentTag3.visibility = View.GONE

        when (index) {
            1 -> {
                tag1.setBackgroundResource(R.drawable.tag_bg_selected)
                tag1.setTextColor(0xFF121A2B.toInt())
                contentTag1.visibility = View.VISIBLE
            }
            2 -> {
                tag2.setBackgroundResource(R.drawable.tag_bg_selected)
                tag2.setTextColor(0xFF121A2B.toInt())
                contentTag2.visibility = View.VISIBLE
            }
            3 -> {
                tag3.setBackgroundResource(R.drawable.tag_bg_selected)
                tag3.setTextColor(0xFF121A2B.toInt())
                contentTag3.visibility = View.VISIBLE
            }
        }
    }

    // 更新圆点
    private fun updateDots(position: Int) {
        val i = (position % bannerImages.size + bannerImages.size) % bannerImages.size
        dot1.setBackgroundResource(if (i == 0) R.drawable.dot_selected_bg else R.drawable.dot_normal_bg)
        dot2.setBackgroundResource(if (i == 1) R.drawable.dot_selected_bg else R.drawable.dot_normal_bg)
        dot3.setBackgroundResource(if (i == 2) R.drawable.dot_selected_bg else R.drawable.dot_normal_bg)
    }

    private fun bannerLoopStartIndex(): Int {
        val n = bannerImages.size
        if (n == 0) return 0
        val mid = (n * BANNER_LOOP_FACTOR) / 2
        return mid - (mid % n)
    }

    private fun normalizeMusicBannerPosition() {
        val adapter = bannerViewPager.adapter as? BannerAdapter ?: return
        val n = adapter.itemCount
        if (n <= 0) return
        val p = bannerViewPager.currentItem
        val buf = bannerImages.size * 20
        when {
            p < buf -> bannerViewPager.setCurrentItem(p + bannerImages.size * 80, false)
            p >= n - buf -> bannerViewPager.setCurrentItem(p - bannerImages.size * 80, false)
        }
    }

    companion object {
        private const val BANNER_LOOP_FACTOR = 240
        private const val MUSIC_BANNER_INTERVAL_MS = 4000L
    }

    private fun setupMiniPlayer(toPlayer: Intent) {
        findViewById<View>(R.id.miniPlayerContainer).setOnClickListener {
            startActivity(toPlayer)
        }

        val playPauseButton = findViewById<ImageButton>(R.id.miniPlayPauseButton)
        val prevButton = findViewById<ImageButton>(R.id.miniPrevButton)
        val nextButton = findViewById<ImageButton>(R.id.miniNextButton)
        val playlistButton = findViewById<ImageButton>(R.id.miniPlaylistButton)
        val coverLoadingRing = findViewById<View>(R.id.miniCoverLoadingRing)
        miniProgressLine = findViewById(R.id.miniProgressLine)
        miniTitleText = findViewById(R.id.miniSongTitle)
        miniArtistText = findViewById(R.id.miniSongArtist)
        miniCoverImage = findViewById(R.id.miniCoverImage)
        miniPlayPauseButton = playPauseButton
        refreshMiniPlayerFromSync()

        setupMiniControlTouchFeedback(prevButton)
        setupMiniControlTouchFeedback(nextButton)
        setupMiniControlTouchFeedback(playlistButton)

        // 按需展示封面加载态动画（默认隐藏）
        setMiniCoverLoading(false, coverLoadingRing)

        prevButton.setOnClickListenerThrottled {
            val wasPlaying = PlayerSyncState.isPlaying
            PlayerSyncState.previousTrack()
            PlayerPlaybackBridge.onTrackChanged(this, wasPlaying) {
                refreshMiniPlayerFromSync()
                if (PlayerSyncState.isPlaying) {
                    startMiniPlayHalo()
                    startMiniProgressUpdater(playPauseButton)
                }
            }
            refreshMiniPlayerFromSync()
            UiFeedback.toast(this, "上一首")
        }
        playPauseButton.setOnClickListener {
            PlayerPlaybackBridge.togglePlayPause(this) {
                playPauseButton.setImageResource(R.drawable.ic_mini_pause)
                startMiniPlayHalo()
                startMiniProgressUpdater(playPauseButton)
                refreshMiniPlayerFromSync()
            }
            playPauseButton.setImageResource(
                if (PlayerSyncState.isPlaying) R.drawable.ic_mini_pause
                else R.drawable.ic_mini_play
            )
            if (PlayerSyncState.isPlaying) {
                startMiniPlayHalo()
                startMiniProgressUpdater(playPauseButton)
            } else {
                stopMiniPlayHalo()
                stopMiniProgressUpdater()
            }
            refreshMiniPlayerFromSync()
        }
        nextButton.setOnClickListenerThrottled {
            val wasPlaying = PlayerSyncState.isPlaying
            PlayerSyncState.nextTrack(shuffle = false)
            PlayerPlaybackBridge.onTrackChanged(this, wasPlaying) {
                refreshMiniPlayerFromSync()
                if (PlayerSyncState.isPlaying) {
                    startMiniPlayHalo()
                    startMiniProgressUpdater(playPauseButton)
                }
            }
            refreshMiniPlayerFromSync()
            UiFeedback.toast(this, "下一首")
        }
        playlistButton.setOnClickListenerThrottled {
            UiFeedback.toast(this, "播放列表")
        }

    }

    private fun setupMiniControlTouchFeedback(button: ImageButton) {
        button.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start()
                    button.imageTintList = android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.neon_cyan)
                    )
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    button.imageTintList = android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.ancient_gold)
                    )
                }
            }
            false
        }
    }

    private fun startMiniPlayHalo() {
        val halo = findViewById<View>(R.id.miniPlayHalo)
        halo.visibility = View.VISIBLE
        miniPlayHaloAnimator?.cancel()

        val alphaAnimator = ObjectAnimator.ofFloat(halo, View.ALPHA, 0.06f, 0.2f, 0.06f).apply {
            duration = 1800
            repeatCount = ObjectAnimator.INFINITE
        }
        val scaleXAnimator = ObjectAnimator.ofFloat(halo, View.SCALE_X, 0.92f, 1.08f, 0.92f).apply {
            duration = 1800
            repeatCount = ObjectAnimator.INFINITE
        }
        val scaleYAnimator = ObjectAnimator.ofFloat(halo, View.SCALE_Y, 0.92f, 1.08f, 0.92f).apply {
            duration = 1800
            repeatCount = ObjectAnimator.INFINITE
        }

        miniPlayHaloAnimator = AnimatorSet().apply {
            playTogether(alphaAnimator, scaleXAnimator, scaleYAnimator)
            start()
        }
    }

    private fun stopMiniPlayHalo() {
        miniPlayHaloAnimator?.cancel()
        miniPlayHaloAnimator = null
        findViewById<View>(R.id.miniPlayHalo).apply {
            visibility = View.GONE
            alpha = 1f
            scaleX = 1f
            scaleY = 1f
        }
    }

    private fun setMiniCoverLoading(isLoading: Boolean, loadingRing: View) {
        if (isLoading) {
            loadingRing.visibility = View.VISIBLE
            loadingRing.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.mini_cover_ring_rotate)
            )
        } else {
            loadingRing.clearAnimation()
            loadingRing.visibility = View.GONE
        }
    }

    private fun startMiniProgressUpdater(playPauseButton: ImageButton) {
        miniProgressHandler.removeCallbacksAndMessages(null)
        miniProgressHandler.post(object : Runnable {
            override fun run() {
                if (!PlayerSyncState.isPlaying) return
                PlayerSyncState.syncFromRealtime()
                updateMiniProgressLine()

                if (PlayerSyncState.currentPositionMs >= PlayerSyncState.trackDurationMs) {
                    PlayerSyncState.updatePlayingState(false)
                    playPauseButton.setImageResource(R.drawable.ic_mini_play)
                    stopMiniPlayHalo()
                    return
                }
                miniProgressHandler.postDelayed(this, 80L)
            }
        })
    }

    private fun stopMiniProgressUpdater() {
        miniProgressHandler.removeCallbacksAndMessages(null)
    }

    private fun updateMiniProgressLine() {
        PlayerSyncState.syncFromRealtime()
        val progress = (PlayerSyncState.currentPositionMs.toFloat() /
                PlayerSyncState.trackDurationMs.toFloat()).coerceIn(0f, 1f)
        miniProgressLine?.scaleX = progress
    }

    private fun refreshMiniPlayerFromSync() {
        val track = PlayerSyncState.currentTrack()
        miniTitleText?.text = track.title
        miniArtistText?.text = track.artist
        miniCoverImage?.loadCoverRemoteOrDrawable(track.coverRemoteUrl, track.coverResId, CoverPreset.Thumb)
        miniPlayPauseButton?.setImageResource(
            if (PlayerSyncState.isPlaying) R.drawable.ic_mini_pause else R.drawable.ic_mini_play
        )
        updateMiniProgressLine()
        if (PlayerSyncState.isPlaying) {
            startMiniPlayHalo()
            miniPlayPauseButton?.let { startMiniProgressUpdater(it) }
        } else {
            stopMiniPlayHalo()
            stopMiniProgressUpdater()
        }
    }

    private fun bindBasicLessonCells() {
        data class LearnCell(val lessonIndex: Int, val rootId: Int, val coverId: Int, val titleId: Int, val descId: Int)
        val cells = listOf(
            LearnCell(0, R.id.learn1, R.id.learn1_cover, R.id.learn1_title, R.id.learn1_desc),
            LearnCell(1, R.id.learn2, R.id.learn2_cover, R.id.learn2_title, R.id.learn2_desc),
        )
        cells.forEach { c ->
            val lesson = BasicLessons.ALL.getOrNull(c.lessonIndex) ?: return@forEach
            findViewById<ImageView>(c.coverId).loadCoverRemoteOrDrawable(
                StaticRemoteAssets.basicLessonCover(c.lessonIndex),
                lesson.coverResId,
                CoverPreset.Card,
            )
            findViewById<TextView>(c.titleId).text = lesson.title
            findViewById<TextView>(c.descId).text = "${lesson.artist} · ${lesson.desc}"
            findViewById<LinearLayout>(c.rootId).setOnClickListener {
                startActivity(
                    Intent(this, LessonActivity::class.java)
                        .putExtra(LessonActivity.EXTRA_LESSON_ID, lesson.id),
                )
            }
        }
    }

    private fun bindDailyGuessCells(toPlayer: Intent) {
        data class GuessCell(val trackIndex: Int, val layoutId: Int)
        val cells = listOf(
            GuessCell(12, R.id.list1_item1),
            GuessCell(13, R.id.list2_item1),
            GuessCell(14, R.id.list3_item1),
        )
        cells.forEach { c ->
            findViewById<LinearLayout>(c.layoutId).setOnClickListener {
                val dur = PlayerSyncState.tracks[c.trackIndex].durationMs
                PlayerSyncState.setTrack(c.trackIndex, dur)
                startActivity(toPlayer)
            }
        }
    }

    private fun bindDailySelectCells(toPlayer: Intent) {
        data class SelectCell(val title: String, val subtitle: String, val trackIndex: Int, val layoutId: Int)
        val cells = listOf(
            SelectCell("杨家将", "河南坠子", 9, R.id.select1),
            SelectCell("杨府挑将", "濮阳大弦戏", 10, R.id.select2),
            SelectCell("湖畔枫吟", "古琴", 11, R.id.select3),
        )
        cells.forEach { c ->
            findViewById<LinearLayout>(c.layoutId).setOnClickListener {
                val dur = PlayerSyncState.tracks[c.trackIndex].durationMs
                PlayerSyncState.setTrack(c.trackIndex, dur)
                startActivity(toPlayer)
            }
        }
    }

    private fun bindDailyHotCells(toPlayer: Intent) {
        // 首页「每日热门」仅展示曲名，乐器/流派不再作为副标题展示。
        data class HotCell(val title: String, val trackIndex: Int)
        val cells = listOf(
            HotCell("哑女告状", 5),
            HotCell("抬花轿", 6),
            HotCell("美美与共", 7),
            HotCell("醉美玉见", 8),
        )
        val titleIds = listOf(R.id.hot1_title, R.id.hot2_title, R.id.hot3_title, R.id.hot4_title)
        val hotIds = listOf(R.id.hot1, R.id.hot2, R.id.hot3, R.id.hot4)
        cells.forEachIndexed { i, c ->
            findViewById<TextView>(titleIds[i]).text = c.title
            findViewById<LinearLayout>(hotIds[i]).setOnClickListener {
                val dur = PlayerSyncState.tracks[c.trackIndex].durationMs
                PlayerSyncState.setTrack(c.trackIndex, dur)
                startActivity(toPlayer)
            }
        }
    }

    override fun onDestroy() {
        cancelMusicBannerAutoScroll()
        stopMiniProgressUpdater()
        miniPlayHaloAnimator?.cancel()
        super.onDestroy()
    }
    
    /**
     * 显示游客模式提示条
     * 功能：在页面顶部显示3秒提示，自动淡入淡出
     */
    private fun showGuestModeHint() {
        // 查找布局中的ScrollView
        val scrollView = findViewById<android.widget.ScrollView>(R.id.scrollView)
        if (scrollView == null) {
            // 如果找不到ScrollView，直接返回不显示提示
            return
        }
        
        // 获取ScrollView的第一个子布局（LinearLayout）
        val mainLayout = scrollView.getChildAt(0) as? android.widget.LinearLayout
        if (mainLayout == null) {
            return
        }
        
        // 创建提示View
        val hintView = LayoutInflater.from(this).inflate(R.layout.guest_mode_hint, null)
        val textView = hintView.findViewById<TextView>(R.id.guestModeHint)
        
        // 在第一个位置插入提示条
        mainLayout.addView(textView, 0)
        
        // 显示并设置淡入动画
        textView.visibility = View.VISIBLE
        textView.alpha = 0f
        textView.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
        
        // 3秒后自动淡出并移除
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            textView.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    mainLayout.removeView(textView)
                }
                .start()
        }, 3000)
    }

    // 轮播适配器（多页循环，实现从最后一页继续滑到第一页）
    inner class BannerAdapter : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {
        inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.banner_image)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.banner_item, parent, false)
            return BannerViewHolder(view)
        }

        override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
            val idx = (position % bannerImages.size + bannerImages.size) % bannerImages.size
            holder.imageView.loadCoverRemoteOrDrawable(
                StaticRemoteAssets.homeSwiper(idx),
                bannerImages[idx],
                CoverPreset.Banner,
            )
            holder.itemView.setOnClickListener {
                startActivity(Intent(this@MusicLibActivity, DetailActivity::class.java))
            }
        }

        override fun onViewRecycled(holder: BannerViewHolder) {
            holder.imageView.cancelCoverLoad()
            super.onViewRecycled(holder)
        }

        override fun getItemCount(): Int = bannerImages.size * BANNER_LOOP_FACTOR
    }
}
