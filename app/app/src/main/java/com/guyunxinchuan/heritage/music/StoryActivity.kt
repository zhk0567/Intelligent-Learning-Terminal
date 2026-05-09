package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class StoryActivity : BaseActivity() {

    private lateinit var storyRv: RecyclerView

    private val storyTiles = listOf(
        R.color.story_mock_tile_1,
        R.color.story_mock_tile_2,
        R.color.story_mock_tile_3,
        R.color.story_mock_tile_4,
        R.color.story_mock_tile_5,
        R.color.story_mock_tile_6,
        R.color.story_mock_tile_7,
        R.color.story_mock_tile_8,
    )

    /** 与 `data/故事` 同步的非遗专题稿（`StoriesData`）；统一展示全部条目。 */
    private val allStories: List<Story> =
        StoriesData.ALL.mapIndexed { i, e -> gridStoryFromEntry(e, i) }

    private fun gridStoryFromEntry(e: StoryEntry, index: Int): Story {
        val hw = if (e.coverWidth > 0 && e.coverHeight > 0) {
            e.coverHeight.toFloat() / e.coverWidth.toFloat()
        } else {
            9f / 16f
        }
        return Story(
            id = e.id,
            title = e.title,
            author = e.author,
            category = e.category,
            placeholderColorRes = storyTiles[index % storyTiles.size],
            aspectRatio = hw,
            imageResId = -1,
            content = e.excerpt,
            publishTime = e.publishTime,
            coverWidth = e.coverWidth,
            coverHeight = e.coverHeight,
            coverResId = e.coverResId,
            likeCount = e.likeCount,
            commentCount = e.commentCount,
            readCount = e.readCount,
            tags = e.tags,
        )
    }

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

        storyRv = findViewById(R.id.storyRecyclerView)

        // RecyclerView 用 2列瀑布流；不在列间挪 item，保证每列宽度稳定、图片竖边对齐
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        }
        storyRv.layoutManager = layoutManager
        storyRv.setHasFixedSize(false)

        val columnGap = resources.getDimensionPixelSize(R.dimen.story_grid_column_gap)
        val rowGap = resources.getDimensionPixelSize(R.dimen.story_grid_row_gap)
        storyRv.addItemDecoration(StaggeredGridSpacingDecoration(2, columnGap, rowGap))

        // 设置数据
        storyRv.adapter = StoryAdapter(this, allStories) { story -> openDetail(story) }

        // 底部导航栏
        val bottomNav = findViewById<android.widget.LinearLayout>(R.id.bottom_navigation)
        BottomNavigationManager(this, "StoryActivity").setupNavigation(bottomNav)
    }

    private fun openDetail(story: Story) {
        val idStr = story.id.toString()
        val entry = StoriesData.ALL.find { it.id == idStr }
        val body = entry?.body ?: story.content
        val summary = entry?.excerpt
        val intent = Intent(this, StoryDetailActivity::class.java).apply {
            putExtra("story_id", idStr)
            putExtra("story_title", story.title)
            putExtra("story_author", story.author)
            putExtra("story_time", story.publishTime)
            putExtra("story_content", body)
            putExtra("story_summary", summary)
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
