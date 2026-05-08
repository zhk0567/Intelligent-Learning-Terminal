package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.guyunxinchuan.heritage.music.databinding.ActivityWorkDetailBinding

class WorkDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkDetailBinding
    private var isLiked = false
    private var isFavorited = false
    private var likeCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWorkDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MallWindowInsets.applyRootOnly(this, binding.root, 0f)

        setupViews()
        loadWorkInfo()
    }

    private fun setupViews() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.likeButton.setOnClickListener {
            toggleLike()
        }

        binding.favoriteButton.setOnClickListener {
            toggleFavorite()
        }

        binding.shareButton.setOnClickListener {
            shareWork()
        }

        binding.commentButton.setOnClickListener {
            UiFeedback.toast(this, "打开评论")
        }

        binding.creatorInfoCard.setOnClickListener {
            val intent = Intent(this, CreatorProfileActivity::class.java)
            startActivity(intent)
        }

        binding.playButton.setOnClickListener {
            togglePlay()
        }
    }

    private fun loadWorkInfo() {
        val title = intent.getStringExtra("work_title") ?: "作品标题"
        val description = intent.getStringExtra("work_description") ?: "作品介绍"
        val author = intent.getStringExtra("work_author") ?: "创作者"
        val category = intent.getStringExtra("work_category") ?: "分类"

        binding.workTitle.text = title
        binding.workDescription.text = description
        binding.creatorName.text = author
        binding.workCategory.text = category
        binding.likeCount.text = "128"
        likeCount = 128
        updateLikeUi()
        updateFavoriteUi()
    }

    private fun toggleLike() {
        isLiked = !isLiked
        likeCount += if (isLiked) 1 else -1
        if (likeCount < 0) likeCount = 0
        binding.likeCount.text = likeCount.toString()
        updateLikeUi()
        UiFeedback.toast(
            this,
            if (isLiked) "已点赞" else "已取消点赞"
        )
    }

    private fun updateLikeUi() {
        if (isLiked) {
            binding.likeButton.setImageResource(R.drawable.ic_post_like_filled)
            ImageViewCompat.setImageTintList(binding.likeButton, null)
        } else {
            binding.likeButton.setImageResource(R.drawable.ic_post_like_outline)
            ImageViewCompat.setImageTintList(binding.likeButton, null)
        }
    }

    private fun toggleFavorite() {
        isFavorited = !isFavorited
        updateFavoriteUi()
        UiFeedback.toast(
            this,
            if (isFavorited) "已收藏" else "已取消收藏"
        )
    }

    private fun updateFavoriteUi() {
        binding.favoriteButton.setImageResource(R.drawable.ic_save)
        ImageViewCompat.setImageTintList(
            binding.favoriteButton,
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    this,
                    if (isFavorited) R.color.neon_teal else R.color.text_secondary
                )
            )
        )
    }

    private fun shareWork() {
        val title = binding.workTitle.text.toString()
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "来看看这个作品：$title")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "分享"))
    }

    private fun togglePlay() {
        UiFeedback.toast(this, "播放/暂停")
    }
}
