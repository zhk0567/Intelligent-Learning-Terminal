package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.ImageViewCompat
import com.guyunxinchuan.heritage.music.databinding.ActivityStoryDetailBinding
import com.google.android.material.appbar.AppBarLayout
import java.util.Locale

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding
    private lateinit var payload: StoryDetailPayload

    private var likeCount = 0
    private var liked = false
    private var saved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 使用系统默认内容区（状态栏下），避免 edge-to-edge + 手动 padding 与 AppBar 叠加出现顶部黑带
        WindowCompat.setDecorFitsSystemWindows(window, true)

        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 保证底栏在 NestedScrollView 之上，避免滑动区域盖住点击
        binding.storyDetailRoot.bringChildToFront(binding.bottomActionBar)
        val bgPrimary = ContextCompat.getColor(this, R.color.bg_primary)
        @Suppress("DEPRECATION")
        window.statusBarColor = bgPrimary
        @Suppress("DEPRECATION")
        window.navigationBarColor = bgPrimary

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomActionBar) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = bars.bottom)
            insets
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.navigationContentDescription = getString(R.string.cd_back)

        setupAppBarScrollCrossfade()
        setupViews()
        payload = StoryDetailPayload.fromIntent(intent)
        bindPayload(payload)
    }

    /** 展开时导航与标题偏亮（浮在头图上），收起后与页面主色统一 */
    private fun setupAppBarScrollCrossfade() {
        val teal = ContextCompat.getColor(this, R.color.neon_teal)
        val titleCollapsed = ContextCompat.getColor(this, R.color.text_primary)
        val listener = AppBarLayout.OnOffsetChangedListener { appBar, verticalOffset ->
            val range = appBar.totalScrollRange
            val p = if (range == 0) 1f else (-verticalOffset.toFloat() / range).coerceIn(0f, 1f)
            val navTint = ColorUtils.blendARGB(Color.WHITE, teal, p)
            val titleTint = ColorUtils.blendARGB(Color.WHITE, titleCollapsed, p)
            binding.toolbar.navigationIcon?.mutate()?.setTint(navTint)
            binding.toolbar.setTitleTextColor(titleTint)
        }
        binding.appBarLayout.addOnOffsetChangedListener(listener)
    }

    private fun setupViews() {
        binding.likeButton.setOnClickListenerThrottled { toggleLike() }
        binding.shareButton.setOnClickListenerThrottled { shareStory() }
        binding.saveButton.setOnClickListenerThrottled { toggleSave() }
        // 数字区域与按钮同效，避免用户只点到文案以为无响应
        binding.storyBarLikeCount.setOnClickListenerThrottled { toggleLike() }
    }

    private fun bindPayload(p: StoryDetailPayload) {
        likeCount = p.likeCount
        liked = false
        saved = false

        adjustHeroHeight(p)
        binding.storyTitleTextView.text = p.title.ifBlank { getString(R.string.story_detail_toolbar_title) }
        binding.storyAuthorTextView.text = p.author.ifBlank { "—" }
        binding.storyContentTextView.text =
            p.body.ifBlank { getString(R.string.story_detail_empty_content) }
        binding.coverImageView.loadCoverRemoteOrDrawable(p.coverRemoteUrl, p.coverResId, CoverPreset.Hero)
        binding.categoryTagTextView.text =
            p.category.ifBlank { getString(R.string.story_detail_default_category) }
        binding.heroFrameContainer.bringChildToFront(binding.categoryTagTextView)

        updateEngagementBarCounts()

        val summary = p.summary?.trim().orEmpty()
        if (summary.isNotEmpty() && summary != p.body.trim()) {
            binding.summarySectionLabel.visibility = View.VISIBLE
            binding.storySummaryTextView.visibility = View.VISIBLE
            binding.storySummaryTextView.text = summary
        } else {
            binding.summarySectionLabel.visibility = View.GONE
            binding.storySummaryTextView.visibility = View.GONE
        }

        bindMetaRows(p)
        populateTags(p)
        updateLikeUi()
        updateSaveUi()
    }

    /**
     * 根据封面素材比例动态计算头图高度，避免固定高度导致裁切过重。
     * 远程封面无像素尺寸时优先用目录里的 [StoryDetailPayload.coverWidth]/[StoryDetailPayload.coverHeight]，否则回退 16:9。
     */
    private fun adjustHeroHeight(p: StoryDetailPayload) {
        val fallbackRatio = 16f / 9f
        val ratio = when {
            p.coverWidth > 0 && p.coverHeight > 0 ->
                p.coverWidth.toFloat() / p.coverHeight.toFloat()
            p.coverRemoteUrl.isNullOrBlank() -> {
                val drawable = ContextCompat.getDrawable(this, p.coverResId)
                val w = drawable?.intrinsicWidth ?: 0
                val h = drawable?.intrinsicHeight ?: 0
                if (w > 0 && h > 0) w.toFloat() / h.toFloat() else fallbackRatio
            }
            else -> fallbackRatio
        }

        val screenWidth = resources.displayMetrics.widthPixels
        val rawHeight = (screenWidth / ratio).toInt()
        val minHeight = dpToPx(220)
        val maxHeight = dpToPx(420)
        val target = rawHeight.coerceIn(minHeight, maxHeight)

        val lp = binding.collapsingToolbar.layoutParams
        if (lp.height != target) {
            lp.height = target
            binding.collapsingToolbar.layoutParams = lp
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun bindMetaRows(p: StoryDetailPayload) {
        binding.metaInfoContainer.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val rows = listOf(
            R.string.story_meta_label_category to p.category,
            R.string.story_meta_label_author to p.author,
        ).filter { it.second.isNotBlank() }

        if (rows.isEmpty()) {
            val hint = TextView(this).apply {
                text = "—"
                setTextColor(ContextCompat.getColor(this@StoryDetailActivity, R.color.text_hint))
                textSize = 13f
            }
            binding.metaInfoContainer.addView(hint)
            return
        }

        for ((labelRes, value) in rows) {
            val row = inflater.inflate(R.layout.item_story_detail_meta_row, binding.metaInfoContainer, false)
            row.findViewById<TextView>(R.id.metaLabel).text = getString(labelRes)
            row.findViewById<TextView>(R.id.metaValue).text = value
            binding.metaInfoContainer.addView(row)
        }
    }

    private fun populateTags(p: StoryDetailPayload) {
        binding.tagContainer.removeAllViews()
        val padH = resources.getDimensionPixelSize(R.dimen.spacing_small)
        val padV = resources.getDimensionPixelSize(R.dimen.spacing_ultra_small)
        val gap = resources.getDimensionPixelSize(R.dimen.spacing_small)

        val cat = p.category.trim()
        val fromApi = p.tags.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
        val withoutDupCategory = fromApi.filter { it != cat }
        val all = if (withoutDupCategory.isNotEmpty()) {
            withoutDupCategory
        } else {
            listOf(cat).filter { it.isNotEmpty() }.distinct()
        }

        if (all.isEmpty()) {
            val hint = TextView(this).apply {
                text = getString(R.string.story_detail_no_tags)
                setTextColor(ContextCompat.getColor(this@StoryDetailActivity, R.color.text_hint))
                textSize = 13f
            }
            binding.tagContainer.addView(hint)
            return
        }

        for (label in all) {
            val tv = TextView(this).apply {
                text = label
                setTextColor(ContextCompat.getColor(this@StoryDetailActivity, R.color.text_secondary))
                textSize = 12f
                setPadding(padH, padV, padH, padV)
                setBackgroundResource(R.drawable.create_tag_normal)
            }
            val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.marginEnd = gap
            binding.tagContainer.addView(tv, lp)
        }
    }

    private fun formatCount(n: Int): String = when {
        n >= 10_000 -> String.format(Locale.CHINA, "%.1f万", n / 10_000f)
        n >= 1000 -> String.format(Locale.CHINA, "%.1fk", n / 1000f)
        else -> n.toString()
    }

    private fun updateEngagementBarCounts() {
        binding.storyBarLikeCount.text = formatCount(likeCount)
    }

    private fun toggleLike() {
        liked = !liked
        likeCount += if (liked) 1 else -1
        if (likeCount < 0) likeCount = 0
        updateEngagementBarCounts()
        updateLikeUi()
        UiFeedback.toast(
            this,
            if (liked) getString(R.string.story_liked_toast) else getString(R.string.story_unliked_toast)
        )
    }

    private fun updateLikeUi() {
        if (liked) {
            binding.likeButton.setImageResource(R.drawable.ic_post_like_filled)
            ImageViewCompat.setImageTintList(binding.likeButton, null)
        } else {
            binding.likeButton.setImageResource(R.drawable.ic_post_like_outline)
            ImageViewCompat.setImageTintList(binding.likeButton, null)
        }
    }

    private fun updateSaveUi() {
        binding.saveButton.setImageResource(R.drawable.ic_save)
        ImageViewCompat.setImageTintList(
            binding.saveButton,
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    this,
                    if (saved) R.color.neon_teal else R.color.text_hint
                )
            )
        )
    }

    private fun shareStory() {
        val title = payload.title.ifBlank { getString(R.string.story_detail_toolbar_title) }
        val text = payload.body.ifBlank { binding.storyContentTextView.text.toString() }.take(280)
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "$title\n\n$text")
        }
        startActivity(Intent.createChooser(send, getString(R.string.story_action_share)))
    }

    private fun toggleSave() {
        saved = !saved
        updateSaveUi()
        UiFeedback.toast(
            this,
            if (saved) getString(R.string.story_subscribe_added) else getString(R.string.story_subscribe_removed)
        )
    }
}
