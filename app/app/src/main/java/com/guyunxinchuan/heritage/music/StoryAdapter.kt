package com.guyunxinchuan.heritage.music

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * 故事列表适配器
 * 配合 StaggeredGridLayoutManager(2, VERTICAL) 使用
 */
class StoryAdapter(
    private val context: Context,
    private val stories: List<Story>,
    private val onItemClick: (Story) -> Unit
) : RecyclerView.Adapter<StoryAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView  = view.findViewById(R.id.coverImageView)
        val title: TextView   = view.findViewById(R.id.storyTitleTextView)
        val categoryTag: TextView = view.findViewById(R.id.categoryTagTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(context)
            .inflate(R.layout.item_story, parent, false)
        return VH(v)
    }

    override fun getItemCount() = stories.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val story = stories[position]
        holder.title.text  = story.title
        holder.categoryTag.text = story.category

        // 优先按封面原图像素比（h/w）设高，与 `data/图片/故事` 一致；否则用 aspectRatio
        holder.image.post {
            val width = holder.image.width
            if (width > 0) {
                val lp = holder.image.layoutParams
                val hw = if (story.coverWidth > 0 && story.coverHeight > 0) {
                    story.coverHeight.toFloat() / story.coverWidth.toFloat()
                } else {
                    story.aspectRatio
                }
                val newH = (width * hw).toInt()
                if (lp.height != newH) {
                    lp.height = newH
                    holder.image.layoutParams = lp
                    holder.itemView.requestLayout()
                }
            }
        }

        // 优先使用真实图片资源，否则用纯色渐变占位
        when {
            story.imageResId != -1 -> {
                holder.image.scaleType = ImageView.ScaleType.CENTER_CROP
                holder.image.loadCoverRemoteOrDrawable(
                    StaticRemoteAssets.remoteBannerMatchingLocal(story.imageResId),
                    story.imageResId,
                    CoverPreset.Card,
                )
            }
            story.coverResId != -1 || !story.coverRemoteUrl.isNullOrBlank() -> {
                holder.image.scaleType = ImageView.ScaleType.CENTER_CROP
                val fallback = if (story.coverResId != -1) {
                    story.coverResId
                } else {
                    R.drawable.music_cover_placeholder
                }
                holder.image.loadCoverRemoteOrDrawable(story.coverRemoteUrl, fallback, CoverPreset.Card)
            }
            else -> {
                holder.image.cancelCoverLoad()
                val drawable = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    setColor(ContextCompat.getColor(context, story.placeholderColorRes))
                }
                holder.image.setImageDrawable(drawable)
                holder.image.scaleType = ImageView.ScaleType.FIT_XY
            }
        }

        holder.itemView.setOnClickListener { onItemClick(story) }
    }

    override fun onViewRecycled(holder: VH) {
        holder.image.cancelCoverLoad()
        super.onViewRecycled(holder)
    }
}