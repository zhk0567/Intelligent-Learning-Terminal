package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.guyunxinchuan.heritage.music.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var postId: Int? = null
    private var engagement: PostEngagementStore.State? = null

    private var liked = false
    private var favorited = false
    private var likeCountValue = 0
    private var commentCountValue = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MallWindowInsets.applyRootOnly(this, binding.root, 0f)

        setupViews()
        loadContent()
    }

    override fun onPause() {
        syncEngagementFromUi()
        super.onPause()
    }

    private fun setupViews() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnMore.setOnClickListener {
            showMoreOptions()
        }

        binding.likeContainer.setOnClickListener {
            toggleLike()
        }

        binding.favoriteContainer.setOnClickListener {
            toggleFavorite()
        }

        binding.shareContainer.setOnClickListener {
            shareContent()
        }

        binding.btnAction.setOnClickListener {
            performMainAction()
        }

        binding.commentMetaContainer.setOnClickListener {
            commentCountValue += 1
            binding.commentCountMeta.text = commentCountValue.toString()
            syncEngagementFromUi()
            UiFeedback.toast(this, "评论 +1（示例）")
        }
    }

    private fun loadContent() {
        val postTitleExtra = intent.getStringExtra("post_title")
        val postCategoryExtra = intent.getStringExtra("post_category")
        val title = intent.getStringExtra("title")
            ?: postCategoryExtra?.let { "$it · 帖子详情" }
            ?: "帖子详情"
        val contentTitle = intent.getStringExtra("content_title")
            ?: postTitleExtra
            ?: "作品详情"
        val contentText = intent.getStringExtra("content_text")
            ?: intent.getStringExtra("post_content")
            ?: "作者还没有补充更多介绍。"
        val metaInfo = intent.getStringExtra("meta_info")
            ?: intent.getStringExtra("post_author")?.let { author -> "$author · 刚刚发布" }
            ?: "佚名 · 2 小时前"
        val imageResId = intent.getIntExtra("image_res_id", R.drawable.banner1_img)
        val likeCount = intent.getIntExtra("like_count", 0)
        val commentFromIntent = intent.getIntExtra(EXTRA_COMMENT_COUNT, 0)
        val actionButtonText = intent.getStringExtra("action_button_text") ?: "查看作品"

        val rawId = intent.getIntExtra(EXTRA_POST_ID, NO_POST_ID)
        postId = if (rawId != NO_POST_ID) rawId else null

        binding.detailTitle.text = title
        binding.contentTitle.text = contentTitle
        binding.contentText.text = contentText
        binding.metaInfo.text = metaInfo
        binding.btnAction.text = actionButtonText

        if (postId != null) {
            val likedIn = intent.getBooleanExtra(EXTRA_LIKED, false)
            val favIn = intent.getBooleanExtra(EXTRA_FAVORITED, false)
            engagement = PostEngagementStore.stateForDetail(
                postId!!,
                likeCount,
                commentFromIntent,
                likedIn,
                favIn
            )
            liked = engagement!!.liked
            favorited = engagement!!.favorited
            likeCountValue = engagement!!.likeCount
            commentCountValue = engagement!!.commentCount
        } else {
            likeCountValue = likeCount
            commentCountValue = commentFromIntent
            liked = false
            favorited = false
        }

        binding.likeCount.text = likeCountValue.toString()
        binding.commentCountMeta.text = commentCountValue.toString()

        binding.detailImage.loadCover(imageResId, CoverPreset.Hero)

        updateLikeInlineUi()
        updateFavoriteUi()
    }

    private fun syncEngagementFromUi() {
        val e = engagement ?: return
        e.liked = liked
        e.favorited = favorited
        e.likeCount = likeCountValue
        e.commentCount = commentCountValue
    }

    private fun toggleLike() {
        liked = !liked
        likeCountValue += if (liked) 1 else -1
        if (likeCountValue < 0) likeCountValue = 0
        binding.likeCount.text = likeCountValue.toString()
        updateLikeInlineUi()
        syncEngagementFromUi()
        UiFeedback.toast(
            this,
            if (liked) "已点赞" else "已取消点赞"
        )
    }

    private fun updateLikeInlineUi() {
        if (liked) {
            binding.likeInlineIcon.setImageResource(R.drawable.ic_post_like_filled)
            ImageViewCompat.setImageTintList(binding.likeInlineIcon, null)
        } else {
            binding.likeInlineIcon.setImageResource(R.drawable.ic_post_like_outline)
            ImageViewCompat.setImageTintList(binding.likeInlineIcon, null)
        }
    }

    private fun toggleFavorite() {
        favorited = !favorited
        updateFavoriteUi()
        syncEngagementFromUi()
        UiFeedback.toast(
            this,
            if (favorited) "已收藏" else "已取消收藏"
        )
    }

    private fun updateFavoriteUi() {
        ImageViewCompat.setImageTintList(
            binding.favoriteIcon,
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    this,
                    if (favorited) R.color.neon_teal else R.color.text_secondary
                )
            )
        )
    }

    private fun shareContent() {
        val title = binding.contentTitle.text.toString()
        val content = binding.contentText.text.toString()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "$title\n\n$content")
        }

        try {
            startActivity(Intent.createChooser(shareIntent, "分享到"))
        } catch (e: Exception) {
            UiFeedback.toast(this, "分享失败")
        }
    }

    private fun showMoreOptions() {
        val options = arrayOf("举报", "不感兴趣", "复制链接")
        android.app.AlertDialog.Builder(this)
            .setTitle("更多操作")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> UiFeedback.toast(this, "举报已提交，谢谢反馈")
                    1 -> UiFeedback.toast(this, "已减少类似内容推荐")
                    2 -> {
                        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("帖子链接", "https://example.com/detail")
                        clipboard.setPrimaryClip(clip)
                        UiFeedback.toast(this, "链接已复制")
                    }
                }
            }
            .show()
    }

    private fun performMainAction() {
        val actionText = binding.btnAction.text.toString()
        // 旧数据中存在多条相同字面量的分支（编码丢失后均退化成同一占位字符串），保留通用提示即可。
        UiFeedback.toast(this, "操作：$actionText")
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val EXTRA_POST_ID = "post_id"
        const val EXTRA_COMMENT_COUNT = "comment_count"
        const val EXTRA_LIKED = "liked"
        const val EXTRA_FAVORITED = "favorited"
        const val NO_POST_ID = -1
    }
}
