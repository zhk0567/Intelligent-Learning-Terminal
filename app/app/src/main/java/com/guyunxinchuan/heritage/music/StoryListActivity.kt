package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.guyunxinchuan.heritage.music.databinding.ActivityStoryListBinding

class StoryListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryListBinding
    private lateinit var storyAdapter: StoryListAdapter

    /** 全量数据（点赞等会写回此处，便于筛选后仍保留状态） */
    private lateinit var catalog: MutableList<Story>

    /** 当前列表展示（搜索 / 分类 / 排序后的结果） */
    private val stories = mutableListOf<Story>()

    private var searchQuery: String = ""
    private var filterCategory: String? = null
    private var sortMode: SortMode = SortMode.TIME_DESC

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 主题里 statusBar 透明，此处关闭系统默认内容避让，由根布局 insets 统一留白，避免顶栏与状态栏重叠
        WindowCompat.setDecorFitsSystemWindows(window, false)
        @Suppress("DEPRECATION")
        window.statusBarColor = ContextCompat.getColor(this, R.color.bg_primary)

        binding = ActivityStoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = bars.top,
                left = bars.left,
                right = bars.right,
                bottom = bars.bottom
            )
            insets
        }

        val source = intent.getStringExtra(EXTRA_LIST_SOURCE) ?: SOURCE_DEFAULT
        catalog = when (source) {
            SOURCE_MURAL_CURATED -> buildMuralStories()
            else -> buildDefaultStories()
        }

        val title = intent.getStringExtra(EXTRA_SECTION_TITLE)?.trim().orEmpty()
            .ifEmpty { getString(R.string.story_list_default_title) }
        binding.titleTextView.text = title

        val subtitle = intent.getStringExtra(EXTRA_SECTION_SUBTITLE)?.trim().orEmpty()
        if (subtitle.isNotEmpty()) {
            binding.sectionSubtitleTextView.visibility = View.VISIBLE
            binding.sectionSubtitleTextView.text = subtitle
        } else {
            binding.sectionSubtitleTextView.visibility = View.GONE
        }

        supportActionBar?.hide()
        setupViews()
        setupRecyclerView()
        rebuildDisplayedList()
    }

    private fun setupViews() {
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.searchButton.setOnClickListener { showSearchDialog() }
        binding.filterButton.setOnClickListener { showFilterDialog() }
        binding.sortButton.setOnClickListener { showSortDialog() }
        binding.refreshButton.setOnClickListener { refreshStories() }
        binding.writeStoryButton.setOnClickListener { writeStory() }
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryListAdapter(stories) { story, action ->
            when (action) {
                StoryListAdapter.Action.VIEW_DETAIL -> viewStoryDetail(story)
                StoryListAdapter.Action.LIKE -> toggleLike(story)
                StoryListAdapter.Action.COMMENT -> commentStory(story)
                StoryListAdapter.Action.SHARE -> shareStory(story)
                StoryListAdapter.Action.SAVE -> saveStory(story)
            }
        }

        binding.storyRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@StoryListActivity)
            adapter = storyAdapter
            setHasFixedSize(true)
        }
    }

    private fun rebuildDisplayedList() {
        var list = catalog.toList()
        filterCategory?.let { cat ->
            list = list.filter { it.category == cat }
        }
        if (searchQuery.isNotBlank()) {
            list = list.filter { storyMatchesQuery(it, searchQuery) }
        }
        list = sortStories(list, sortMode)
        stories.clear()
        stories.addAll(list)
        storyAdapter.updateStories(stories)
        updateStoryCount()
        updateEmptyState()
        if (stories.isNotEmpty()) {
            binding.storyRecyclerView.scrollToPosition(0)
        }
    }

    private fun storyMatchesQuery(story: Story, q: String): Boolean {
        if (q.isBlank()) return true
        return story.title.contains(q, ignoreCase = true) ||
            story.author.contains(q, ignoreCase = true) ||
            story.content.contains(q, ignoreCase = true) ||
            story.category.contains(q, ignoreCase = true) ||
            story.tags.any { it.contains(q, ignoreCase = true) }
    }

    private fun sortStories(list: List<Story>, mode: SortMode): List<Story> {
        return when (mode) {
            SortMode.TIME_DESC -> list.sortedByDescending { it.publishTime }
            SortMode.TIME_ASC -> list.sortedBy { it.publishTime }
            SortMode.READ_DESC -> list.sortedByDescending { it.readCount }
            SortMode.LIKE_DESC -> list.sortedByDescending { it.likeCount }
            SortMode.TITLE_ASC -> list.sortedBy { it.title }
        }
    }

    private fun syncStoryToCatalog(updated: Story) {
        val c = catalog.indexOfFirst { it.id == updated.id }
        if (c >= 0) catalog[c] = updated
    }

    private fun showSearchDialog() {
        val density = resources.displayMetrics.density
        val padH = (20 * density).toInt()
        val padV = (8 * density).toInt()
        val edit = EditText(this).apply {
            hint = getString(R.string.story_list_search_hint)
            setText(searchQuery)
            setSingleLine(true)
        }
        val frame = FrameLayout(this).apply {
            setPadding(padH, padV, padH, 0)
            addView(
                edit,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.story_list_search_title)
            .setView(frame)
            .setNeutralButton(R.string.story_list_search_clear_btn) { _, _ ->
                searchQuery = ""
                edit.setText("")
                rebuildDisplayedList()
                UiFeedback.toast(this, getString(R.string.story_list_search_cleared))
            }
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                searchQuery = edit.text.toString().trim()
                rebuildDisplayedList()
                UiFeedback.toast(
                    this,
                    if (searchQuery.isEmpty()) getString(R.string.story_list_search_cleared)
                    else getString(R.string.story_list_search_applied)
                )
            }
            .show()
    }

    private fun showFilterDialog() {
        val allLabel = getString(R.string.story_list_filter_all)
        val categories = listOf(allLabel) + catalog.map { it.category }.distinct().sorted()
        val checkedIndex = when (val f = filterCategory) {
            null -> 0
            else -> categories.indexOf(f).coerceAtLeast(0)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.story_list_filter_title)
            .setSingleChoiceItems(categories.toTypedArray(), checkedIndex) { dialog, which ->
                filterCategory = if (which == 0) null else categories[which]
                rebuildDisplayedList()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showSortDialog() {
        val modes = SortMode.values()
        val labels = modes.map { mode ->
            when (mode) {
                SortMode.TIME_DESC -> getString(R.string.story_list_sort_time_desc)
                SortMode.TIME_ASC -> getString(R.string.story_list_sort_time_asc)
                SortMode.READ_DESC -> getString(R.string.story_list_sort_read_desc)
                SortMode.LIKE_DESC -> getString(R.string.story_list_sort_like_desc)
                SortMode.TITLE_ASC -> getString(R.string.story_list_sort_title_az)
            }
        }.toTypedArray()
        val selected = modes.indexOf(sortMode).coerceIn(0, modes.size - 1)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.story_list_sort_title)
            .setSingleChoiceItems(labels, selected) { dialog, which ->
                sortMode = modes[which]
                rebuildDisplayedList()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun viewStoryDetail(story: Story) {
        val entry = StoriesData.ALL.find { it.id == story.id }
        val excerpt = entry?.excerpt?.trim().orEmpty()
        startActivity(Intent(this, StoryDetailActivity::class.java).apply {
            putExtra(StoryDetailPayload.Extras.ID, story.id)
            putExtra(StoryDetailPayload.Extras.TITLE, story.title)
            putExtra(StoryDetailPayload.Extras.AUTHOR, story.author)
            putExtra(StoryDetailPayload.Extras.PUBLISHED_AT, story.publishTime)
            putExtra(StoryDetailPayload.Extras.BODY, story.content)
            if (excerpt.isNotEmpty() && excerpt != story.content.trim()) {
                putExtra(StoryDetailPayload.Extras.SUMMARY, excerpt)
            }
            putExtra(StoryDetailPayload.Extras.COVER, story.coverResId)
            putExtra(StoryDetailPayload.Extras.CATEGORY, story.category)
            putExtra(StoryDetailPayload.Extras.TAGS, story.tags.joinToString("|||"))
            putExtra(StoryDetailPayload.Extras.READ_COUNT, story.readCount)
            putExtra(StoryDetailPayload.Extras.LIKE_COUNT, story.likeCount)
            putExtra(StoryDetailPayload.Extras.COMMENT_COUNT, story.commentCount)
        })
    }

    private fun toggleLike(story: Story) {
        val idx = stories.indexOfFirst { it.id == story.id }
        if (idx == -1) return
        val cur = stories[idx]
        val likedNow = !cur.isLiked
        val updated = cur.copy(
            isLiked = likedNow,
            likeCount = if (likedNow) cur.likeCount + 1 else (cur.likeCount - 1).coerceAtLeast(0)
        )
        stories[idx] = updated
        syncStoryToCatalog(updated)
        storyAdapter.notifyItemChanged(idx)
        UiFeedback.toast(this, if (likedNow) "已点赞" else "已取消点赞")
    }

    private fun commentStory(story: Story) {
        UiFeedback.toast(this, "评论功能开发中")
    }

    private fun shareStory(story: Story) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "分享故事：${story.title}")
            putExtra(
                Intent.EXTRA_TEXT,
                "我正在阅读《${story.title}》，作者：${story.author}，推荐给你！"
            )
        }
        try {
            startActivity(Intent.createChooser(shareIntent, "分享故事"))
        } catch (_: Exception) {
            UiFeedback.toast(this, "分享失败")
        }
    }

    private fun saveStory(story: Story) {
        val idx = stories.indexOfFirst { it.id == story.id }
        if (idx == -1) return
        val cur = stories[idx]
        val savedNow = !cur.isSaved
        val updated = cur.copy(isSaved = savedNow)
        stories[idx] = updated
        syncStoryToCatalog(updated)
        storyAdapter.notifyItemChanged(idx)
        UiFeedback.toast(this, if (savedNow) "已收藏" else "已取消收藏")
    }

    private fun refreshStories() {
        searchQuery = ""
        filterCategory = null
        sortMode = SortMode.TIME_DESC
        rebuildDisplayedList()
        UiFeedback.toast(this, getString(R.string.story_list_reset_list))
    }

    private fun writeStory() {
        UiFeedback.toast(this, "写故事功能开发中")
    }

    private fun updateStoryCount() {
        binding.storyCountTextView.text = getString(R.string.story_list_count_format, stories.size)
    }

    private fun updateEmptyState() {
        if (stories.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.storyContentLayout.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.storyContentLayout.visibility = View.VISIBLE
        }
    }

    private enum class SortMode {
        TIME_DESC,
        TIME_ASC,
        READ_DESC,
        LIKE_DESC,
        TITLE_ASC
    }

    data class Story(
        val id: String,
        val title: String,
        val author: String,
        val publishTime: String,
        val content: String,
        val coverWidth: Int = 0,
        val coverHeight: Int = 0,
        val coverResId: Int,
        val coverRemoteUrl: String? = null,
        var likeCount: Int,
        var commentCount: Int,
        var readCount: Int,
        val tags: List<String>,
        val category: String = "传统音乐",
        var isLiked: Boolean = false,
        var isSaved: Boolean = false
    )

    companion object {
        const val EXTRA_SECTION_TITLE = "section_title"
        const val EXTRA_SECTION_SUBTITLE = "section_subtitle"
        const val EXTRA_LIST_SOURCE = "list_source"
        const val SOURCE_DEFAULT = "default"
        const val SOURCE_MURAL_CURATED = "mural_curated"
    }
}

private fun buildDefaultStories(): MutableList<StoryListActivity.Story> =
    StoriesData.ALL.map { e ->
        StoryListActivity.Story(
            id = e.id,
            title = e.title,
            author = e.author,
            publishTime = e.publishTime,
            content = e.body,
            coverWidth = e.coverWidth,
            coverHeight = e.coverHeight,
            coverResId = e.coverResId,
            coverRemoteUrl = StaticRemoteAssets.storyCover(e.key),
            likeCount = e.likeCount,
            commentCount = e.commentCount,
            readCount = e.readCount,
            tags = e.tags,
            category = e.category,
        )
    }.toMutableList()

private fun buildMuralStories(): MutableList<StoryListActivity.Story> = mutableListOf(
    StoryListActivity.Story(
        id = "m1",
        title = "敦煌壁画中的乐队编制",
        author = "石窟音乐档案",
        publishTime = "2024-05-01",
        content = "莫高窟壁画所见乐伎、乐队排列与乐器组合，为研究唐五代宫廷与寺院用乐提供了图像与空间线索。",
        coverResId = R.drawable.banner2_img,
        coverRemoteUrl = StaticRemoteAssets.remoteBannerMatchingLocal(R.drawable.banner2_img),
        likeCount = 342,
        commentCount = 58,
        readCount = 4102,
        tags = listOf("敦煌", "壁画", "乐队"),
        category = "壁画音乐"
    ),
    StoryListActivity.Story(
        id = "m2",
        title = "乐伎飞天与「不鼓自鸣」",
        author = "图像与声音实验室",
        publishTime = "2024-04-28",
        content = "飞天持乐器、璎珞飘举的图像传统，与经变画中「不鼓自鸣」的声学想象，共同构成壁画音乐的隐喻系统。",
        coverResId = R.drawable.p_1,
        likeCount = 276,
        commentCount = 41,
        readCount = 3550,
        tags = listOf("飞天", "经变", "想象"),
        category = "壁画音乐"
    ),
    StoryListActivity.Story(
        id = "m3",
        title = "墓室砖画里的宴乐与百戏",
        author = "汉唐物质文化",
        publishTime = "2024-04-22",
        content = "汉唐墓室砖刻、壁画常见宴乐、百戏场景，反映当时社会音声娱乐与礼俗用乐的并行结构。",
        coverResId = R.drawable.p_3,
        likeCount = 198,
        commentCount = 33,
        readCount = 2890,
        tags = listOf("墓室", "宴乐", "百戏"),
        category = "壁画音乐"
    ),
    StoryListActivity.Story(
        id = "m4",
        title = "西域乐器东传：从琵琶到阮咸",
        author = "丝路乐史",
        publishTime = "2024-04-18",
        content = "曲项琵琶等西域乐器沿丝路进入中原，在壁画与文献中留下清晰的形制演变与演奏姿态记录。",
        coverResId = R.drawable.p_4,
        likeCount = 245,
        commentCount = 47,
        readCount = 3012,
        tags = listOf("丝路", "琵琶", "东传"),
        category = "壁画音乐"
    ),
    StoryListActivity.Story(
        id = "m5",
        title = "永乐宫壁画与道教仪式用乐",
        author = "宗教艺术研究",
        publishTime = "2024-04-12",
        content = "永乐宫三清殿壁画中的朝元仙仗与仪仗乐队，可与道教斋醮科仪文献对照，理解仪式空间中的音声秩序。",
        coverResId = R.drawable.p_5,
        likeCount = 167,
        commentCount = 26,
        readCount = 2144,
        tags = listOf("永乐宫", "道教", "仪式"),
        category = "壁画音乐"
    ),
    StoryListActivity.Story(
        id = "m6",
        title = "克孜尔石窟的早期乐舞图像",
        author = "西域考古笔记",
        publishTime = "2024-04-06",
        content = "龟兹地区石窟早期壁画中的乐舞与乐器图像，为理解佛教东传过程中的音声文化层积提供关键样本。",
        coverResId = R.drawable.p_6,
        likeCount = 189,
        commentCount = 29,
        readCount = 2566,
        tags = listOf("龟兹", "石窟", "早期"),
        category = "壁画音乐"
    ),
    StoryListActivity.Story(
        id = "m7",
        title = "从图像到声场：壁画音乐的当代聆听",
        author = "声景策展人",
        publishTime = "2024-03-30",
        content = "当代创作者以声学测量、空间音频与即兴演奏，尝试「翻译」壁画中的无声乐舞，形成可感知的展览声景。",
        coverResId = R.drawable.banner3_img,
        coverRemoteUrl = StaticRemoteAssets.remoteBannerMatchingLocal(R.drawable.banner3_img),
        likeCount = 223,
        commentCount = 52,
        readCount = 2788,
        tags = listOf("声景", "当代", "展览"),
        category = "壁画音乐"
    )
)
