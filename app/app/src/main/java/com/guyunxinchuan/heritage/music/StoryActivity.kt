package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class StoryActivity : BaseActivity() {

    private lateinit var tabRecommend: TextView
    private lateinit var tabFollow: TextView
    private lateinit var recommendRv: RecyclerView
    private lateinit var followRv: RecyclerView

    private val recommendStories = listOf(
        Story(1,  "烟火弦音",       "古筝艺术家",   "传统音乐", R.color.story_mock_tile_1, 1.50f, -1, "古筝，又称瑶琴、玉琴、七弦琴，是中国传统拨弦乐器，有三千年以上历史。", "", null, System.currentTimeMillis(), listOf("传统音乐", "古筝"), "2024-04-22", R.drawable.p_1, 128, 36, 1024),
        Story(2,  "仕女弄乐",       "宫廷雅乐",     "宫廷音乐", R.color.story_mock_tile_2, 0.80f, -1, "宫廷雅乐是中国古代宫廷中使用的音乐，具有严格的礼仪性和等级性。", "", null, System.currentTimeMillis(), listOf("宫廷音乐", "历史"), "2024-04-21", R.drawable.p_2, 96, 24, 856),
        Story(3,  "敦煌乐舞",       "敦煌文化研究", "敦煌文化", R.color.story_mock_tile_3, 1.25f, -1, "敦煌乐舞是敦煌壁画中描绘的音乐和舞蹈，反映了古代丝绸之路上的文化交流。", "", null, System.currentTimeMillis(), listOf("敦煌文化", "舞蹈"), "2024-04-20", R.drawable.p_3, 156, 42, 1234),
        Story(4,  "古建回响",       "建筑音乐家",   "建筑音乐", R.color.story_mock_tile_4, 1.60f, -1, "古建回响是研究古代建筑声学的学科，探索建筑与音乐的关系。", "", null, System.currentTimeMillis(), listOf("建筑音乐", "声学"), "2024-04-19", R.drawable.p_4, 89, 18, 678),
        Story(5,  "竹韵江南",       "笛子演奏家",   "民间音乐", R.color.story_mock_tile_5, 0.70f, -1, "竹韵江南是江南地区的民间音乐，以笛子为主要乐器，表现江南水乡的柔美。", "", null, System.currentTimeMillis(), listOf("民间音乐", "笛子"), "2024-04-18", R.drawable.p_5, 112, 28, 945),
        Story(6,  "水墨音韵",       "书法音乐家",   "书法音乐", R.color.story_mock_tile_6, 1.10f, -1, "水墨音韵是将书法与音乐相结合的艺术形式，通过书法的节奏和音乐的韵律表达情感。", "", null, System.currentTimeMillis(), listOf("书法音乐", "艺术"), "2024-04-17", R.drawable.p_6, 134, 32, 1102),
        Story(7,  "丝路琴音",       "丝路学者",     "丝路文化", R.color.story_mock_tile_7, 0.90f, -1, "丝路琴音是丝绸之路上的音乐文化，融合了东西方音乐元素。", "", null, System.currentTimeMillis(), listOf("丝路文化", "音乐"), "2024-04-16", R.drawable.p_7, 105, 26, 892),
        Story(8,  "编钟回响",       "礼乐研究者",   "礼乐文化", R.color.story_mock_tile_8, 1.35f, -1, "编钟是中国古代重要的礼乐器，编钟回响研究古代礼乐文化的发展。", "", null, System.currentTimeMillis(), listOf("礼乐文化", "编钟"), "2024-04-15", R.drawable.p_1, 142, 38, 1176)
    )

    private val followStories = listOf(
        Story(9,  "非遗传承人的日常记录", "非遗传承人", "非遗文化", R.color.story_mock_tile_1, 1.20f, -1, "记录非遗传承人的日常工作和生活，展示非遗文化的传承过程。", "", null, System.currentTimeMillis(), listOf("非遗文化", "传承"), "2024-04-14", R.drawable.p_2, 167, 45, 1324),
        Story(10, "传统乐器制作全过程",   "乐器制作师", "传统工艺", R.color.story_mock_tile_2, 0.75f, -1, "展示传统乐器的制作过程，从选材到成品的完整流程。", "", null, System.currentTimeMillis(), listOf("传统工艺", "乐器制作"), "2024-04-13", R.drawable.p_3, 123, 30, 987),
        Story(11, "民间音乐采风纪实",     "音乐采风者", "民间音乐", R.color.story_mock_tile_3, 1.45f, -1, "记录民间音乐采风的过程，收集和整理民间音乐资料。", "", null, System.currentTimeMillis(), listOf("民间音乐", "采风"), "2024-04-12", R.drawable.p_4, 154, 39, 1205),
        Story(12, "古乐谱修复工作日志",   "古乐谱专家", "古籍修复", R.color.story_mock_tile_4, 0.85f, -1, "记录古乐谱修复的工作过程，保护和传承古代音乐文化。", "", null, System.currentTimeMillis(), listOf("古籍修复", "音乐"), "2024-04-11", R.drawable.p_5, 98, 22, 765),
        Story(13, "苗族芦笙节现场记录",   "民族音乐家", "民族音乐", R.color.story_mock_tile_5, 1.30f, -1, "记录苗族芦笙节的现场盛况，展示苗族音乐文化的魅力。", "", null, System.currentTimeMillis(), listOf("民族音乐", "苗族"), "2024-04-10", R.drawable.p_6, 145, 36, 1123),
        Story(14, "纳西古乐",             "纳西族传人", "民族音乐", R.color.story_mock_tile_6, 1.00f, -1, "纳西古乐是纳西族的传统音乐，被誉为'音乐活化石'。", "", null, System.currentTimeMillis(), listOf("民族音乐", "纳西族"), "2024-04-09", R.drawable.p_7, 138, 34, 1089)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_story)

        MallWindowInsets.applyToActivity(
            this,
            findViewById(R.id.story_root),
            findViewById(R.id.story_root),
            findViewById(R.id.bottom_navigation),
            findViewById(R.id.story_rv_container),
            12f,
        )

        tabRecommend = findViewById(R.id.tabRecommend)
        tabFollow    = findViewById(R.id.tabFollow)
        recommendRv  = findViewById(R.id.recommendRecyclerView)
        followRv     = findViewById(R.id.followRecyclerView)

        // 两个 RecyclerView 都用 2列瀑布流；不在列间挪 item，保证每列宽度稳定、图片竖边对齐
        val recommendLm = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        }
        val followLm = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        }
        recommendRv.layoutManager = recommendLm
        followRv.layoutManager = followLm
        recommendRv.setHasFixedSize(false)
        followRv.setHasFixedSize(false)

        val columnGap = resources.getDimensionPixelSize(R.dimen.story_grid_column_gap)
        val rowGap = resources.getDimensionPixelSize(R.dimen.story_grid_row_gap)
        recommendRv.addItemDecoration(StaggeredGridSpacingDecoration(2, columnGap, rowGap))
        followRv.addItemDecoration(StaggeredGridSpacingDecoration(2, columnGap, rowGap))

        // 设置数据
        recommendRv.adapter = StoryAdapter(this, recommendStories) { story -> openDetail(story) }
        followRv.adapter    = StoryAdapter(this, followStories)    { story -> openDetail(story) }

        // Tab 点击
        tabRecommend.setOnClickListener { switchTab(true) }
        tabFollow.setOnClickListener    { switchTab(false) }

        // 默认显示推荐
        switchTab(true)

        // 底部导航栏
        val bottomNav = findViewById<android.widget.LinearLayout>(R.id.bottom_navigation)
        BottomNavigationManager(this, "StoryActivity").setupNavigation(bottomNav)
    }

    // ── Tab 切换 ──────────────────────────────────────────────────────

    private fun switchTab(isRecommend: Boolean) {
        val accent = ContextCompat.getColor(this, R.color.neon_teal)
        val inactive = ContextCompat.getColor(this, R.color.text_secondary)
        if (isRecommend) {
            // 推荐选中
            tabRecommend.setTextColor(accent)
            tabRecommend.setTypeface(null, Typeface.BOLD)
            tabRecommend.setBackgroundResource(R.drawable.story_tab_selected)
            // 关注未选中
            tabFollow.setTextColor(inactive)
            tabFollow.setTypeface(null, Typeface.NORMAL)
            tabFollow.background = null
            // 切换内容
            recommendRv.visibility = View.VISIBLE
            followRv.visibility    = View.GONE
        } else {
            // 关注选中
            tabFollow.setTextColor(accent)
            tabFollow.setTypeface(null, Typeface.BOLD)
            tabFollow.setBackgroundResource(R.drawable.story_tab_selected)
            // 推荐未选中
            tabRecommend.setTextColor(inactive)
            tabRecommend.setTypeface(null, Typeface.NORMAL)
            tabRecommend.background = null
            // 切换内容
            recommendRv.visibility = View.GONE
            followRv.visibility    = View.VISIBLE
        }
    }

    private fun openDetail(story: Story) {
        val intent = Intent(this, StoryDetailActivity::class.java).apply {
            putExtra("story_id", story.id.toString())
            putExtra("story_title", story.title)
            putExtra("story_author", story.author)
            putExtra("story_time", story.publishTime)
            putExtra("story_content", story.content)
            putExtra("story_cover", story.coverResId)
            putExtra("like_count", story.likeCount)
            putExtra("comment_count", story.commentCount)
            putExtra("read_count", story.readCount)
            putExtra("story_category", story.category)
            putExtra("story_tags", story.tags.joinToString("|||"))
        }
        startActivity(intent)
    }
}
