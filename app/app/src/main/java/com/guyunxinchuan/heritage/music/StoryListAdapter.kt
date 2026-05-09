package com.guyunxinchuan.heritage.music

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.guyunxinchuan.heritage.music.databinding.ItemStoryBinding

class StoryListAdapter(
    private var stories: List<StoryListActivity.Story>,
    private val onActionClick: (StoryListActivity.Story, Action) -> Unit
) : RecyclerView.Adapter<StoryListAdapter.StoryViewHolder>() {
    
    enum class Action {
        VIEW_DETAIL,    // 查看详情
        LIKE,           // 点赞
        COMMENT,        // 评论
        SHARE,          // 分享
        SAVE            // 收藏
    }
    
    class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        val cover get() = binding.coverImageView

        fun bind(story: StoryListActivity.Story, onActionClick: (StoryListActivity.Story, Action) -> Unit) {
            binding.storyTitleTextView.text = story.title
            binding.viewCountTextView.text = story.readCount.toString()
            binding.categoryTagTextView.text = story.tags.firstOrNull() ?: story.category

            binding.coverImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            binding.coverImageView.post {
                val w = binding.coverImageView.width
                if (w > 0) {
                    val hw = if (story.coverWidth > 0 && story.coverHeight > 0) {
                        story.coverHeight.toFloat() / story.coverWidth.toFloat()
                    } else {
                        9f / 16f
                    }
                    val newH = (w * hw).toInt()
                    val lp = binding.coverImageView.layoutParams
                    if (lp.height != newH) {
                        lp.height = newH
                        binding.coverImageView.layoutParams = lp
                        binding.root.requestLayout()
                    }
                }
            }
            binding.coverImageView.loadCover(story.coverResId, CoverPreset.Card)

            binding.root.setOnClickListener {
                onActionClick(story, Action.VIEW_DETAIL)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(stories[position], onActionClick)
    }

    override fun onViewRecycled(holder: StoryViewHolder) {
        holder.cover.cancelCoverLoad()
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int = stories.size

    fun updateStories(newStories: List<StoryListActivity.Story>) {
        stories = newStories
        notifyDataSetChanged()
    }
}
