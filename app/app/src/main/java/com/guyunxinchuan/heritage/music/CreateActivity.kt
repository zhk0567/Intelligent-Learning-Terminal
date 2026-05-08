package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CreateActivity : BaseActivity() {

    private lateinit var tagString: TextView
    private lateinit var tagElectronic: TextView
    private lateinit var tagAI: TextView
    private lateinit var tagWind: TextView
    private lateinit var tagPercussion: TextView
    private lateinit var postsRecyclerView: RecyclerView

    private var currentCategory = "弦乐"

    private val stringPosts = listOf(
        Post(1, "古筝与现代弦乐的融合创作", "尝试将传统古筝技法与现代弦乐编曲结合，创作了一首融合风格的作品", "弦乐", "音乐探索者"),
        Post(2, "二胡演奏技巧分享", "分享一些二胡演奏的基本技巧和练习方法，适合初学者参考", "弦乐", "传统乐手"),
        Post(3, "琵琶曲《春江花月夜》改编", "对经典琵琶曲进行现代改编，加入了更多情感表达元素", "弦乐", "古典创新者")
    )

    private val electronicPosts = listOf(
        Post(4, "电子音乐中的非遗元素运用", "探讨如何在电子音乐创作中融入非遗音乐元素，创造独特风格", "电子", "电音制作人"),
        Post(5, "合成器音色设计教程", "分享使用合成器创造独特音色的方法和技巧", "电子", "音色设计师"),
        Post(6, "电子乐现场演出经验", "分享电子乐现场演出的准备工作和舞台表现技巧", "电子", "现场DJ")
    )

    private val aiPosts = listOf(
        Post(7, "AI音乐生成工具使用心得", "分享使用AI工具生成音乐的经验和创作流程优化建议", "AI", "科技音乐人"),
        Post(8, "人工智能辅助作曲实践", "探讨AI在音乐创作中的应用，以及人机协作的可能性", "AI", "未来音乐家"),
        Post(9, "AI音乐版权问题探讨", "讨论AI生成音乐的版权归属和法律问题", "AI", "法律音乐人")
    )

    private val windPosts = listOf(
        Post(10, "笛箫合奏层次设计", "记录笛子与箫在同一编曲中的频段分配与旋律互补技巧", "管乐", "山水吹奏者"),
        Post(11, "唢呐舞台混音心得", "分享唢呐在现场扩声中的动态控制和穿透力处理经验", "管乐", "民乐舞台人")
    )

    private val percussionPosts = listOf(
        Post(12, "非遗鼓点节奏重组", "将传统鼓点拆解重组到现代编曲中，形成更强推进感", "打击乐", "节奏匠人"),
        Post(13, "木鱼与电子打击融合", "尝试木鱼音色与电子鼓采样叠加，构建东方律动", "打击乐", "跨界节拍师")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        MallWindowInsets.applyToActivity(
            this,
            findViewById(R.id.create_root),
            findViewById(R.id.create_scroll),
            findViewById(R.id.bottom_navigation),
            findViewById(R.id.create_scroll),
            12f,
        )

        tagString = findViewById(R.id.tagString)
        tagElectronic = findViewById(R.id.tagElectronic)
        tagAI = findViewById(R.id.tagAI)
        tagWind = findViewById(R.id.tagWind)
        tagPercussion = findViewById(R.id.tagPercussion)
        postsRecyclerView = findViewById(R.id.postsRecyclerView)
        postsRecyclerView.isNestedScrollingEnabled = false

        findViewById<TextView>(R.id.challengeLink).setOnClickListener {
            startActivity(Intent(this, ChallengeActivity::class.java))
        }

        findViewById<FloatingActionButton>(R.id.fabPublishWork).setOnClickListener {
            startActivity(Intent(this, PublishWorkActivity::class.java))
        }

        tagString.setOnClickListener { switchCategory("弦乐") }
        tagElectronic.setOnClickListener { switchCategory("电子") }
        tagAI.setOnClickListener { switchCategory("AI") }
        tagWind.setOnClickListener { switchCategory("管乐") }
        tagPercussion.setOnClickListener { switchCategory("打击乐") }

        switchCategory("弦乐")

        val bottomNavigation = findViewById<android.widget.LinearLayout>(R.id.bottom_navigation)
        BottomNavigationManager(this, "CreateActivity").setupNavigation(bottomNavigation)
    }

    override fun onResume() {
        super.onResume()
        switchCategory(currentCategory)
    }

    private fun switchCategory(category: String) {
        currentCategory = category
        updateTagStyles()

        val posts = when (category) {
            "弦乐" -> UserPublishedContent.postsForCategory(category, stringPosts)
            "电子" -> UserPublishedContent.postsForCategory(category, electronicPosts)
            "AI" -> UserPublishedContent.postsForCategory(category, aiPosts)
            "管乐" -> UserPublishedContent.postsForCategory(category, windPosts)
            "打击乐" -> UserPublishedContent.postsForCategory(category, percussionPosts)
            else -> UserPublishedContent.postsForCategory(category, stringPosts)
        }

        postsRecyclerView.adapter = PostAdapter(posts) { post ->
            val st = PostEngagementStore.stateForPost(post)
            startActivity(Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_POST_ID, post.id)
                putExtra("post_title", post.title)
                putExtra("post_content", post.content)
                putExtra("post_category", post.category)
                putExtra("post_author", post.author)
                putExtra("like_count", st.likeCount)
                putExtra(DetailActivity.EXTRA_COMMENT_COUNT, st.commentCount)
                putExtra(DetailActivity.EXTRA_LIKED, st.liked)
                putExtra(DetailActivity.EXTRA_FAVORITED, st.favorited)
            })
        }
    }

    private fun updateTagStyles() {
        val onLight = ContextCompat.getColor(this, R.color.text_on_light_primary)
        val muted = ContextCompat.getColor(this, R.color.shop_text_muted)
        tagString.apply {
            setTextColor(if (currentCategory == "弦乐") onLight else muted)
            setBackgroundResource(if (currentCategory == "弦乐") R.drawable.create_tag_selected else R.drawable.create_tag_normal)
        }
        tagElectronic.apply {
            setTextColor(if (currentCategory == "电子") onLight else muted)
            setBackgroundResource(if (currentCategory == "电子") R.drawable.create_tag_selected else R.drawable.create_tag_normal)
        }
        tagAI.apply {
            setTextColor(if (currentCategory == "AI") onLight else muted)
            setBackgroundResource(if (currentCategory == "AI") R.drawable.create_tag_selected else R.drawable.create_tag_normal)
        }
        tagWind.apply {
            setTextColor(if (currentCategory == "管乐") onLight else muted)
            setBackgroundResource(if (currentCategory == "管乐") R.drawable.create_tag_selected else R.drawable.create_tag_normal)
        }
        tagPercussion.apply {
            setTextColor(if (currentCategory == "打击乐") onLight else muted)
            setBackgroundResource(if (currentCategory == "打击乐") R.drawable.create_tag_selected else R.drawable.create_tag_normal)
        }
    }
}
