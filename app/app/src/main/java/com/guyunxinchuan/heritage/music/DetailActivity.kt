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
            UiFeedback.toast(this, "???+1????")
        }
    }

    private fun loadContent() {
        val title = intent.getStringExtra("title") ?: "??"
        val contentTitle = intent.getStringExtra("content_title")
            ?: intent.getStringExtra("post_title")
            ?: "??????"
        val contentText = intent.getStringExtra("content_text")
            ?: intent.getStringExtra("post_content")
            ?: "??????????"
        val metaInfo = intent.getStringExtra("meta_info")
            ?: intent.getStringExtra("post_author")?.let { author -> "$author � ??" }
            ?: "?? � 2???"
        val imageUrl = intent.getStringExtra("image_url")
        val imageResId = intent.getIntExtra("image_res_id", R.drawable.banner1_img)
        val likeCount = intent.getIntExtra("like_count", 0)
        val commentFromIntent = intent.getIntExtra(EXTRA_COMMENT_COUNT, 0)
        val actionButtonText = intent.getStringExtra("action_button_text") ?: "????"

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
            if (liked) "???" else "?????"
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
            if (favorited) "???" else "?????"
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
            startActivity(Intent.createChooser(shareIntent, "????"))
        } catch (e: Exception) {
            UiFeedback.toast(this, "????")
        }
    }

    private fun showMoreOptions() {
        val options = arrayOf("??", "????", "????")
        android.app.AlertDialog.Builder(this)
            .setTitle("????")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> UiFeedback.toast(this, "???????")
                    1 -> UiFeedback.toast(this, "????????")
                    2 -> {
                        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("??", "https://example.com/detail")
                        clipboard.setPrimaryClip(clip)
                        UiFeedback.toast(this, "?????")
                    }
                }
            }
            .show()
    }

    private fun performMainAction() {
        val actionText = binding.btnAction.text.toString()
        // 旧数据中存在多条相同字面量的分支（编码丢失后均退化成 "????"），保留通用提示即可。
        UiFeedback.toast(this, "??: $actionText")
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
